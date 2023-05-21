package net.floodlightcontroller.com.zhangyh;

import net.floodlightcontroller.com.zhangyh.model.SwitchNode;
import net.floodlightcontroller.com.zhangyh.route.SwitchLinkRoutable;
import net.floodlightcontroller.com.zhangyh.route.TopologyRoutable;
import net.floodlightcontroller.com.zhangyh.service.TopologyMonitorService;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.Link;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import org.projectfloodlight.openflow.types.DatapathId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangyh
 * @desc 获取拓扑结构并暴露为RestAPI
 * @date: 2023/4/5  15:05
 */
public class TopologyMonitor implements IFloodlightModule, ITopologyListener, TopologyMonitorService {

    private ITopologyService topologyService;
    private IRestApiService iRestApiService;
    private IThreadPoolService threadPoolService;
    private ILinkDiscoveryService iLinkDiscoveryService;

    IOFSwitchService switchService;

    IStatisticsService statisticsService;

    private static Logger log = LoggerFactory.getLogger(TopologyMonitor.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        // 声明 MyModule 模块提供的服务接口
        List<Class<? extends IFloodlightService>> services = new ArrayList<>();
        services.add(TopologyMonitorService.class);
        return services;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(TopologyMonitorService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        // MyModule 模块依赖的其他服务接口
        List<Class<? extends IFloodlightService>> dependencies = new ArrayList<>();
        dependencies.add(ITopologyService.class);
        dependencies.add(IRestApiService.class);
        dependencies.add(IThreadPoolService.class);
        dependencies.add(IStatisticsService.class);
        dependencies.add(ILinkDiscoveryService.class);
        dependencies.add(IOFSwitchService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        topologyService= context.getServiceImpl(ITopologyService.class);
        iRestApiService=context.getServiceImpl(IRestApiService.class);
        threadPoolService=context.getServiceImpl(IThreadPoolService.class);
        iLinkDiscoveryService=context.getServiceImpl(ILinkDiscoveryService.class);
        statisticsService=context.getServiceImpl(IStatisticsService.class);
        switchService=context.getServiceImpl(IOFSwitchService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        // 获取 REST API 组件，并注册 REST API 资源类
        statisticsService.collectStatistics(true);
        int portStatsInterval=10;
        threadPoolService
                .getScheduledExecutor()
                .scheduleAtFixedRate(this::getSwitchLinks, portStatsInterval, portStatsInterval, TimeUnit.SECONDS);
        log.warn("Statistics collection thread(s) started");
        // 注册 REST API
        addRestletRoutable();
    }

    public Map<DatapathId, Set<Link>> getAllLink(){
        return topologyService.getAllLinks();
    }

    /**
     * 获取链路连接情况与时延等
     * @return
     */
    public List<SwitchNode> getSwitchLinks(){
        log.info("交换机拓扑获取---------------------->>>");
        Map<DatapathId, Set<Link>> switchLinks = iLinkDiscoveryService.getSwitchLinks();
        List<SwitchNode> res=new ArrayList<>();
        switchLinks.forEach(((datapathId, links) -> {
            SwitchNode topology = new SwitchNode();
            topology.setSwitchDPID(datapathId.toString());
            links.forEach(link -> {
                SwitchNode.Links links1 = new SwitchNode.Links();
                links1.setSrcSwitch(link.getSrc().toString());
                links1.setSrcPort(link.getSrcPort().getPortNumber());
                links1.setDstSwitch(link.getDst().toString());
                links1.setDstPort(link.getDstPort().getPortNumber());
                links1.setLatency(link.getLatency().getValue());
                SwitchPortBandwidth bw = statisticsService.getBandwidthConsumption(datapathId, link.getSrcPort());
                if(bw!=null){
                    long rx = bw.getBitsPerSecondRx().getValue();
                    long tx = bw.getBitsPerSecondTx().getValue();
                    long speed = bw.getLinkSpeedBitsPerSec().getValue(); // 端口速度转换为 Mbps
                    double rate = (rx + tx) / (double)(speed); // 将除数转换为double类型，确保计算结果为小数
                    DecimalFormat df = new DecimalFormat("#.###"); // 创建DecimalFormat对象，设置保留三位小数
                    links1.setRate(df.format(rate * 100));
//                    System.out.println("交换机："+bw.getSwitchId()+"端口"+bw.getSwitchPort()+"带宽大小 TX="+bw.getBitsPerSecondTx().getValue()+"RX="+bw.getBitsPerSecondRx().getValue()+"利用率："+df.format(rate * 100));
                }
                topology.getLinks().add(links1);
            });
            res.add(topology);
        }));
        return res;
    }


    //注册我们的API
    protected void addRestletRoutable() {
        iRestApiService.addRestletRoutable(new TopologyRoutable());
        iRestApiService.addRestletRoutable(new SwitchLinkRoutable());
    }

    @Override
    public void topologyChanged(List<ILinkDiscovery.LDUpdate> linkUpdates) {
            log.info("拓扑结构正在改变--------------------->>>");
    }
}
