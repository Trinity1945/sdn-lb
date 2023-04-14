package net.floodlightcontroller.com.zhangyh;

import net.floodlightcontroller.com.zhangyh.route.TopologyRoutable;
import net.floodlightcontroller.com.zhangyh.service.TopologMonitorService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.Link;
import net.floodlightcontroller.linkdiscovery.internal.LinkInfo;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import org.projectfloodlight.openflow.types.DatapathId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangyh
 * @desc 获取拓扑结构并暴露为RestAPI
 * @date: 2023/4/5  15:05
 */
public class TopologyMonitor implements IFloodlightModule, ITopologyListener, TopologMonitorService {

    private ITopologyService topologyService;
    private IRestApiService iRestApiService;
    private IThreadPoolService threadPoolService;
    private ILinkDiscoveryService iLinkDiscoveryService;

    private static Logger log = LoggerFactory.getLogger(TopologyMonitor.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        // 声明 MyModule 模块提供的服务接口
        List<Class<? extends IFloodlightService>> services = new ArrayList<>();
        services.add(TopologMonitorService.class);
        return services;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(TopologMonitorService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        // MyModule 模块依赖的其他服务接口
        List<Class<? extends IFloodlightService>> dependencies = new ArrayList<>();
        dependencies.add(ITopologyService.class);
        dependencies.add(IRestApiService.class);
        dependencies.add(IThreadPoolService.class);
        dependencies.add(ILinkDiscoveryService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        topologyService= context.getServiceImpl(ITopologyService.class);
        iRestApiService=context.getServiceImpl(IRestApiService.class);
        threadPoolService=context.getServiceImpl(IThreadPoolService.class);
        iLinkDiscoveryService=context.getServiceImpl(ILinkDiscoveryService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        // 获取 REST API 组件，并注册 REST API 资源类
        int portStatsInterval=60;
        threadPoolService
                .getScheduledExecutor()
                .scheduleAtFixedRate(this::getAllLink, portStatsInterval, portStatsInterval, TimeUnit.SECONDS);
        log.warn("Statistics collection thread(s) started");
        getAllLink();
        // 注册 REST API
        addRestletRoutable();
    }

    public Map<DatapathId, Set<Link>> getAllLink(){
        log.info("拓扑获取---------------------->>>>>>>>>>>>>");
        final Map<DatapathId, Set<Link>> allLinks = topologyService.getAllLinks();

//        allLinks.entrySet().forEach(k->{
//            log.info("拓扑结构：{}-----{}",k.getKey(),k.getValue());
//        });
        final Map<Link, LinkInfo> links = iLinkDiscoveryService.getLinks();
        final Map<DatapathId, Set<Link>> switchLinks = iLinkDiscoveryService.getSwitchLinks();
        switchLinks.entrySet().forEach(k->{
            log.info("：键：{}-----{}",k.getKey(),k.getValue());
        });
        return allLinks;
    }


    //注册我们的API
    protected void addRestletRoutable() {
        iRestApiService.addRestletRoutable(new TopologyRoutable());
    }

    @Override
    public void topologyChanged(List<ILinkDiscovery.LDUpdate> linkUpdates) {

    }
}
