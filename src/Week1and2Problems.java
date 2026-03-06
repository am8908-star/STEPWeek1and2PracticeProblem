import java.util.*;

class DNSCache {

    class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private int capacity;
    private HashMap<String, DNSEntry> cache;

    // LinkedHashMap for LRU
    private LinkedHashMap<String, DNSEntry> lruMap;

    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        cache = new HashMap<>();

        lruMap = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                if (size() > DNSCache.this.capacity) {
                    cache.remove(eldest.getKey());
                    return true;
                }
                return false;
            }
        };
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                lruMap.get(domain); // update LRU order
                return "Cache HIT → " + entry.ipAddress;
            }

            // expired
            cache.remove(domain);
            lruMap.remove(domain);
        }

        // cache miss
        misses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 300);

        cache.put(domain, newEntry);
        lruMap.put(domain, newEntry);

        return "Cache MISS → " + ip;
    }

    // Simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();

        return "172.217.14." + rand.nextInt(255);
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCache dns = new DNSCache(3);

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("openai.com"));
        System.out.println(dns.resolve("github.com"));
        System.out.println(dns.resolve("google.com"));

        dns.getCacheStats();
    }
}