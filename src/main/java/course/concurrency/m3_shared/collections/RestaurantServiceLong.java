package course.concurrency.m3_shared.collections;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RestaurantServiceLong extends RestaurantService {

    private final ConcurrentHashMap<String, Long> statLong = new ConcurrentHashMap<>();
    private Restaurant mockRestaurant = new Restaurant("A");

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return mockRestaurant;
    }

    public void addToStat(String restaurantName) {
        // Increment happens inside synchronized block so it's possible to use plain long value
        // In most cases workload is distributed across different keys, so it's ok to use this approach
        // Synchronized block becomes a bottleneck
        // when 2+ concurrent threads are intensely working with the same key
        statLong.merge(restaurantName, 1L, (k,v) -> k = k + 1);
    }

    public Set<String> printStat() {
        return statLong.entrySet().stream()
                .map(e -> e.getKey() + " - " + e.getValue())
                .collect(Collectors.toSet());
    }

}
