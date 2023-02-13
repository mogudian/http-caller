package com.mogudiandian.http.caller;

import com.coral.base.util.ip.IpAddressUtils;

import java.net.Inet4Address;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡-随机算法
 *
 * @author sunbo
 */
public class LoadBalancerIpHashImpl implements LoadBalancer {

    private static final int localIpHashCode;

    static {
        localIpHashCode = IpAddressUtils.getLocalIp4Address()
                                        .map(Inet4Address::getHostAddress)
                                        .orElse("127.0.0.1")
                                        .hashCode();
    }

    @Override
    public String select(List<String> list) {
        return list.get(localIpHashCode % list.size());
    }

}
