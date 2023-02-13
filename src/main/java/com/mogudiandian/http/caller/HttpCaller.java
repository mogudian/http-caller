package com.mogudiandian.http.caller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * HTTP调用者
 *
 * @author sunbo
 */
public interface HttpCaller {

    /**
     * 调用
     * @param hostList URL前缀集合
     * @param path URL路径
     * @param uriVariables URL中的变量
     * @param method 请求方法
     * @param requestEntity 请求实体
     * @param responseClass 响应对象类型
     * @return 响应对象
     * @param <T> 请求对象类型
     * @param <R> 响应对象类型
     */
    <T, R> R call(List<String> hostList, String path, Map<String, ?> uriVariables, HttpMethod method, HttpEntity<T> requestEntity, Class<R> responseClass);

    /**
     * 调用
     * @param hostList URL前缀集合
     * @param path URL路径
     * @param uriVariables URL中的变量
     * @param method 请求方法
     * @param requestEntity 请求实体
     * @param responseType 响应对象类型
     * @return 响应对象
     * @param <T> 请求对象类型
     * @param <R> 响应对象类型
     */
    <T, R> R call(List<String> hostList, String path, Map<String, ?> uriVariables, HttpMethod method, HttpEntity<T> requestEntity, ParameterizedTypeReference<R> responseType);

}
