import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    private long tokens;
    private long maxTokens;
    private double refillRate;
    private long lastRefillTime;

    public TokenBucket(long maxTokens, double refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTime) / 1000.0 * refillRate;
        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + (long) tokensToAdd);
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    public synchronized long getRemainingTokens() {
        refill();
        return tokens;
    }

    public synchronized long getRetryAfterSeconds() {
        if (tokens > 0) return 0;
        return (long)(1 / refillRate);
    }
}

class RateLimiter {

    private ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private TokenBucket getBucket(String clientId) {
        return buckets.computeIfAbsent(clientId, id -> new TokenBucket(1000, 1000.0 / 3600));
    }

    public String checkRateLimit(String clientId) {

        TokenBucket bucket = getBucket(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        }

        return "Denied (0 requests remaining, retry after " + bucket.getRetryAfterSeconds() + "s)";
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = getBucket(clientId);

        long remaining = bucket.getRemainingTokens();
        long used = 1000 - remaining;
        long reset = System.currentTimeMillis()/1000 + bucket.getRetryAfterSeconds();

        return "{used: " + used + ", limit: 1000, reset: " + reset + "}";
    }
}

public class DistributedRateLimiterDemo {

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String clientId = "abc123";

        for(int i=0;i<1005;i++){
            System.out.println(limiter.checkRateLimit(clientId));
        }

        System.out.println(limiter.getRateLimitStatus(clientId));
    }
}