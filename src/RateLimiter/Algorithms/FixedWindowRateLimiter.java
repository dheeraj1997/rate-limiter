package RateLimiter.Algorithms;

import RateLimiter.AbstractRateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FixedWindowRateLimiter extends AbstractRateLimiter {
    private final Map<String, Integer> requestCounts;
    private final Map<String, Long> windowStartTimes;
    private final Map<String, ReentrantLock> locks;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        super(maxRequests, windowSizeInMillis);
        this.requestCounts = new ConcurrentHashMap<>();
        this.windowStartTimes = new ConcurrentHashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    @Override
    protected boolean isRequestAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        locks.putIfAbsent(clientId, new ReentrantLock());
        ReentrantLock lock = locks.get(clientId);

        lock.lock();
        try {
            windowStartTimes.putIfAbsent(clientId, currentTime);
            requestCounts.putIfAbsent(clientId, 0);

            long windowStartTime = windowStartTimes.get(clientId);
            if (currentTime - windowStartTime >= windowSizeInMillis) {
                windowStartTimes.put(clientId, currentTime);
                requestCounts.put(clientId, 0);
            }

            int requestCount = requestCounts.get(clientId);
            if (requestCount < maxRequests) {
                requestCounts.put(clientId, requestCount + 1);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
