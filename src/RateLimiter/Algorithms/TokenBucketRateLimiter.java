package RateLimiter.Algorithms;

import RateLimiter.AbstractRateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketRateLimiter extends AbstractRateLimiter {
    private final Map<String, TokenBucket> tokenBuckets;
    private final Map<String, ReentrantLock> locks;

    public TokenBucketRateLimiter(int maxRequests, long windowSizeInMillis) {
        super(maxRequests, windowSizeInMillis);
        this.tokenBuckets = new ConcurrentHashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    private static class TokenBucket {
        int tokens;
        long lastRefillTime;

        TokenBucket(int tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }

    @Override
    protected boolean isRequestAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        tokenBuckets.putIfAbsent(clientId, new TokenBucket(maxRequests, currentTime));
        locks.putIfAbsent(clientId, new ReentrantLock());

        TokenBucket tokenBucket = tokenBuckets.get(clientId);
        ReentrantLock lock = locks.get(clientId);

        lock.lock();
        try {
            long elapsedTime = currentTime - tokenBucket.lastRefillTime;
            double refillRate = (double) maxRequests / windowSizeInMillis; // tokens per millisecond
            int tokensToAdd = (int) (elapsedTime * refillRate);

            if (tokensToAdd > 0) {
                tokenBucket.tokens = Math.min(maxRequests, tokenBucket.tokens + tokensToAdd);
                tokenBucket.lastRefillTime = currentTime;
            }

            if (tokenBucket.tokens > 0) {
                tokenBucket.tokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
