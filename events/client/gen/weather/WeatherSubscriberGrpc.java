package weather;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: weather.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class WeatherSubscriberGrpc {

  private WeatherSubscriberGrpc() {}

  public static final java.lang.String SERVICE_NAME = "weather.WeatherSubscriber";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<weather.Weather.WeatherSubscription,
      weather.Weather.WeatherEvent> getSubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Subscribe",
      requestType = weather.Weather.WeatherSubscription.class,
      responseType = weather.Weather.WeatherEvent.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<weather.Weather.WeatherSubscription,
      weather.Weather.WeatherEvent> getSubscribeMethod() {
    io.grpc.MethodDescriptor<weather.Weather.WeatherSubscription, weather.Weather.WeatherEvent> getSubscribeMethod;
    if ((getSubscribeMethod = WeatherSubscriberGrpc.getSubscribeMethod) == null) {
      synchronized (WeatherSubscriberGrpc.class) {
        if ((getSubscribeMethod = WeatherSubscriberGrpc.getSubscribeMethod) == null) {
          WeatherSubscriberGrpc.getSubscribeMethod = getSubscribeMethod =
              io.grpc.MethodDescriptor.<weather.Weather.WeatherSubscription, weather.Weather.WeatherEvent>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Subscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  weather.Weather.WeatherSubscription.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  weather.Weather.WeatherEvent.getDefaultInstance()))
              .setSchemaDescriptor(new WeatherSubscriberMethodDescriptorSupplier("Subscribe"))
              .build();
        }
      }
    }
    return getSubscribeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WeatherSubscriberStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberStub>() {
        @java.lang.Override
        public WeatherSubscriberStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherSubscriberStub(channel, callOptions);
        }
      };
    return WeatherSubscriberStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static WeatherSubscriberBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberBlockingV2Stub>() {
        @java.lang.Override
        public WeatherSubscriberBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherSubscriberBlockingV2Stub(channel, callOptions);
        }
      };
    return WeatherSubscriberBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static WeatherSubscriberBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberBlockingStub>() {
        @java.lang.Override
        public WeatherSubscriberBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherSubscriberBlockingStub(channel, callOptions);
        }
      };
    return WeatherSubscriberBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WeatherSubscriberFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<WeatherSubscriberFutureStub>() {
        @java.lang.Override
        public WeatherSubscriberFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new WeatherSubscriberFutureStub(channel, callOptions);
        }
      };
    return WeatherSubscriberFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void subscribe(weather.Weather.WeatherSubscription request,
        io.grpc.stub.StreamObserver<weather.Weather.WeatherEvent> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service WeatherSubscriber.
   */
  public static abstract class WeatherSubscriberImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return WeatherSubscriberGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service WeatherSubscriber.
   */
  public static final class WeatherSubscriberStub
      extends io.grpc.stub.AbstractAsyncStub<WeatherSubscriberStub> {
    private WeatherSubscriberStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherSubscriberStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherSubscriberStub(channel, callOptions);
    }

    /**
     */
    public void subscribe(weather.Weather.WeatherSubscription request,
        io.grpc.stub.StreamObserver<weather.Weather.WeatherEvent> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service WeatherSubscriber.
   */
  public static final class WeatherSubscriberBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<WeatherSubscriberBlockingV2Stub> {
    private WeatherSubscriberBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherSubscriberBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherSubscriberBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, weather.Weather.WeatherEvent>
        subscribe(weather.Weather.WeatherSubscription request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getSubscribeMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service WeatherSubscriber.
   */
  public static final class WeatherSubscriberBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<WeatherSubscriberBlockingStub> {
    private WeatherSubscriberBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherSubscriberBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherSubscriberBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<weather.Weather.WeatherEvent> subscribe(
        weather.Weather.WeatherSubscription request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service WeatherSubscriber.
   */
  public static final class WeatherSubscriberFutureStub
      extends io.grpc.stub.AbstractFutureStub<WeatherSubscriberFutureStub> {
    private WeatherSubscriberFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected WeatherSubscriberFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new WeatherSubscriberFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_SUBSCRIBE = 0;

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
        case METHODID_SUBSCRIBE:
          serviceImpl.subscribe((weather.Weather.WeatherSubscription) request,
              (io.grpc.stub.StreamObserver<weather.Weather.WeatherEvent>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSubscribeMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              weather.Weather.WeatherSubscription,
              weather.Weather.WeatherEvent>(
                service, METHODID_SUBSCRIBE)))
        .build();
  }

  private static abstract class WeatherSubscriberBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    WeatherSubscriberBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return weather.Weather.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WeatherSubscriber");
    }
  }

  private static final class WeatherSubscriberFileDescriptorSupplier
      extends WeatherSubscriberBaseDescriptorSupplier {
    WeatherSubscriberFileDescriptorSupplier() {}
  }

  private static final class WeatherSubscriberMethodDescriptorSupplier
      extends WeatherSubscriberBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    WeatherSubscriberMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (WeatherSubscriberGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WeatherSubscriberFileDescriptorSupplier())
              .addMethod(getSubscribeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
