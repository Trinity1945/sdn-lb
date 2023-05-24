package net.floodlightcontroller.com.zhangyh.core.resource;

import net.floodlightcontroller.com.zhangyh.core.service.LoadBalanceService;
import org.json.simple.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/24  11:04
 */
public class DisableLoadBalanceResource extends ServerResource {

    private static final Logger log = LoggerFactory.getLogger(DisableLoadBalanceResource.class);

    @Get("json")
    public JSONObject retrieve() {
        log.info("----------------关闭负载均衡----------------");
        LoadBalanceService service = (LoadBalanceService)getContext().getAttributes().get(LoadBalanceService.class.getCanonicalName());
        service.disableLoadBalance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("loadBalance","关闭负载均衡");
        return jsonObject;
    }
}
