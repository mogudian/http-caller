package com.mogudiandian.http.caller.lb;

import java.util.function.Supplier;

/**
 * 负载均衡器类型
 *
 * @author Joshua Sun
 * @since 1.0.0
 */
public enum LoadBalancerType {

    RANDOM(() -> new LoadBalancerRandomImpl()),
    POLLING(() -> new LoadBalancerPollingImpl());

    private Supplier<? extends LoadBalancer> instantiationMethod;

    LoadBalancerType(Supplier<LoadBalancer> instantiationMethod) {
        this.instantiationMethod = instantiationMethod;
    }

    public LoadBalancer instantiate() {
        return instantiationMethod.get();
    }
}
