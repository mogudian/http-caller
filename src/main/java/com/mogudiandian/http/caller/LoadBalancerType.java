package com.mogudiandian.http.caller;

import java.util.function.Supplier;

/**
 * 负载均衡器类型
 *
 * @author sunbo
 */
public enum LoadBalancerType {

    RANDOM(() -> new LoadBalancerRandomImpl()),
    POLLING(() -> new LoadBalancerPollingImpl()),
    IP_HASH(() -> new LoadBalancerIpHashImpl());

    private Supplier<? extends LoadBalancer> instantiationMethod;

    LoadBalancerType(Supplier<LoadBalancer> instantiationMethod) {
        this.instantiationMethod = instantiationMethod;
    }

    public LoadBalancer instantiate() {
        return instantiationMethod.get();
    }
}
