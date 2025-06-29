package fantasy;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: fantasy.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FantasySubscriberGrpc {

  private FantasySubscriberGrpc() {}

  public static final java.lang.String SERVICE_NAME = "fantasy.FantasySubscriber";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<fantasy.Fantasy.ControlRequest,
      fantasy.Fantasy.FantasyEvent> getStreamEventsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamEvents",
      requestType = fantasy.Fantasy.ControlRequest.class,
      responseType = fantasy.Fantasy.FantasyEvent.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<fantasy.Fantasy.ControlRequest,
      fantasy.Fantasy.FantasyEvent> getStreamEventsMethod() {
    io.grpc.MethodDescriptor<fantasy.Fantasy.ControlRequest, fantasy.Fantasy.FantasyEvent> getStreamEventsMethod;
    if ((getStreamEventsMethod = FantasySubscriberGrpc.getStreamEventsMethod) == null) {
      synchronized (FantasySubscriberGrpc.class) {
        if ((getStreamEventsMethod = FantasySubscriberGrpc.getStreamEventsMethod) == null) {
          FantasySubscriberGrpc.getStreamEventsMethod = getStreamEventsMethod =
              io.grpc.MethodDescriptor.<fantasy.Fantasy.ControlRequest, fantasy.Fantasy.FantasyEvent>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamEvents"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  fantasy.Fantasy.ControlRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  fantasy.Fantasy.FantasyEvent.getDefaultInstance()))
              .setSchemaDescriptor(new FantasySubscriberMethodDescriptorSupplier("StreamEvents"))
              .build();
        }
      }
    }
    return getStreamEventsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FantasySubscriberStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberStub>() {
        @java.lang.Override
        public FantasySubscriberStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FantasySubscriberStub(channel, callOptions);
        }
      };
    return FantasySubscriberStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static FantasySubscriberBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberBlockingV2Stub>() {
        @java.lang.Override
        public FantasySubscriberBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FantasySubscriberBlockingV2Stub(channel, callOptions);
        }
      };
    return FantasySubscriberBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FantasySubscriberBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberBlockingStub>() {
        @java.lang.Override
        public FantasySubscriberBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FantasySubscriberBlockingStub(channel, callOptions);
        }
      };
    return FantasySubscriberBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FantasySubscriberFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FantasySubscriberFutureStub>() {
        @java.lang.Override
        public FantasySubscriberFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FantasySubscriberFutureStub(channel, callOptions);
        }
      };
    return FantasySubscriberFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<fantasy.Fantasy.ControlRequest> streamEvents(
        io.grpc.stub.StreamObserver<fantasy.Fantasy.FantasyEvent> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getStreamEventsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FantasySubscriber.
   */
  public static abstract class FantasySubscriberImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FantasySubscriberGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FantasySubscriber.
   */
  public static final class FantasySubscriberStub
      extends io.grpc.stub.AbstractAsyncStub<FantasySubscriberStub> {
    private FantasySubscriberStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FantasySubscriberStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FantasySubscriberStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<fantasy.Fantasy.ControlRequest> streamEvents(
        io.grpc.stub.StreamObserver<fantasy.Fantasy.FantasyEvent> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getStreamEventsMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FantasySubscriber.
   */
  public static final class FantasySubscriberBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<FantasySubscriberBlockingV2Stub> {
    private FantasySubscriberBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FantasySubscriberBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FantasySubscriberBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<fantasy.Fantasy.ControlRequest, fantasy.Fantasy.FantasyEvent>
        streamEvents() {
      return io.grpc.stub.ClientCalls.blockingBidiStreamingCall(
          getChannel(), getStreamEventsMethod(), getCallOptions());
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service FantasySubscriber.
   */
  public static final class FantasySubscriberBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FantasySubscriberBlockingStub> {
    private FantasySubscriberBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FantasySubscriberBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FantasySubscriberBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FantasySubscriber.
   */
  public static final class FantasySubscriberFutureStub
      extends io.grpc.stub.AbstractFutureStub<FantasySubscriberFutureStub> {
    private FantasySubscriberFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FantasySubscriberFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FantasySubscriberFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_STREAM_EVENTS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM_EVENTS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.streamEvents(
              (io.grpc.stub.StreamObserver<fantasy.Fantasy.FantasyEvent>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getStreamEventsMethod(),
          io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
            new MethodHandlers<
              fantasy.Fantasy.ControlRequest,
              fantasy.Fantasy.FantasyEvent>(
                service, METHODID_STREAM_EVENTS)))
        .build();
  }

  private static abstract class FantasySubscriberBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FantasySubscriberBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return fantasy.Fantasy.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FantasySubscriber");
    }
  }

  private static final class FantasySubscriberFileDescriptorSupplier
      extends FantasySubscriberBaseDescriptorSupplier {
    FantasySubscriberFileDescriptorSupplier() {}
  }

  private static final class FantasySubscriberMethodDescriptorSupplier
      extends FantasySubscriberBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    FantasySubscriberMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FantasySubscriberGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FantasySubscriberFileDescriptorSupplier())
              .addMethod(getStreamEventsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
