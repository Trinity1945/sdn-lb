package net.floodlightcontroller.com.zhangyh.service;

import net.floodlightcontroller.com.zhangyh.model.SwitchNode;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.Link;
import org.projectfloodlight.openflow.types.DatapathId;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/5  21:45
 */
public interface TopologyMonitorService extends IFloodlightService {

    /**
     * 获取所有连接
     * @return
     */
    Map<DatapathId, Set<Link>> getAllLink();


    List<SwitchNode> getSwitchLinks();
}
