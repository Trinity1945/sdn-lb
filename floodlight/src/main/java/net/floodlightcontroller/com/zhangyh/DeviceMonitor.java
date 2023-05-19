package net.floodlightcontroller.com.zhangyh;

import net.floodlightcontroller.com.zhangyh.route.DeviceMonitorRoutable;
import net.floodlightcontroller.com.zhangyh.service.DeviceMonitorService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangyh
 * @desc 主机监控
 * @date: 2023/4/6  20:40
 */
public class DeviceMonitor implements IFloodlightModule , DeviceMonitorService {

    IDeviceService deviceService;
    IThreadPoolService threadPoolService;

    private IRestApiService iRestApiService;

    IStatisticsService iStatisticsService;

    private static Logger log = LoggerFactory.getLogger(TopologyMonitor.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(DeviceMonitorService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(DeviceMonitorService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        List<Class<? extends IFloodlightService>> dependencies = new ArrayList<>();
        dependencies.add(IRestApiService.class);
        dependencies.add(IThreadPoolService.class);
        dependencies.add(IDeviceService.class);
        dependencies.add(IStatisticsService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        deviceService = context.getServiceImpl(IDeviceService.class);
        iRestApiService=context.getServiceImpl(IRestApiService.class);
        threadPoolService=context.getServiceImpl(IThreadPoolService.class);
        iStatisticsService=context.getServiceImpl(IStatisticsService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        int portStatsInterval=10;
        threadPoolService
                .getScheduledExecutor()
                .scheduleAtFixedRate(this::getAllDevice, portStatsInterval, portStatsInterval, TimeUnit.SECONDS);
        log.warn("Statistics collection thread(s) started");
        addRestletRoutable();
    }

    //注册我们的API
    protected void addRestletRoutable() {
        iRestApiService.addRestletRoutable(new DeviceMonitorRoutable());
    }

    public Collection<? extends IDevice> getAllDevice() {
        log.info("Device---------------------->>>");
        iStatisticsService.collectStatistics(true);
        return deviceService.getAllDevices();
    }
}
