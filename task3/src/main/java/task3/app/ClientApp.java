package task3.app;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import task3.grpc.stubs.ItemServiceGrpc;
import task3.grpc.stubs.OrderServiceGrpc;

public class ClientApp {

    private static final List<Option> opts = List.of(
            option("Exit", (i, o, s) -> false),
            option("List all items", ClientApp::getAllItems),
            option("List item by id", ClientApp::getItemById),
            option("Add new item", ClientApp::addItem),
            option("List all orders", ClientApp::getAllOrders),
            option("List order by id", ClientApp::getOrderById),
            option("Add an order", ClientApp::addOrder)
    );

    public static void main(String[] args) {
        System.out.println("Running gRPC Shop client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        ItemServiceGrpc.ItemServiceBlockingStub itemServiceStub = ItemServiceGrpc.newBlockingStub(channel);
        OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub = OrderServiceGrpc.newBlockingStub(channel);

        Map<Integer, Option> options = IntStream.range(0, ClientApp.opts.size()).boxed().collect(Collectors.toMap(i -> i, ClientApp.opts::get));
        try (Scanner scanner = new Scanner(System.in)){
            int cmd;
            while (true){
                System.out.println();
                options.forEach((i, o) -> System.out.println(i+"."+o.getDescription()));
                System.out.print("Option: ");

                cmd = scanner.nextInt();
                Option option = options.get(cmd);
                if (option == null) throw new IllegalArgumentException("No option:" + cmd);
                boolean resume = option.handle(itemServiceStub, orderServiceStub, scanner);
                if (!resume) break;
            }
        }

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Shutdown was interrupted");
            throw new IllegalStateException(e);
        }
    }

    private static boolean getAllItems(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {

        return true;
    }

    private static boolean getItemById(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {

        return true;
    }

    private static boolean addItem(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {

        return true;
    }

    private static boolean getAllOrders(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {

        return true;
    }

    private static boolean getOrderById(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {

        return true;
    }

    private static boolean addOrder(ItemServiceGrpc.ItemServiceBlockingStub itemService, OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner) {

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
        boolean handle(ItemServiceGrpc.ItemServiceBlockingStub itemService,  OrderServiceGrpc.OrderServiceBlockingStub orderService, Scanner scanner);
    }

    private interface Option extends Handler {
        String getDescription();
    }
}
