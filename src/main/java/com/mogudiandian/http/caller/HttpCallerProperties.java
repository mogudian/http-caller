package com.mogudiandian.http.caller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HTTP调用者的配置
 * @author sunbo
 */
@ConfigurationProperties(prefix = "http.caller")
@Getter
@Setter
public class HttpCallerProperties {

    /**
     * 默认连接时间(毫秒) 默认10秒
     */
    private Long defaultConnectTimeout = 10_000L;

    /**
     * 默认读取时间(毫秒) 默认10秒
     */
    private Long defaultReadTimeout = 10_000L;

    /**
     * 负载均衡器类型 默认随机
     */
    private LoadBalancerType loadBalancerType = LoadBalancerType.RANDOM;

    /**
     * 断路器窗口大小 默认是10 表示调用10次后开始计数
     */
    private Integer circuitBreakerWindowSize = 10;

    /**
     * 断路器熔断阈值 默认50% 表示失败超过50%则开启
     */
    private Float circuitBreakerThresholdPercent = 0.5f;

    /**
     * 断路器开启最小调用数量 默认是5 表示如果超过50%但又没到10次不开启
     */
    private Integer circuitBreakerOpenMinCalls = 10;

    /**
     * 断路器每次开启时长(毫秒) 默认5秒
     */
    private Long circuitBreakerOpenDuration = 5_000L;

}
