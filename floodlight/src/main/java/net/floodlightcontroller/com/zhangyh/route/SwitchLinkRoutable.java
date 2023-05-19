package net.floodlightcontroller.com.zhangyh.route;

import net.floodlightcontroller.com.zhangyh.Resource.SwitchLinkResource;
import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @Author: lsw
 * @desc
 * @date: 2023/5/8  15:06
 */
public class SwitchLinkRoutable  implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/json", SwitchLinkResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/wm/monitor/switch/link";
    }
}
