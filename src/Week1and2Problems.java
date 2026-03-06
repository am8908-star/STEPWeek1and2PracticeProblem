import java.util.*;

class FlashSaleInventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> stockMap;

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList;

    public FlashSaleInventoryManager() {
        stockMap = new HashMap<>();
        waitingList = new HashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = stockMap.getOrDefault(productId, 0);

        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        }

        // add to waiting list
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);
        int position = queue.size() + 1;
        queue.put(userId, position);

        return "Added to waiting list, position #" + position;
    }

    // View waiting list
    public void printWaitingList(String productId) {
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        for (Integer user : queue.keySet()) {
            System.out.println("User " + user + " -> Position " + queue.get(user));
        }
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 3);

        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 11111));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 22222));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 33333));

        System.out.println("\nWaiting List:");
        manager.printWaitingList("IPHONE15_256GB");
    }
}