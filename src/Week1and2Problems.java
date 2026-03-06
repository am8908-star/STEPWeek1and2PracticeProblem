import java.util.*;

class UsernameSystem {

    // username -> userId
    private HashMap<String, Integer> usernameToUserId;

    // username -> attempt frequency
    private HashMap<String, Integer> usernameAttempts;

    public UsernameSystem() {
        usernameToUserId = new HashMap<>();
        usernameAttempts = new HashMap<>();
    }

    // Check if username is available
    public boolean checkAvailability(String username) {

        // track attempt
        usernameAttempts.put(username,
                usernameAttempts.getOrDefault(username, 0) + 1);

        return !usernameToUserId.containsKey(username);
    }

    // Register username
    public void register(String username, int userId) {
        if (!usernameToUserId.containsKey(username)) {
            usernameToUserId.put(username, userId);
            System.out.println("Registered: " + username);
        } else {
            System.out.println("Username already taken");
        }
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // append numbers
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;

            if (!usernameToUserId.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // replace underscore with dot
        if (username.contains("_")) {
            String alt = username.replace("_", ".");
            if (!usernameToUserId.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for (String user : usernameAttempts.keySet()) {
            int count = usernameAttempts.get(user);

            if (count > max) {
                max = count;
                result = user;
            }
        }

        return result + " (" + max + " attempts)";
    }

    // Main method for testing
    public static void main(String[] args) {

        UsernameSystem system = new UsernameSystem();

        system.register("john_doe", 1);
        system.register("admin", 2);

        System.out.println(system.checkAvailability("john_doe"));
        System.out.println(system.checkAvailability("jane_smith"));

        System.out.println(system.suggestAlternatives("john_doe"));

        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("admin");

        System.out.println(system.getMostAttempted());
    }
}