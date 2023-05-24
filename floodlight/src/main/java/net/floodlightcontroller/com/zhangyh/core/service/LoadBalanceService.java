package net.floodlightcontroller.com.zhangyh.core.service;

import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/24  10:54
 */
public interface LoadBalanceService extends IFloodlightService {

    /**
     * 开启负载均衡
     * @return
     */
    void enableLoadBalance();

    /**
     * 关闭负载均衡
     * @return
     */
    void disableLoadBalance();
}
