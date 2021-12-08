package task3.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import task3.grpc.stubs.OrderServiceGrpc;
import task3.grpc.stubs.Shop;
import task3.model.DataSource;
import task3.model.order.Order;
import task3.model.order.OrderedItem;

public class OrderService extends OrderServiceGrpc.OrderServiceImplBase {
    @Override
    public void getOrders(Shop.Empty request, StreamObserver<Shop.OrdersResponse> responseObserver) {
        List<Shop.ShortOrderResponse> values = DataSource.getInstance().getOrders().stream().map(this::mapOrderToShortResponse).collect(Collectors.toList());
        responseObserver.onNext(Shop.OrdersResponse.newBuilder().addAllOrders(values).build());
        responseObserver.onCompleted();
    }

    private Shop.ShortOrderResponse mapOrderToShortResponse(Order order) {
        return Shop.ShortOrderResponse.newBuilder()
                .setId(order.getId())
                .setTotalItems(order.getItems().size())
                .build();
    }

    @Override
    public void getOrder(Shop.OrderRequest request, StreamObserver<Shop.OrderResponse> responseObserver) {
        Optional<Order> order = DataSource.getInstance().findOrderById(request.getId());
        if (order.isPresent()){
            responseObserver.onNext(mapOrderToResponse(order.get()));
            responseObserver.onCompleted();
        }else {
            Status status = Status.NOT_FOUND.withDescription("No order with following id was found: " + request.getId());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    private Shop.OrderResponse mapOrderToResponse(Order order) {
        return Shop.OrderResponse.newBuilder()
                .addAllItems(order.getItems().stream().map(this::mapOrderedItemToResponse).collect(Collectors.toList()))
                .build();
    }

    private Shop.OrderedItemResponse mapOrderedItemToResponse(OrderedItem orderedItem) {
        return Shop.OrderedItemResponse.newBuilder()
                .setItemId(orderedItem.getId())
                .setName(DataSource.getInstance().findItemById(orderedItem.getId()).get().getName())
                .setQuantity(orderedItem.getQuantity())
                .build();
    }

    @Override
    public void addOrder(Shop.AddOrderRequest request, StreamObserver<Shop.AddOrderResponse> responseObserver) {
        DataSource dataSource = DataSource.getInstance();
        try {
            Order newOrder = mapToOrder(request);
            dataSource.addOrder(newOrder);
            responseObserver.onNext(Shop.AddOrderResponse.newBuilder().setCreatedId(newOrder.getId()).build());
            responseObserver.onCompleted();
        }catch (Exception e){
            Status status = Status.NOT_FOUND.withDescription(e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    private Order mapToOrder(Shop.AddOrderRequest request) {
        return new Order(DataSource.getInstance().getNextOrderId(), request.getNewItemsList().stream().map(this::mapToOrderedItems).collect(Collectors.toList()));
    }

    private OrderedItem mapToOrderedItems(Shop.OrderedItemRequest orderedItemRequest) {
        int id = orderedItemRequest.getId();
        if (DataSource.getInstance().findItemById(id).isEmpty()){
            throw new IllegalArgumentException("No Item with id:" + id);
        }
        return new OrderedItem(id, orderedItemRequest.getQuantity());
    }


}
