package net.floodlightcontroller.com.zhangyh.service;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;

import java.util.Collection;

/**
 * @Author: lsw
 * @desc
 * @date: 2023/5/8  10:11
 */
public interface DeviceMonitorService extends IFloodlightService {
    /**
     * 获取当前的所有设备详情
     * @return
     */
    Collection<? extends IDevice> getAllDevice();
}
