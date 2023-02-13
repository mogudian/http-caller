package com.mogudiandian.http.caller;

import com.mogudiandian.http.caller.cb.CircuitBreaker;
import com.mogudiandian.http.caller.lb.LoadBalancer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * HTTP调用者默认实现 包括调用、重试、负载均衡、熔断
 *
 * @author sunbo
 */
public class HttpCallerDefaultImpl implements HttpCaller {

    private RestTemplate restTemplate;

    private RetryTemplate retryTemplate;

    private LoadBalancer loadBalancer;

    private CircuitBreaker circuitBreaker;

    public HttpCallerDefaultImpl(RestTemplate restTemplate, RetryTemplate retryTemplate, LoadBalancer loadBalancer, CircuitBreaker circuitBreaker) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
        this.loadBalancer = loadBalancer;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public <T, R> R call(List<String> hostList, String path, Map<String, ?> uriVariables, HttpMethod method, HttpEntity<T> requestEntity, Class<R> responseClass) {
        return retryTemplate.execute(retryContext -> {
            String url = selectUrl(hostList, path);
            return circuitBreaker.execute(url, () -> call0(url, uriVariables, method, requestEntity, responseClass));
        });
    }

    @Override
    public <T, R> R call(List<String> hostList, String path, Map<String, ?> uriVariables, HttpMethod method, HttpEntity<T> requestEntity, ParameterizedTypeReference<R> responseType) {
        return retryTemplate.execute(retryContext -> {
            String url = selectUrl(hostList, path);
            return circuitBreaker.execute(url, () -> call0(url, uriVariables, method, requestEntity, responseType));
        });
    }

    /**
     * 选择URL
     * @param hostList URL前缀集合
     * @param path URL路径
     * @return 完整URL
     */
    private String selectUrl(List<String> hostList, String path) {
        String host = loadBalancer.select(hostList);
        return joinUrl(host, path);
    }

    /**
     * 调用
     * @param url 完整URL
     * @param uriVariables URL中的变量
     * @param method 请求方法
     * @param requestEntity 请求实体
     * @param responseClass 响应对象类型
     * @return 响应对象
     * @param <T> 请求对象类型
     * @param <R> 响应对象类型
     */
    private <T, R> R call0(String url, Map<String, ?> uriVariables, HttpMethod method, HttpEntity<T> requestEntity, Class<R> responseClass) {
        ResponseEntity<R> responseEntity;
        if (uriVariables != null) {
            responseEntity = restTemplate.exchange(url, method, requestEntity, responseClass, uriVariables);
        } else {
            responseEntity = restTemplate.exchange(url, method, requestEntity, responseClass);
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
            throw new RuntimeException("call " + url + " failure, http status is: " + responseEntity.getStatusCodeValue());
        }
        return getResponseObject(url, responseEntity);
    }

    /**
     * 调用
     * @param url 完整URL
     * @param uriVariables URL中的变量
     * @param method 请求方法
     * @param requestEntity 请求实体
     * @param responseType 响应对象类型
     * @return 响应对象
     * @param <T> 请求对象类型
     * @param <R> 响应对象类型
     */
    private <T, R> R call0(String url, Map<String, ?> uriVariables, HttpMethod method, HttpEntity<T> requestEntity, ParameterizedTypeReference<R> responseType) {
        ResponseEntity<R> responseEntity;
        if (uriVariables != null) {
            responseEntity = restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
        } else {
            responseEntity = restTemplate.exchange(url, method, requestEntity, responseType);
        }
        return getResponseObject(url, responseEntity);
    }

    /**
     * 获取响应对象
     * @param url 完整URL
     * @param responseEntity 响应实体
     * @return 响应对象
     * @param <R> 响应对象类型
     */
    private <R> R getResponseObject(String url, ResponseEntity<R> responseEntity) {
        if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
            throw new RuntimeException("call " + url + " failure, http status is: " + responseEntity.getStatusCodeValue());
        }
        return responseEntity.getBody();
    }

    /**
     * 拼接URL
     * @param baseUrl 前缀 例如 http://www.xxx.com/
     * @param path 后缀 例如 /api/login
     * @return 完整URL
     */
    private String joinUrl(String baseUrl, String path) {
        while (!baseUrl.isEmpty() && baseUrl.charAt(baseUrl.length() - 1) == '/') {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        while (!path.isEmpty() && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        return baseUrl + '/' + path;
    }

}
