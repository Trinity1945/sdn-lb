package net.floodlightcontroller.com.zhangyh.Resource;

import net.floodlightcontroller.com.zhangyh.service.BandwidthMonitorService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.Map;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/5  20:50
 */
public class BandWidthMonitorResource extends ServerResource {
    @Get("json")
    public Map<NodePortTuple, SwitchPortBandwidth> retrieve() {
        BandwidthMonitorService service = (BandwidthMonitorService)getContext().getAttributes().get(BandwidthMonitorService.class.getCanonicalName());
        return service.getAllBandwidth();
    }
}
