import com.google.protobuf.*;
import io.grpc.*;
import io.grpc.stub.ClientCalls;

import java.io.FileInputStream;
import java.io.InputStream;

public class DynamicGrpcClient {

    public static void main(String[] args) throws Exception {
        // Load descriptor set
        FileInputStream fis = new FileInputStream("echo.desc");
        DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(fis);
        DescriptorProtos.FileDescriptorProto fileDescriptorProto = descriptorSet.getFile(0);
        Descriptors.FileDescriptor fileDescriptor =
                Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[]{});

        // Get service and method descriptors
        Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.findServiceByName("EchoService");
        Descriptors.MethodDescriptor methodDescriptor = serviceDescriptor.findMethodByName("Echo");

        // Create input message dynamically
        Descriptors.Descriptor inputType = methodDescriptor.getInputType();
        DynamicMessage requestMessage = DynamicMessage.newBuilder(inputType)
                .setField(inputType.findFieldByName("message"), "Hello from dynamic client!")
                .build();

        // Set up channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // Create dynamic method descriptor
        MethodDescriptor<DynamicMessage, DynamicMessage> grpcMethod =
                MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                        .setType(MethodDescriptor.MethodType.UNARY)
                        .setFullMethodName(MethodDescriptor.generateFullMethodName(serviceDescriptor.getFullName(), methodDescriptor.getName()))
                        .setRequestMarshaller(new DynamicMarshaller(inputType))
                        .setResponseMarshaller(new DynamicMarshaller(methodDescriptor.getOutputType()))
                        .build();

        // Call RPC
        DynamicMessage response = ClientCalls.blockingUnaryCall(channel, grpcMethod, CallOptions.DEFAULT, requestMessage);
        System.out.println("Response: " + response);

        channel.shutdown();
    }

    static class DynamicMarshaller implements MethodDescriptor.Marshaller<DynamicMessage> {
        private final Descriptors.Descriptor descriptor;

        DynamicMarshaller(Descriptors.Descriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public InputStream stream(DynamicMessage value) {
            return value.toByteString().newInput();
        }

        @Override
        public DynamicMessage parse(InputStream stream) {
            try {
                return DynamicMessage.parseFrom(descriptor, stream);
            } catch (Exception e) {
                throw new RuntimeException("Parsing failed", e);
            }
        }

    }


}
