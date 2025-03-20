package task3.grpc.stubs;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.1)",
    comments = "Source: shop.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class OrderServiceGrpc {

  private OrderServiceGrpc() {}

  public static final String SERVICE_NAME = "OrderService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.Empty,
      task3.grpc.stubs.Shop.OrdersResponse> getGetOrdersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getOrders",
      requestType = task3.grpc.stubs.Shop.Empty.class,
      responseType = task3.grpc.stubs.Shop.OrdersResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.Empty,
      task3.grpc.stubs.Shop.OrdersResponse> getGetOrdersMethod() {
    io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.Empty, task3.grpc.stubs.Shop.OrdersResponse> getGetOrdersMethod;
    if ((getGetOrdersMethod = OrderServiceGrpc.getGetOrdersMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getGetOrdersMethod = OrderServiceGrpc.getGetOrdersMethod) == null) {
          OrderServiceGrpc.getGetOrdersMethod = getGetOrdersMethod =
              io.grpc.MethodDescriptor.<task3.grpc.stubs.Shop.Empty, task3.grpc.stubs.Shop.OrdersResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getOrders"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.OrdersResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("getOrders"))
              .build();
        }
      }
    }
    return getGetOrdersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.OrderRequest,
      task3.grpc.stubs.Shop.OrderResponse> getGetOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getOrder",
      requestType = task3.grpc.stubs.Shop.OrderRequest.class,
      responseType = task3.grpc.stubs.Shop.OrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.OrderRequest,
      task3.grpc.stubs.Shop.OrderResponse> getGetOrderMethod() {
    io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.OrderRequest, task3.grpc.stubs.Shop.OrderResponse> getGetOrderMethod;
    if ((getGetOrderMethod = OrderServiceGrpc.getGetOrderMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getGetOrderMethod = OrderServiceGrpc.getGetOrderMethod) == null) {
          OrderServiceGrpc.getGetOrderMethod = getGetOrderMethod =
              io.grpc.MethodDescriptor.<task3.grpc.stubs.Shop.OrderRequest, task3.grpc.stubs.Shop.OrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.OrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.OrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("getOrder"))
              .build();
        }
      }
    }
    return getGetOrderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.AddOrderRequest,
      task3.grpc.stubs.Shop.AddOrderResponse> getAddOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "addOrder",
      requestType = task3.grpc.stubs.Shop.AddOrderRequest.class,
      responseType = task3.grpc.stubs.Shop.AddOrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.AddOrderRequest,
      task3.grpc.stubs.Shop.AddOrderResponse> getAddOrderMethod() {
    io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.AddOrderRequest, task3.grpc.stubs.Shop.AddOrderResponse> getAddOrderMethod;
    if ((getAddOrderMethod = OrderServiceGrpc.getAddOrderMethod) == null) {
      synchronized (OrderServiceGrpc.class) {
        if ((getAddOrderMethod = OrderServiceGrpc.getAddOrderMethod) == null) {
          OrderServiceGrpc.getAddOrderMethod = getAddOrderMethod =
              io.grpc.MethodDescriptor.<task3.grpc.stubs.Shop.AddOrderRequest, task3.grpc.stubs.Shop.AddOrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "addOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.AddOrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.AddOrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderServiceMethodDescriptorSupplier("addOrder"))
              .build();
        }
      }
    }
    return getAddOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OrderServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceStub>() {
        @java.lang.Override
        public OrderServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceStub(channel, callOptions);
        }
      };
    return OrderServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OrderServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceBlockingStub>() {
        @java.lang.Override
        public OrderServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceBlockingStub(channel, callOptions);
        }
      };
    return OrderServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OrderServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderServiceFutureStub>() {
        @java.lang.Override
        public OrderServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderServiceFutureStub(channel, callOptions);
        }
      };
    return OrderServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class OrderServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getOrders(task3.grpc.stubs.Shop.Empty request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.OrdersResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetOrdersMethod(), responseObserver);
    }

    /**
     */
    public void getOrder(task3.grpc.stubs.Shop.OrderRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.OrderResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetOrderMethod(), responseObserver);
    }

    /**
     */
    public void addOrder(task3.grpc.stubs.Shop.AddOrderRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.AddOrderResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddOrderMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetOrdersMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                task3.grpc.stubs.Shop.Empty,
                task3.grpc.stubs.Shop.OrdersResponse>(
                  this, METHODID_GET_ORDERS)))
          .addMethod(
            getGetOrderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                task3.grpc.stubs.Shop.OrderRequest,
                task3.grpc.stubs.Shop.OrderResponse>(
                  this, METHODID_GET_ORDER)))
          .addMethod(
            getAddOrderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                task3.grpc.stubs.Shop.AddOrderRequest,
                task3.grpc.stubs.Shop.AddOrderResponse>(
                  this, METHODID_ADD_ORDER)))
          .build();
    }
  }

  /**
   */
  public static final class OrderServiceStub extends io.grpc.stub.AbstractAsyncStub<OrderServiceStub> {
    private OrderServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceStub(channel, callOptions);
    }

    /**
     */
    public void getOrders(task3.grpc.stubs.Shop.Empty request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.OrdersResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetOrdersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getOrder(task3.grpc.stubs.Shop.OrderRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.OrderResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetOrderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addOrder(task3.grpc.stubs.Shop.AddOrderRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.AddOrderResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class OrderServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<OrderServiceBlockingStub> {
    private OrderServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public task3.grpc.stubs.Shop.OrdersResponse getOrders(task3.grpc.stubs.Shop.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrdersMethod(), getCallOptions(), request);
    }

    /**
     */
    public task3.grpc.stubs.Shop.OrderResponse getOrder(task3.grpc.stubs.Shop.OrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetOrderMethod(), getCallOptions(), request);
    }

    /**
     */
    public task3.grpc.stubs.Shop.AddOrderResponse addOrder(task3.grpc.stubs.Shop.AddOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class OrderServiceFutureStub extends io.grpc.stub.AbstractFutureStub<OrderServiceFutureStub> {
    private OrderServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<task3.grpc.stubs.Shop.OrdersResponse> getOrders(
        task3.grpc.stubs.Shop.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetOrdersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<task3.grpc.stubs.Shop.OrderResponse> getOrder(
        task3.grpc.stubs.Shop.OrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetOrderMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<task3.grpc.stubs.Shop.AddOrderResponse> addOrder(
        task3.grpc.stubs.Shop.AddOrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ORDERS = 0;
  private static final int METHODID_GET_ORDER = 1;
  private static final int METHODID_ADD_ORDER = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OrderServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(OrderServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_ORDERS:
          serviceImpl.getOrders((task3.grpc.stubs.Shop.Empty) request,
              (io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.OrdersResponse>) responseObserver);
          break;
        case METHODID_GET_ORDER:
          serviceImpl.getOrder((task3.grpc.stubs.Shop.OrderRequest) request,
              (io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.OrderResponse>) responseObserver);
          break;
        case METHODID_ADD_ORDER:
          serviceImpl.addOrder((task3.grpc.stubs.Shop.AddOrderRequest) request,
              (io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.AddOrderResponse>) responseObserver);
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

  private static abstract class OrderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    OrderServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return task3.grpc.stubs.Shop.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("OrderService");
    }
  }

  private static final class OrderServiceFileDescriptorSupplier
      extends OrderServiceBaseDescriptorSupplier {
    OrderServiceFileDescriptorSupplier() {}
  }

  private static final class OrderServiceMethodDescriptorSupplier
      extends OrderServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    OrderServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (OrderServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OrderServiceFileDescriptorSupplier())
              .addMethod(getGetOrdersMethod())
              .addMethod(getGetOrderMethod())
              .addMethod(getAddOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
