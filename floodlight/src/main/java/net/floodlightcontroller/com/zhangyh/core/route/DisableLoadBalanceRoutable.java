package net.floodlightcontroller.com.zhangyh.core.route;

import net.floodlightcontroller.com.zhangyh.core.resource.DisableLoadBalanceResource;
import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/24  11:04
 */
public class DisableLoadBalanceRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/json", DisableLoadBalanceResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/loadbalance/disable";
    }
}
