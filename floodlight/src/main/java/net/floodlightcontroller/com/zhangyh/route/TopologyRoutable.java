package net.floodlightcontroller.com.zhangyh.route;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Router;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/5  15:35
 */
public class TopologyRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);

        // 处理 GET 请求，返回 "Hello, World!"
        router.attach("/hello", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                response.setEntity("Hello, World!", MediaType.TEXT_PLAIN);
                response.setStatus(Status.SUCCESS_OK);
            }
        });

        return router;
    }

    @Override
    public String basePath() {
        return "/mynamespace";
    }
}
