package net.floodlightcontroller.com.zhangyh.route;

import net.floodlightcontroller.com.zhangyh.Resource.BandWidthMonitorResource;
import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/5  20:52
 */
public class BandwidthMonitorRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/json", BandWidthMonitorResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/wm/bandwidth";
    }
}
