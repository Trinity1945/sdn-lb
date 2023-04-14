package net.floodlightcontroller.com.zhangyh;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangyh
 * @desc 主机监控
 * @date: 2023/4/6  20:40
 */
public class HostMonitor implements IFloodlightModule {

    IDeviceService deviceService;

    IThreadPoolService threadPoolService;


    private IRestApiService iRestApiService;

    private static Logger log = LoggerFactory.getLogger(TopologyMonitor.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        List<Class<? extends IFloodlightService>> dependencies = new ArrayList<>();
        dependencies.add(IRestApiService.class);
        dependencies.add(IThreadPoolService.class);
        dependencies.add(IDeviceService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        deviceService = context.getServiceImpl(IDeviceService.class);
        iRestApiService=context.getServiceImpl(IRestApiService.class);
        threadPoolService=context.getServiceImpl(IThreadPoolService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        int portStatsInterval=60;
        threadPoolService
                .getScheduledExecutor()
                .scheduleAtFixedRate(this::getAllHost, portStatsInterval, portStatsInterval, TimeUnit.SECONDS);
        log.warn("Statistics collection thread(s) started");
    }

    //注册我们的API
    protected void addRestletRoutable() {

    }

    public void getAllHost() {
        log.info("Device---------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        final Collection<? extends IDevice> allDevices = deviceService.getAllDevices();
        allDevices.forEach(device -> {
            log.info("设备：{}", device);
        });
    }
}
