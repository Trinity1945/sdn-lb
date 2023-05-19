package net.floodlightcontroller.com.zhangyh.Resource;

import net.floodlightcontroller.com.zhangyh.model.SwitchNode;
import net.floodlightcontroller.com.zhangyh.service.TopologyMonitorService;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.List;

/**
 * @Author: lsw
 * @desc
 * @date: 2023/5/8  15:07
 */
public class SwitchLinkResource extends ServerResource {
    @Get("json")
    public List<SwitchNode> retrieve() {
        TopologyMonitorService service = (TopologyMonitorService)getContext().getAttributes().get(TopologyMonitorService.class.getCanonicalName());
        return service.getSwitchLinks();
    }
}
