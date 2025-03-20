package task3.service;


import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import java.util.stream.Collectors;
import task3.grpc.stubs.ItemServiceGrpc;
import task3.grpc.stubs.Shop;
import task3.model.DataSource;
import task3.model.item.Item;

public class ItemService extends ItemServiceGrpc.ItemServiceImplBase {


    @Override
    public void getItems(Shop.Empty request, StreamObserver<Shop.ItemsResponse> responseObserver) {
        DataSource dataSource = DataSource.getInstance();
        Shop.ItemsResponse response = Shop.ItemsResponse.newBuilder()
                .addAllItems(dataSource.getItems().stream().map(this::mapToShortResponse).collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Shop.ItemShortResponse mapToShortResponse(Item item) {
        return Shop.ItemShortResponse.newBuilder()
                .setId(item.getId())
                .setName(item.getName())
                .build();
    }

    @Override
    public void getItem(Shop.ItemRequest request, StreamObserver<Shop.ItemResponse> responseObserver) {
        DataSource dataSource = DataSource.getInstance();
        Optional<Item> itemById = dataSource.findItemById(request.getId());
        if (itemById.isPresent()){
            responseObserver.onNext(mapToResponse(itemById.get()));
            responseObserver.onCompleted();
        }else {
            Status status = Status.NOT_FOUND.withDescription("No Item with following id was found: " + request.getId());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    private Shop.ItemResponse mapToResponse(Item item){
        return Shop.ItemResponse.newBuilder()
                .setId(item.getId())
                .setName(item.getName())
                .setDescription(item.getDescription())
                .setPrice(item.getPrice())
                .build();
    }

    @Override
    public void addItem(Shop.AddItemRequest request, StreamObserver<Shop.AddItemResponse> responseObserver) {
        Item item = mapToItem(request);
        DataSource.getInstance().addItem(item);
        responseObserver.onNext(Shop.AddItemResponse.newBuilder().setCreatedId(item.getId()).build());
        responseObserver.onCompleted();
    }

    private Item mapToItem(Shop.AddItemRequest request){
        return new Item(DataSource.getInstance().getNextItemId(), request.getName(), request.getDescription(), request.getPrice());
    }
}
