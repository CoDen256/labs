package task3.grpc.stubs;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * gRPC service definitions 
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.1)",
    comments = "Source: shop.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ItemServiceGrpc {

  private ItemServiceGrpc() {}

  public static final String SERVICE_NAME = "ItemService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.Empty,
      task3.grpc.stubs.Shop.ItemsResponse> getGetItemsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getItems",
      requestType = task3.grpc.stubs.Shop.Empty.class,
      responseType = task3.grpc.stubs.Shop.ItemsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.Empty,
      task3.grpc.stubs.Shop.ItemsResponse> getGetItemsMethod() {
    io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.Empty, task3.grpc.stubs.Shop.ItemsResponse> getGetItemsMethod;
    if ((getGetItemsMethod = ItemServiceGrpc.getGetItemsMethod) == null) {
      synchronized (ItemServiceGrpc.class) {
        if ((getGetItemsMethod = ItemServiceGrpc.getGetItemsMethod) == null) {
          ItemServiceGrpc.getGetItemsMethod = getGetItemsMethod =
              io.grpc.MethodDescriptor.<task3.grpc.stubs.Shop.Empty, task3.grpc.stubs.Shop.ItemsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getItems"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.ItemsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ItemServiceMethodDescriptorSupplier("getItems"))
              .build();
        }
      }
    }
    return getGetItemsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.ItemRequest,
      task3.grpc.stubs.Shop.ItemResponse> getGetItemMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getItem",
      requestType = task3.grpc.stubs.Shop.ItemRequest.class,
      responseType = task3.grpc.stubs.Shop.ItemResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.ItemRequest,
      task3.grpc.stubs.Shop.ItemResponse> getGetItemMethod() {
    io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.ItemRequest, task3.grpc.stubs.Shop.ItemResponse> getGetItemMethod;
    if ((getGetItemMethod = ItemServiceGrpc.getGetItemMethod) == null) {
      synchronized (ItemServiceGrpc.class) {
        if ((getGetItemMethod = ItemServiceGrpc.getGetItemMethod) == null) {
          ItemServiceGrpc.getGetItemMethod = getGetItemMethod =
              io.grpc.MethodDescriptor.<task3.grpc.stubs.Shop.ItemRequest, task3.grpc.stubs.Shop.ItemResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getItem"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.ItemRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.ItemResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ItemServiceMethodDescriptorSupplier("getItem"))
              .build();
        }
      }
    }
    return getGetItemMethod;
  }

  private static volatile io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.AddItemRequest,
      task3.grpc.stubs.Shop.AddItemResponse> getAddItemMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "addItem",
      requestType = task3.grpc.stubs.Shop.AddItemRequest.class,
      responseType = task3.grpc.stubs.Shop.AddItemResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.AddItemRequest,
      task3.grpc.stubs.Shop.AddItemResponse> getAddItemMethod() {
    io.grpc.MethodDescriptor<task3.grpc.stubs.Shop.AddItemRequest, task3.grpc.stubs.Shop.AddItemResponse> getAddItemMethod;
    if ((getAddItemMethod = ItemServiceGrpc.getAddItemMethod) == null) {
      synchronized (ItemServiceGrpc.class) {
        if ((getAddItemMethod = ItemServiceGrpc.getAddItemMethod) == null) {
          ItemServiceGrpc.getAddItemMethod = getAddItemMethod =
              io.grpc.MethodDescriptor.<task3.grpc.stubs.Shop.AddItemRequest, task3.grpc.stubs.Shop.AddItemResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "addItem"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.AddItemRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  task3.grpc.stubs.Shop.AddItemResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ItemServiceMethodDescriptorSupplier("addItem"))
              .build();
        }
      }
    }
    return getAddItemMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ItemServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ItemServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ItemServiceStub>() {
        @java.lang.Override
        public ItemServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ItemServiceStub(channel, callOptions);
        }
      };
    return ItemServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ItemServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ItemServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ItemServiceBlockingStub>() {
        @java.lang.Override
        public ItemServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ItemServiceBlockingStub(channel, callOptions);
        }
      };
    return ItemServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ItemServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ItemServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ItemServiceFutureStub>() {
        @java.lang.Override
        public ItemServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ItemServiceFutureStub(channel, callOptions);
        }
      };
    return ItemServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * gRPC service definitions 
   * </pre>
   */
  public static abstract class ItemServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getItems(task3.grpc.stubs.Shop.Empty request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.ItemsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetItemsMethod(), responseObserver);
    }

    /**
     */
    public void getItem(task3.grpc.stubs.Shop.ItemRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.ItemResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetItemMethod(), responseObserver);
    }

    /**
     */
    public void addItem(task3.grpc.stubs.Shop.AddItemRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.AddItemResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddItemMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetItemsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                task3.grpc.stubs.Shop.Empty,
                task3.grpc.stubs.Shop.ItemsResponse>(
                  this, METHODID_GET_ITEMS)))
          .addMethod(
            getGetItemMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                task3.grpc.stubs.Shop.ItemRequest,
                task3.grpc.stubs.Shop.ItemResponse>(
                  this, METHODID_GET_ITEM)))
          .addMethod(
            getAddItemMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                task3.grpc.stubs.Shop.AddItemRequest,
                task3.grpc.stubs.Shop.AddItemResponse>(
                  this, METHODID_ADD_ITEM)))
          .build();
    }
  }

  /**
   * <pre>
   * gRPC service definitions 
   * </pre>
   */
  public static final class ItemServiceStub extends io.grpc.stub.AbstractAsyncStub<ItemServiceStub> {
    private ItemServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ItemServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ItemServiceStub(channel, callOptions);
    }

    /**
     */
    public void getItems(task3.grpc.stubs.Shop.Empty request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.ItemsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetItemsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getItem(task3.grpc.stubs.Shop.ItemRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.ItemResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetItemMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addItem(task3.grpc.stubs.Shop.AddItemRequest request,
        io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.AddItemResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddItemMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * gRPC service definitions 
   * </pre>
   */
  public static final class ItemServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ItemServiceBlockingStub> {
    private ItemServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ItemServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ItemServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public task3.grpc.stubs.Shop.ItemsResponse getItems(task3.grpc.stubs.Shop.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetItemsMethod(), getCallOptions(), request);
    }

    /**
     */
    public task3.grpc.stubs.Shop.ItemResponse getItem(task3.grpc.stubs.Shop.ItemRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetItemMethod(), getCallOptions(), request);
    }

    /**
     */
    public task3.grpc.stubs.Shop.AddItemResponse addItem(task3.grpc.stubs.Shop.AddItemRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddItemMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * gRPC service definitions 
   * </pre>
   */
  public static final class ItemServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ItemServiceFutureStub> {
    private ItemServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ItemServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ItemServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<task3.grpc.stubs.Shop.ItemsResponse> getItems(
        task3.grpc.stubs.Shop.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetItemsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<task3.grpc.stubs.Shop.ItemResponse> getItem(
        task3.grpc.stubs.Shop.ItemRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetItemMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<task3.grpc.stubs.Shop.AddItemResponse> addItem(
        task3.grpc.stubs.Shop.AddItemRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddItemMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ITEMS = 0;
  private static final int METHODID_GET_ITEM = 1;
  private static final int METHODID_ADD_ITEM = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ItemServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ItemServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_ITEMS:
          serviceImpl.getItems((task3.grpc.stubs.Shop.Empty) request,
              (io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.ItemsResponse>) responseObserver);
          break;
        case METHODID_GET_ITEM:
          serviceImpl.getItem((task3.grpc.stubs.Shop.ItemRequest) request,
              (io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.ItemResponse>) responseObserver);
          break;
        case METHODID_ADD_ITEM:
          serviceImpl.addItem((task3.grpc.stubs.Shop.AddItemRequest) request,
              (io.grpc.stub.StreamObserver<task3.grpc.stubs.Shop.AddItemResponse>) responseObserver);
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

  private static abstract class ItemServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ItemServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return task3.grpc.stubs.Shop.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ItemService");
    }
  }

  private static final class ItemServiceFileDescriptorSupplier
      extends ItemServiceBaseDescriptorSupplier {
    ItemServiceFileDescriptorSupplier() {}
  }

  private static final class ItemServiceMethodDescriptorSupplier
      extends ItemServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ItemServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ItemServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ItemServiceFileDescriptorSupplier())
              .addMethod(getGetItemsMethod())
              .addMethod(getGetItemMethod())
              .addMethod(getAddItemMethod())
              .build();
        }
      }
    }
    return result;
  }
}
