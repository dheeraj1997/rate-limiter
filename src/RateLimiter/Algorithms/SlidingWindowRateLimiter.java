package RateLimiter.Algorithms;

import RateLimiter.AbstractRateLimiter;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SlidingWindowRateLimiter extends AbstractRateLimiter {
    private final Map<String, Queue<Long>> requestTimestamps;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        super(maxRequests, windowSizeInMillis);
        this.requestTimestamps = new ConcurrentHashMap<>();
    }

    @Override
    protected boolean isRequestAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        requestTimestamps.putIfAbsent(clientId, new ConcurrentLinkedQueue<>());

        Queue<Long> timestamps = requestTimestamps.get(clientId);
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && currentTime - timestamps.peek() > windowSizeInMillis) {
                timestamps.poll();
            }

            if (timestamps.size() < maxRequests) {
                timestamps.add(currentTime);
                return true;
            }
            return false;
        }
    }
}
