package com.mogudiandian.http.caller;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡-随机算法
 *
 * @author sunbo
 */
public class LoadBalancerRandomImpl implements LoadBalancer {

    @Override
    public String select(List<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

}
