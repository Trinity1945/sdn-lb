package net.floodlightcontroller.com.zhangyh.Resource;

import net.floodlightcontroller.com.zhangyh.service.DeviceMonitorService;
import net.floodlightcontroller.devicemanager.IDevice;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.Collection;

/**
 * @Author: lsw
 * @desc
 * @date: 2023/5/8  10:17
 */
public class DeviceMonitorResource extends ServerResource {

    /**
     * 获取所有的设备信息
     * @return
     */
    @Get("json")
    public Collection<? extends IDevice> retrieve() {
        DeviceMonitorService service = (DeviceMonitorService)getContext().getAttributes().get(DeviceMonitorService.class.getCanonicalName());
        return service.getAllDevice();
    }
}
