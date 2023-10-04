package com.mogudiandian.http.caller.lb;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡-自定义
 *
 * @author Joshua Sun
 * @since 1.0.0
 */
public class LoadBalancerCustomizeImpl implements LoadBalancer {

    private static final ThreadLocal<String> SPECIFIED = new ThreadLocal<>();

    @Override
    public String select(List<String> list) {
        try {
            return SPECIFIED.get();
        } finally {
            SPECIFIED.remove();
        }
    }

    public static void customize(String str) {
        SPECIFIED.set(str);
    }

}
