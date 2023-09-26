package com.mogudiandian.http.caller.cb;

import java.util.function.Supplier;

/**
 * 断路器
 *
 * @author Joshua Sun
 * @since 1.0.0
 */
public interface CircuitBreaker {

    <R> R execute(String key, Supplier<R> supplier);

}
