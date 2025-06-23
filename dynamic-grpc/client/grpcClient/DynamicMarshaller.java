package grpcClient;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import io.grpc.MethodDescriptor;

import java.io.IOException;
import java.io.InputStream;

class DynamicMarshaller implements MethodDescriptor.Marshaller<DynamicMessage> {
    private final Descriptors.Descriptor descriptor;

    public DynamicMarshaller(Descriptors.Descriptor descriptor) {
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
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse DynamicMessage", e);
        }
    }
}
