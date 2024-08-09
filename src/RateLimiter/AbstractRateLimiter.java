package RateLimiter;

public abstract class AbstractRateLimiter implements RateLimiter {
    protected final int maxRequests;
    protected final long windowSizeInMillis;

    protected AbstractRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    @Override
    public boolean allowRequest(String clientId) {
        return isRequestAllowed(clientId);
    }

    protected abstract boolean isRequestAllowed(String clientId);
}
