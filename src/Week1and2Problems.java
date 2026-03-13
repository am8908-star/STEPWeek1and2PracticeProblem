import java.util.*;

class Event {
    String url;
    String userId;
    String source;

    Event(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsSystem {

    private HashMap<String, Integer> pageViews = new HashMap<>();
    private HashMap<String, HashSet<String>> uniqueVisitors = new HashMap<>();
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(Event e) {

        pageViews.put(e.url, pageViews.getOrDefault(e.url, 0) + 1);

        uniqueVisitors.putIfAbsent(e.url, new HashSet<>());
        uniqueVisitors.get(e.url).add(e.userId);

        trafficSources.put(e.source, trafficSources.getOrDefault(e.source, 0) + 1);
    }

    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 10) pq.poll();
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);
        return result;
    }

    public void getDashboard() {

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        System.out.println("Top Pages:");

        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url + " - " + views + " views (" + unique + " unique)");
            rank++;
        }

        int total = 0;
        for (int count : trafficSources.values()) total += count;

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent = (entry.getValue() * 100.0) / total;

            System.out.println(entry.getKey() + ": " + String.format("%.2f", percent) + "%");
        }
    }
}

public class Week1and2Problems {

    public static void main(String[] args) {

        AnalyticsSystem analytics = new AnalyticsSystem();

        analytics.processEvent(new Event("/article/breaking-news","user_123","google"));
        analytics.processEvent(new Event("/article/breaking-news","user_456","facebook"));
        analytics.processEvent(new Event("/sports/championship","user_789","direct"));
        analytics.processEvent(new Event("/sports/championship","user_101","google"));
        analytics.processEvent(new Event("/sports/championship","user_102","google"));
        analytics.processEvent(new Event("/tech/ai","user_103","facebook"));
        analytics.processEvent(new Event("/tech/ai","user_104","google"));

        analytics.getDashboard();
    }
}