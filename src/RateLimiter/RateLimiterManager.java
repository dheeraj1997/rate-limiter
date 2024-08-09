package RateLimiter;

public class RateLimiterManager {
    private static RateLimiterManager instance;
    RateLimiter rateLimiter;

    private RateLimiterManager() {
        this.rateLimiter = RateLimiterFactory.createRateLimiter("fixed", 10, 1000);
    }

    public static RateLimiterManager getInstance() {
        if (instance == null) {
            instance = new RateLimiterManager();
        }
        return instance;
    }

    public boolean allowRequest(String clientId) {
        return rateLimiter.allowRequest(clientId);
    }
}
