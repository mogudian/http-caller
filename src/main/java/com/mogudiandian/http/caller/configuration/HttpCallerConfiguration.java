package com.mogudiandian.http.caller.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.mogudiandian.http.caller.*;
import com.google.common.collect.ImmutableList;
import com.mogudiandian.http.caller.cb.CircuitBreaker;
import com.mogudiandian.http.caller.cb.CircuitBreakerResilienceImpl;
import com.mogudiandian.http.caller.lb.LoadBalancer;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;

/**
 * HTTP调用者启动类
 *
 * @author Joshua Sun
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(HttpCallerProperties.class)
public class HttpCallerConfiguration {

    @Autowired
    private HttpCallerProperties httpCallerProperties;

    /**
     * HTTP请求工厂 RestTemplate使用
     */
    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(httpCallerProperties.getDefaultConnectTimeout().intValue());
        factory.setReadTimeout(httpCallerProperties.getDefaultReadTimeout().intValue());
        return factory;
    }

    /**
     * 如果没有HttpClient 指定一个
     */
    @ConditionalOnMissingBean(RestTemplate.class)
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        // 换上fastjson
        List<HttpMessageConverter<?>> httpMessageConverterList = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = httpMessageConverterList.iterator();
        if (iterator.hasNext()) {
            HttpMessageConverter<?> converter = iterator.next();
            // 原有的StringHttpMessageConverter是ISO-8859-1编码 去掉
            if (converter instanceof StringHttpMessageConverter) {
                iterator.remove();
            }
        }
        httpMessageConverterList.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        fastJsonHttpMessageConverter.setSupportedMediaTypes(ImmutableList.of(MediaType.APPLICATION_JSON));
        httpMessageConverterList.add(0, fastJsonHttpMessageConverter);
        return restTemplate;
    }

    /**
     * 重试模板 默认不重试
     */
    @Bean
    @ConditionalOnMissingBean(RetryTemplate.class)
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new NeverRetryPolicy());
        return retryTemplate;
    }

    /**
     * 负载均衡器
     */
    @Bean
    @ConditionalOnMissingBean(LoadBalancer.class)
    public LoadBalancer loadBalancer() {
        return httpCallerProperties.getLoadBalancerType().instantiate();
    }

    /**
     * 负载均衡配置 LoadBalancer使用
     */
    @Bean
    @ConditionalOnMissingBean(CircuitBreakerConfig.class)
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                                   .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                   .slidingWindowSize(httpCallerProperties.getCircuitBreakerWindowSize())
                                   .failureRateThreshold(httpCallerProperties.getCircuitBreakerThresholdPercent() * 100)
                                   .minimumNumberOfCalls(httpCallerProperties.getCircuitBreakerOpenMinCalls())
                                   .waitDurationInOpenState(Duration.ofMillis(httpCallerProperties.getCircuitBreakerOpenDuration()))
                                   .build();
    }

    /**
     * 负载均衡器
     */
    @Bean
    @ConditionalOnMissingBean(CircuitBreaker.class)
    public CircuitBreaker circuitBreaker(CircuitBreakerConfig circuitBreakerConfig) {
        return new CircuitBreakerResilienceImpl(circuitBreakerConfig);
    }

    /**
     * HTTP调用者
     */
    @Bean
    @ConditionalOnMissingBean(HttpCallerDefaultImpl.class)
    public HttpCaller httpCaller(RestTemplate restTemplate, RetryTemplate retryTemplate, LoadBalancer loadBalancer, CircuitBreaker circuitBreaker) {
        return new HttpCallerDefaultImpl(restTemplate, retryTemplate, loadBalancer, circuitBreaker);
    }

}