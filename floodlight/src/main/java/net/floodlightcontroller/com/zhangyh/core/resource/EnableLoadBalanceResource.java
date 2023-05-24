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
 * @date: 2023/5/24  10:58
 */
public class EnableLoadBalanceResource extends ServerResource {

    private static final Logger log = LoggerFactory.getLogger(EnableLoadBalanceResource.class);

    @Get("json")
    public JSONObject retrieve() {
        log.info("----------------开启负载均衡----------------");
        LoadBalanceService service = (LoadBalanceService)getContext().getAttributes().get(LoadBalanceService.class.getCanonicalName());
        service.enableLoadBalance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("loadBalance","开启负载均衡");
        return jsonObject;
    }
}
