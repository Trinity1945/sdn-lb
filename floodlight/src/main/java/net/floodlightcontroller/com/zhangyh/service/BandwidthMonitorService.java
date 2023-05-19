package net.floodlightcontroller.com.zhangyh.service;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;

import java.util.Map;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/5  20:45
 */
public interface BandwidthMonitorService extends IFloodlightService {

    /**
     * 获取当前设备的带宽详情
     * @return
     */
    Map<NodePortTuple, SwitchPortBandwidth> getAllBandwidth();
}
