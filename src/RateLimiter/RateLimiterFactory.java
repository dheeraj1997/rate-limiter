package RateLimiter;

import RateLimiter.Algorithms.FixedWindowRateLimiter;
import RateLimiter.Algorithms.SlidingWindowRateLimiter;

public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(String algoType, int maxRequests, long windowSize) {
        switch (algoType.toLowerCase()) {
            case "fixed":
                return new FixedWindowRateLimiter(maxRequests, windowSize);
            case "sliding":
                return new SlidingWindowRateLimiter(maxRequests, windowSize);
            default:
                throw new IllegalArgumentException("Unsupported RateLimiter type: " + algoType);
        }
    }
}
