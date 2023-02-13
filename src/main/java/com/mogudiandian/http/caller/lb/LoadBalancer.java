package com.mogudiandian.http.caller.lb;

import java.util.List;

/**
 * 负载均衡器
 *
 * @author sunbo
 */
public interface LoadBalancer {

    /**
     * 选择
     * @param list 集合
     * @return 选中的元素
     */
    String select(List<String> list);

}
