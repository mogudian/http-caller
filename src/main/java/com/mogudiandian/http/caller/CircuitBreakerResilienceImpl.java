package com.mogudiandian.http.caller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.util.function.Supplier;

/**
 * 断路器resilience4j实现
 *
 * @author sunbo
 */
public class CircuitBreakerResilienceImpl implements CircuitBreaker {

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private CircuitBreakerConfig circuitBreakerConfig;

    public CircuitBreakerResilienceImpl(CircuitBreakerConfig circuitBreakerConfig) {
        this.circuitBreakerConfig = circuitBreakerConfig;
        this.circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Override
    public <R> R execute(String key, Supplier<R> supplier) {
        return circuitBreakerRegistry.circuitBreaker(key, circuitBreakerConfig)
                                     .executeSupplier(supplier);
    }
}
