package com.mogudiandian.http.caller.lb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡-轮询算法
 *
 * @author Joshua Sun
 * @since 1.0.0
 */
public class LoadBalancerPollingImpl implements LoadBalancer {

    private static final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    private String getKey(List<String> list) {
        return "list@" + Integer.toHexString(list.hashCode());
    }

    @Override
    public String select(List<String> list) {
        AtomicInteger counter = counterMap.computeIfAbsent(getKey(list), k -> new AtomicInteger(-1));
        int index = counter.incrementAndGet();
        if (index < 0 || index == Integer.MAX_VALUE) {
            synchronized (counter) {
                counter.set(-1);
            }
            index = 0;
        }
        return list.get(index % list.size());
    }

}
