package net.floodlightcontroller.com.zhangyh.Resource;

import net.floodlightcontroller.com.zhangyh.service.TopologyMonitorService;
import net.floodlightcontroller.linkdiscovery.Link;
import org.projectfloodlight.openflow.types.DatapathId;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.Map;
import java.util.Set;

/**
 * @Author: lsw
 * @desc
 * @date: 2023/5/8  10:00
 */
public class TopologyResource extends ServerResource {

    @Get("json")
    public Map<DatapathId, Set<Link>> retrieve() {
        TopologyMonitorService service = (TopologyMonitorService)getContext().getAttributes().get(TopologyMonitorService.class.getCanonicalName());
        return service.getAllLink();
    }
}
