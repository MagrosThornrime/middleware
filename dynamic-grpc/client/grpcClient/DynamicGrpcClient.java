package grpcClient;

import com.google.protobuf.*;
import io.grpc.*;
import io.grpc.stub.ClientCalls;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class StringTask {
    public final String text;
    public final int count;

    public StringTask(String text, int count) {
        this.text = text;
        this.count = count;
    }
}


public class DynamicGrpcClient {

    static String path = "printer.desc";

    static Descriptors.ServiceDescriptor serviceDescriptor;

    static Descriptors.MethodDescriptor stringsDescriptor;
    static Descriptors.MethodDescriptor fibonacciDescriptor;
    static Descriptors.MethodDescriptor rangeDescriptor;

    static Descriptors.Descriptor stringsInputType;
    static Descriptors.Descriptor fibonacciInputType;
    static Descriptors.Descriptor rangeInputType;

    static ManagedChannel channel;

    public static void main(String[] args) throws Exception {
        // Load descriptor set
        FileInputStream fis = new FileInputStream(path);
        DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(fis);
        DescriptorProtos.FileDescriptorProto fileDescriptorProto = descriptorSet.getFile(0);
        Descriptors.FileDescriptor fileDescriptor =
                Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[]{});

        // Get service descriptor
        serviceDescriptor = fileDescriptor.findServiceByName("Printer");

        // Get methods descriptors
        stringsDescriptor = serviceDescriptor.findMethodByName("PrintStrings");
        fibonacciDescriptor = serviceDescriptor.findMethodByName("PrintFibonacci");
        rangeDescriptor = serviceDescriptor.findMethodByName("PrintRange");

        // Get methods inputs descriptors
        stringsInputType = stringsDescriptor.getInputType();
        fibonacciInputType = fibonacciDescriptor.getInputType();
        rangeInputType = rangeDescriptor.getInputType();

        // Set up channel
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // Create arguments
        List<StringTask> stringTasks = List.of(
                new StringTask("Print job A", 4),
                new StringTask("Print job B", 4),
                new StringTask("Print job A", 1)
        );
        var stringsArgs = createStringsArgument(stringTasks);
        var fibonacciArgs = createFibonacciArgument(4);
        var rangeArgs = createRangeArgument(2, 7, 2);


        // Call RPC
        System.out.println("PrintStrings:");
        callServerStreaming(stringsDescriptor, stringsInputType, stringsArgs);
        System.out.println("PrintFibonacci:");
        callServerStreaming(fibonacciDescriptor, fibonacciInputType, fibonacciArgs);
        System.out.println("PrintRange:");
        callServerStreaming(rangeDescriptor, rangeInputType, rangeArgs);

        channel.shutdown();
    }


    private static DynamicMessage createStringsArgument(List<StringTask> tasks) {
        Descriptors.Descriptor inputType = stringsDescriptor.getInputType();
        Descriptors.FieldDescriptor tasksField = inputType.findFieldByName("tasks");
        Descriptors.Descriptor taskDescriptor = tasksField.getMessageType();

        DynamicMessage.Builder requestBuilder = DynamicMessage.newBuilder(inputType);

        for (StringTask job : tasks) {
            DynamicMessage task = DynamicMessage.newBuilder(taskDescriptor)
                    .setField(taskDescriptor.findFieldByName("text"), job.text)
                    .setField(taskDescriptor.findFieldByName("count"), job.count)
                    .build();

            requestBuilder.addRepeatedField(tasksField, task);
        }

        return requestBuilder.build();
    }


    private static DynamicMessage createFibonacciArgument(int number) {
        Descriptors.Descriptor inputType = fibonacciDescriptor.getInputType();

        return DynamicMessage.newBuilder(inputType)
                .setField(inputType.findFieldByName("number"), number)
                .build();
    }

    private static DynamicMessage createRangeArgument(int start, int end, int step) {
        Descriptors.Descriptor inputType = rangeDescriptor.getInputType();

        return DynamicMessage.newBuilder(inputType)
                .setField(inputType.findFieldByName("startNumber"), start)
                .setField(inputType.findFieldByName("endNumber"), end)
                .setField(inputType.findFieldByName("step"), step)
                .build();
    }

    private static MethodDescriptor<DynamicMessage, DynamicMessage> getMethod(Descriptors.MethodDescriptor method,
                                                                              Descriptors.Descriptor inputType,
                                                                              MethodDescriptor.MethodType methodType) {
        return MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                .setType(methodType)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(serviceDescriptor.getFullName(), method.getName()))
                .setRequestMarshaller(new DynamicMarshaller(inputType))
                .setResponseMarshaller(new DynamicMarshaller(method.getOutputType()))
                .build();
    }

    private static void callUnary(
            Descriptors.MethodDescriptor method,
            Descriptors.Descriptor inputType,
            DynamicMessage request
    ) {
        var grpcMethod = getMethod(method, inputType, MethodDescriptor.MethodType.UNARY);

        var result = ClientCalls.blockingUnaryCall(channel, grpcMethod, CallOptions.DEFAULT, request);
        System.out.print("Received message: " + result);
    }

    private static void callServerStreaming(
            Descriptors.MethodDescriptor method,
            Descriptors.Descriptor inputType,
            DynamicMessage request
    ) {
        var grpcMethod = getMethod(method, inputType, MethodDescriptor.MethodType.SERVER_STREAMING);
        Iterator<DynamicMessage> responses = ClientCalls.blockingServerStreamingCall(
                channel, grpcMethod, CallOptions.DEFAULT, request
        );

        List<DynamicMessage> responseList = new ArrayList<>();
        responses.forEachRemaining(responseList::add);

        for (DynamicMessage msg : responseList) {
            System.out.print("Received message: " + msg);
        }

    }

}
