package task3.app;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import task3.grpc.stubs.ItemServiceGrpc;
import task3.grpc.stubs.OrderServiceGrpc;
import task3.grpc.stubs.Shop;

public class ClientApp {

    private static final List<Option> opts = List.of(
            option("Exit", (i, o, s) -> false),
            option("List all items", ClientApp::getAllItems),
            option("View item by id", ClientApp::getItemById),
            option("Add a new item", ClientApp::addItem),
            option("List all orders", ClientApp::getAllOrders),
            option("View order by id", ClientApp::getOrderById),
            option("Add a new order", ClientApp::addOrder)
    );

    public static void main(String[] args) {
        System.out.println("Running gRPC Shop client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        ItemServiceGrpc.ItemServiceBlockingStub itemServiceStub = ItemServiceGrpc.newBlockingStub(channel);
        OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub = OrderServiceGrpc.newBlockingStub(channel);

        Map<Integer, Option> options = IntStream.range(0, ClientApp.opts.size()).boxed().collect(Collectors.toMap(i -> i, ClientApp.opts::get));
        try (Scanner scanner = new Scanner(System.in)) {
            handleOptions(itemServiceStub, orderServiceStub, options, scanner);
        }

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Shutdown was interrupted");
            throw new IllegalStateException(e);
        }
    }

    private static void handleOptions(ItemServiceGrpc.ItemServiceBlockingStub itemServiceStub, OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub, Map<Integer, Option> options, Scanner scanner) {
        int cmd;
        while (true) {
            System.out.println();
            System.out.println("Choose one of the option:");
            options.forEach((i, o) -> System.out.println(i + "." + o.getDescription()));
            System.out.print("Option: ");

            cmd = scanner.nextInt();
            scanner.nextLine();
            Option option = options.get(cmd);
            if (option == null) throw new IllegalArgumentException("No option: " + cmd);

            try {
                boolean resume = option.handle(itemServiceStub, orderServiceStub, scanner);
                if (!resume) break;
            }catch (StatusRuntimeException e){
                System.out.printf("Error occurred:%s %n",  e.getLocalizedMessage());
            }
        }
    }

    private static boolean getAllItems(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {
        Shop.ItemsResponse items = itemService.getItems(Shop.Empty.getDefaultInstance());
        System.out.println("\nList of all items:");
        items.getItemsList().forEach(
                item -> System.out.printf("%d.%s%n", item.getId(), item.getName())
        );
        return true;
    }

    private static boolean getItemById(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {
        System.out.print("\nChoose item id:");
        int id = scanner.nextInt();

        Shop.ItemResponse item = itemService.getItem(Shop.ItemRequest.newBuilder().setId(id).build());
        System.out.printf("Item %d:%n", id);
        System.out.println("Name: " + item.getName());
        System.out.println("Description: " + item.getDescription());
        System.out.println("Price: " + item.getPrice());

        return true;
    }

    private static boolean addItem(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {
        System.out.print("\nEnter the name of the item:");
        String name = scanner.nextLine();

        System.out.print("Enter the description of the item:");
        String description = scanner.nextLine();

        System.out.print("Enter the price of the item (cents):");
        long price = scanner.nextLong();

        Shop.AddItemRequest addItemRequest = Shop.AddItemRequest.newBuilder()
                .setDescription(description)
                .setName(name)
                .setPrice(price)
                .build();

        Shop.AddItemResponse response = itemService.addItem(addItemRequest);
        System.out.printf("New item with id %d was created!%n", response.getCreatedId());

        return true;
    }

    private static boolean getAllOrders(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {
        System.out.println("\nList of all orders:");
        List<Shop.ShortOrderResponse> orders = orderService.getOrders(Shop.Empty.getDefaultInstance()).getOrdersList();

        orders.forEach(
                o -> System.out.printf("%d. [Order with %d items] %n", o.getId(), o.getTotalItems())
        );

        return true;
    }

    private static boolean getOrderById(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {
        System.out.print("\nChoose order id:");
        int id = scanner.nextInt();

        Shop.OrderResponse order = orderService.getOrder(Shop.OrderRequest.newBuilder().setId(id).build());
        List<Shop.OrderedItemResponse> items = order.getItemsList();
        System.out.printf("Ordered items in order %d:%n", id);
        items.forEach(orderedItem -> {
            System.out.println();
            System.out.printf("Item name: %s%n", orderedItem.getName());
            System.out.printf("Item id: %d%n", orderedItem.getItemId());
            System.out.printf("Quantity: %d%n", orderedItem.getQuantity());

        });

        return true;
    }

    private static boolean addOrder(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {
        List<Shop.OrderedItemRequest> orderedItems = new ArrayList<>();

        System.out.print("\nEnter the total order entries:");
        int orderedItemsCount = scanner.nextInt();

        for (int i = 0; i < orderedItemsCount; i++) {
            System.out.print("\nEnter the id of the item to order:");
            int id = scanner.nextInt();

            System.out.print("Enter the quantity to order:");
            int quantity = scanner.nextInt();
            orderedItems.add(Shop.OrderedItemRequest.newBuilder()
                    .setId(id)
                    .setQuantity(quantity)
                    .build());
        }

        Shop.AddOrderRequest addOrderRequest = Shop.AddOrderRequest.newBuilder()
                .addAllNewItems(orderedItems)
                .build();

        Shop.AddOrderResponse response = orderService.addOrder(addOrderRequest);
        System.out.printf("New order with id %d was created!%n", response.getCreatedId());

        return true;
    }


    private static Option option(String description, Handler handler) {
        return new Option() {
            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public boolean handle(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {
                return handler.handle(itemService, orderService, scanner);
            }
        };
    }

    private interface Handler {
        boolean handle(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner);
    }

    private interface Option extends Handler {
        String getDescription();
    }
}
