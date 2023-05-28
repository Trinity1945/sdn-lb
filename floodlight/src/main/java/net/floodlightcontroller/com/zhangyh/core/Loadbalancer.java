package net.floodlightcontroller.com.zhangyh.core;

import com.alibaba.fastjson.JSON;
import net.floodlightcontroller.com.zhangyh.core.algorithm.AntColonyOptimization;
import net.floodlightcontroller.com.zhangyh.core.domain.Client;
import net.floodlightcontroller.com.zhangyh.core.domain.Edge;
import net.floodlightcontroller.com.zhangyh.core.domain.LoadBalanceClient;
import net.floodlightcontroller.com.zhangyh.core.route.DisableLoadBalanceRoutable;
import net.floodlightcontroller.com.zhangyh.core.route.EnableLoadBalanceRoutable;
import net.floodlightcontroller.com.zhangyh.core.service.LoadBalanceService;
import net.floodlightcontroller.com.zhangyh.model.SwitchNode;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.debugcounter.IDebugCounter;
import net.floodlightcontroller.debugcounter.IDebugCounterService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.Link;
import net.floodlightcontroller.packet.*;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Path;
import net.floodlightcontroller.routing.PathId;
import net.floodlightcontroller.staticentry.IStaticEntryPusherService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.FlowModUtils;
import net.floodlightcontroller.util.OFMessageUtils;
import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.*;
import org.python.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangyh
 * @desc 负载均衡
 * @date: 2023/4/14  19:09
 */
public class Loadbalancer implements IFloodlightModule, IOFMessageListener, LoadBalanceService {

    protected static Logger log = LoggerFactory.getLogger(Loadbalancer.class);

    protected IFloodlightProviderService floodlightProviderService;
    protected IRestApiService restApiService;

    protected IDebugCounterService debugCounterService;
    private IDebugCounter counterPacketOut;
    private IDebugCounter counterPacketIn;
    protected IDeviceService deviceManagerService;
    protected IRoutingService routingEngineService;
    protected ITopologyService topologyService;

    private ILinkDiscoveryService iLinkDiscoveryService;
    protected IStaticEntryPusherService sfpService;
    protected IOFSwitchService switchService;
    protected IStatisticsService statisticsService;
    protected IThreadPoolService threadService;

    protected static int LB_PRIORITY = 33333;

    /**
     * 缓存路径
     */
    List<SwitchNode> topoInstence;

    private Boolean enableLoadBalance = false;
    /**
     * 蚁群解空间
     */
    Map<String, List<Edge>> graph;

    private Map<PathId, List<Path>> pathcache;

    private static final Integer TIME_OUT = 5;

    private Set<LoadBalanceClient> loadBalanceClients;
    private Set<OFPacketIn> packetIns;

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<>();
        l.add(LoadBalanceService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<>();
        m.put(LoadBalanceService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<>();
        l.add(IFloodlightProviderService.class);
        l.add(IRestApiService.class);
        l.add(IOFSwitchService.class);
        l.add(IDeviceService.class);
        l.add(IDebugCounterService.class);
        l.add(ITopologyService.class);
        l.add(IRoutingService.class);
        l.add(IStaticEntryPusherService.class);
        l.add(IStatisticsService.class);
        l.add(ILinkDiscoveryService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        routingEngineService = context.getServiceImpl(IRoutingService.class);
        restApiService = context.getServiceImpl(IRestApiService.class);
        floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
        debugCounterService = context.getServiceImpl(IDebugCounterService.class);
        topologyService = context.getServiceImpl(ITopologyService.class);
        sfpService = context.getServiceImpl(IStaticEntryPusherService.class);
        switchService = context.getServiceImpl(IOFSwitchService.class);
        statisticsService = context.getServiceImpl(IStatisticsService.class);
        threadService = context.getServiceImpl(IThreadPoolService.class);
        deviceManagerService = context.getServiceImpl(IDeviceService.class);
        iLinkDiscoveryService = context.getServiceImpl(ILinkDiscoveryService.class);

        topoInstence = new CopyOnWriteArrayList<>();
        graph = new ConcurrentHashMap<>();
        pathcache = new ConcurrentHashMap<>();
        packetIns = new CopyOnWriteArraySet<>();
        loadBalanceClients = new CopyOnWriteArraySet<>();
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProviderService.addOFMessageListener(OFType.PACKET_IN, this);
        debugCounterService.registerModule(this.getName());
        counterPacketOut = debugCounterService.registerCounter(this.getName(), "packet-outs-written", "Packet outs written by the LoadBalancer", IDebugCounterService.MetaData.WARN);
        counterPacketIn = debugCounterService.registerCounter(this.getName(), "packet-ins-received", "Packet ins received by the LoadBalancer", IDebugCounterService.MetaData.WARN);
        int portStatsInterval = 10;
        int loadBalanceStatsInterval = 10;
        threadService
                .getScheduledExecutor()
                .scheduleAtFixedRate(this::clearCache, portStatsInterval, portStatsInterval, TimeUnit.SECONDS);
//        threadService
//                .getScheduledExecutor()
//                .scheduleAtFixedRate(this::loadbalance, loadBalanceStatsInterval, loadBalanceStatsInterval, TimeUnit.SECONDS);
        addRestletRoutable();
    }

    public void clearCache() {
        log.info("路径缓存清空--------------------------------------》");
        pathcache.clear();
        log.info("拓扑构建--------------------------------------》");
        Map<DatapathId, Set<Link>> topologyLinks = iLinkDiscoveryService.getSwitchLinks();
        buildGraph(topologyLinks);
    }

    public void loadbalance() {
        log.info("任务调度中");
        if (enableLoadBalance) {
            log.info("负载均衡中:{}", loadBalanceClients.size());
            log.info("Package In Numbenr:{}", packetIns.size());
            loadBalanceClients.forEach(client -> {
                log.info("负载起点=={}-端口{}---负载终点=={}-端口{}", client.getClient().getIpAddress(), client.getClient().getSrcPort(),
                        client.getClient().getTargetIpAddress(), client.getClient().getTargetPort());
                doLoadBalance(client.getClient(), client.getSw(), client.getPi());
            });
        }
        loadBalanceClients.clear();
        log.info("调度结束");
    }

    @Override
    public String getName() {
        return Loadbalancer.class.getSimpleName();
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        switch (msg.getType()) {
            case PACKET_IN:
                return processPacketIn(sw, (OFPacketIn) msg, cntx);
            default:
                break;
        }
        log.warn("Received unexpected message {}", msg);
        return Command.CONTINUE;
    }

    private net.floodlightcontroller.core.IListener.Command processPacketIn(IOFSwitch sw, OFPacketIn pi, FloodlightContext cntx) {
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        IPacket pkt = eth.getPayload();
        // 仅负载均衡 IPv4 数据包
        if (pkt instanceof IPv4) {
            IPv4 ip_pkt = (IPv4) pkt;
            // 构造 IP 客户端信息
            Client client = new Client();
            client.setIpAddress(ip_pkt.getSourceAddress());
            client.setNw_proto(ip_pkt.getProtocol());
            client.setTargetIpAddress(ip_pkt.getDestinationAddress());
            if (ip_pkt.getPayload() instanceof TCP) {
                TCP tcp_pkt = (TCP) ip_pkt.getPayload();
                client.setSrcPort(tcp_pkt.getSourcePort());
                client.setTargetPort(tcp_pkt.getDestinationPort());
            }
            if (ip_pkt.getPayload() instanceof UDP) {
                UDP udp_pkt = (UDP) ip_pkt.getPayload();
                client.setSrcPort(udp_pkt.getSourcePort());
                client.setTargetPort(udp_pkt.getDestinationPort());
            }
            if (ip_pkt.getPayload() instanceof ICMP) {
                client.setSrcPort(TransportPort.of(8));
                client.setTargetPort(TransportPort.of(0));
            }
            if (statisticsService != null && enableLoadBalance) {
                statisticsService.collectStatistics(true);
            }
            if (enableLoadBalance) {
                LoadBalanceClient loadBalanceClient = new LoadBalanceClient(client, sw, pi);
                loadBalanceClients.add(loadBalanceClient);
                packetIns.add(pi);
                loadbalance();
            }
        }
        return Command.CONTINUE;
    }

    protected void addRestletRoutable() {
        restApiService.addRestletRoutable(new EnableLoadBalanceRoutable());
        restApiService.addRestletRoutable(new DisableLoadBalanceRoutable());
    }

    /**
     * 负载均衡链路计算
     *
     * @param client 负载均衡的客户端信息
     * @param sw     源交换机
     * @param pi     消息
     */
    private void doLoadBalance(Client client, IOFSwitch sw, OFPacketIn pi) {
        IDevice srcDevice = null;
        IDevice dstDevice = null;
        //获取所有设备
        Collection<? extends IDevice> allDevices = deviceManagerService.getAllDevices();

        for (IDevice d : allDevices) {
            for (int j = 0; j < d.getIPv4Addresses().length; j++) {
                if (srcDevice == null && client.getIpAddress().equals(d.getIPv4Addresses()[j]))
                    srcDevice = d;
                if (dstDevice == null && client.getTargetIpAddress().equals(d.getIPv4Addresses()[j])) {
                    dstDevice = d;
                }
                if (srcDevice != null && dstDevice != null)
                    break;
            }
        }

        if (srcDevice == null || dstDevice == null) return;
        log.info("源设备：{}", JSON.toJSONString(srcDevice.getIPv4Addresses()));
        log.info("目标设备：{}", JSON.toJSONString(dstDevice.getIPv4Addresses()));
        //获取设备所在集群
        DatapathId srcIsland = topologyService.getClusterId(sw.getId());

        if (srcIsland == null) {
            log.debug("没有找到openflow集群 {}/{}",
                    sw.getId().toString(), pi.getInPort());
            return;
        }
        boolean on_same_island = false;
        boolean on_same_if = false;

        //验证设备是否在同一个集群中
        for (SwitchPort dstDap : dstDevice.getAttachmentPoints()) {
            DatapathId dstSwDpid = dstDap.getNodeId();
            DatapathId dstIsland = topologyService.getClusterId(dstSwDpid);
            if ((dstIsland != null) && dstIsland.equals(srcIsland)) {
                log.info("on same island");
                on_same_island = true;
                if ((sw.getId().equals(dstSwDpid)) && OFMessageUtils.getInPort(pi).equals(dstDap.getPortId())) {
                    on_same_if = true;
                    log.info("on_same_if");
                }
                break;
            }
        }

        if (!on_same_island) {
            if (log.isTraceEnabled()) {
                log.trace("No first hop island found for destination " +
                        "device {}, Action = flooding", dstDevice);
            }
            return;
        }

        if (on_same_if) {
            if (log.isTraceEnabled()) {
                log.trace("Both source and destination are on the same " +
                                "switch/port {}/{}, Action = NOP",
                        sw.getId(), pi.getInPort());
            }
            return;
        }

        SwitchPort[] srcDaps = srcDevice.getAttachmentPoints();
        Arrays.sort(srcDaps, clusterIdComparator);
        SwitchPort[] dstDaps = dstDevice.getAttachmentPoints();
        Arrays.sort(dstDaps, clusterIdComparator);

        log.info("开始计算最佳路径-----------------------》》》");
        int iSrcDaps = 0, iDstDaps = 0;
        while ((iSrcDaps < srcDaps.length) && (iDstDaps < dstDaps.length)) {
            SwitchPort srcDap = srcDaps[iSrcDaps];
            SwitchPort dstDap = dstDaps[iDstDaps];
            DatapathId srcCluster =
                    topologyService.getClusterId(srcDap.getNodeId());
            DatapathId dstCluster =
                    topologyService.getClusterId(dstDap.getNodeId());
            int srcVsDest = srcCluster.compareTo(dstCluster);
            if (srcVsDest == 0) {
                if (!srcDap.equals(dstDap) &&
                        (srcCluster != null) &&
                        (dstCluster != null)) {
                    log.info("迭代路径中。。。。。。。。。。。。");
                    //最佳路径获取
                    Path routeIn = getPath(srcDap.getNodeId(),
                            srcDap.getPortId(),
                            dstDap.getNodeId(),
                            dstDap.getPortId());
                    Path routeOut = getPath(dstDap.getNodeId(),
                            dstDap.getPortId(),
                            srcDap.getNodeId(),
                            srcDap.getPortId());

                    if (!routeIn.getPath().isEmpty()) {
                        log.info("蚁群增强算法入路径");
                        log.info("起点：{}，端口：{}", srcDap.getNodeId(), srcDap.getPortId());
                        log.info("终点：{}，端口：{}", dstDap.getNodeId(), dstDap.getPortId());
                        routeIn.getPath().forEach(e -> {
                            log.info("ACO入路由：{}====入端口：{}", e.getNodeId().toString(), e.getPortId());
                        });
                        pushStaticRoute(true, routeIn, client, sw);
                    } else {
                        log.info("迪杰斯特拉算法容错");
                        routeIn =
                                routingEngineService.getPath(srcDap.getNodeId(),
                                        srcDap.getPortId(),
                                        dstDap.getNodeId(),
                                        dstDap.getPortId());

                        if (!routeIn.getPath().isEmpty()) {
                            log.info("起点：{}，端口：{}", srcDap.getNodeId(), srcDap.getPortId());
                            log.info("终点：{}，端口：{}", dstDap.getNodeId(), dstDap.getPortId());
                            routeIn.getPath().forEach(e -> {
                                log.info("ACO入路由：{}====入端口：{}", e.getNodeId().toString(), e.getPortId());
                            });
                            pushStaticRoute(true, routeIn, client, sw);
                            PathId pathId = new PathId(srcDap.getNodeId(), dstDap.getNodeId());
                            List<Path> paths = pathcache.get(pathId);
                            if (paths == null || paths.size() == 0) {
                                List<Path> path = new ArrayList<>();
                                routeIn.getPath().remove(0);
                                routeIn.getPath().remove(routeIn.getPath().size() - 1);
                                path.add(routeIn);
                                pathcache.put(pathId, path);
                            }else{
                                paths.clear();
                                List<Path> path = new ArrayList<>();
                                routeIn.getPath().remove(0);
                                routeIn.getPath().remove(routeIn.getPath().size() - 1);
                                path.add(routeIn);
                                pathcache.put(pathId, path);
                            }
                        }
                    }
                    log.info("---------------------------------------------------------------------------------");
                    if (!routeOut.getPath().isEmpty()) {
                        log.info("蚁群增强算法出路径");
                        pushStaticRoute(false, routeOut, client, sw);
                        log.info("起点：{}，端口：{}", dstDap.getNodeId(), dstDap.getPortId());
                        log.info("终点：{}，端口：{}", srcDap.getNodeId(), srcDap.getPortId());
                        routeOut.getPath().forEach(e -> {
                            log.info("ACO出路由：{}~~~~出端口：{}", e.getNodeId().toString(), e.getPortId());
                        });
                    } else {
                        log.info("迪杰斯特拉算法容错");
                        routeOut =
                                routingEngineService.getPath(dstDap.getNodeId(),
                                        dstDap.getPortId(),
                                        srcDap.getNodeId(),
                                        srcDap.getPortId());
                        if (!routeOut.getPath().isEmpty()) {
                            log.info("起点：{}，端口：{}", dstDap.getNodeId(), dstDap.getPortId());
                            log.info("终点：{}，端口：{}", srcDap.getNodeId(), srcDap.getPortId());
                            routeOut.getPath().forEach(e -> {
                                log.info("ACO出路由：{}~~~~出端口：{}", e.getNodeId().toString(), e.getPortId());
                            });
                            pushStaticRoute(false, routeOut, client, sw);
                            PathId pathId = new PathId(dstDap.getNodeId(), srcDap.getNodeId());
                            List<Path> paths = pathcache.get(pathId);
                            if (paths == null || paths.size() == 0) {
                                List<Path> path = new ArrayList<>();
                                routeOut.getPath().remove(0);
                                routeOut.getPath().remove(routeOut.getPath().size() - 1);
                                path.add(routeOut);
                                pathcache.put(pathId, path);
                            }else{
                                paths.clear();
                                List<Path> path = new ArrayList<>();
                                routeOut.getPath().remove(0);
                                routeOut.getPath().remove(routeIn.getPath().size() - 1);
                                path.add(routeOut);
                                pathcache.put(pathId, path);
                            }
                        }
                    }
                }
                iSrcDaps++;
                iDstDaps++;
            } else if (srcVsDest < 0) {
                iSrcDaps++;
            } else {
                iDstDaps++;
            }
        }
        log.debug("最佳路径计算完毕-----------------------》》》");
        return;
    }

    /**
     * 流表下发
     */
    private void pushStaticRoute(boolean inBound, Path route, Client client, IOFSwitch pinSwitch) {
        List<NodePortTuple> path = route.getPath();
        if (path.size() > 0) {
            for (int i = 0; i < path.size(); i += 2) {
                DatapathId sw = path.get(i).getNodeId();
                String entryName;
                Match.Builder mb = pinSwitch.getOFFactory().buildMatch();
                ArrayList<OFAction> actions = new ArrayList<>();

                OFFlowMod.Builder fmb = pinSwitch.getOFFactory().buildFlowAdd();

                fmb.setIdleTimeout(TIME_OUT);
                fmb.setHardTimeout(FlowModUtils.INFINITE_TIMEOUT);
                fmb.setBufferId(OFBufferId.NO_BUFFER);
                fmb.setOutPort(OFPort.ANY);
                fmb.setCookie(U64.of(0));
                fmb.setPriority(FlowModUtils.PRIORITY_MAX);

                if (inBound) {
                    entryName = "inbound-vip-client-" + client.getIpAddress()
                            + "-srcport-" + client.getSrcPort() + "-dstport-" + client.getTargetPort()
                            + "-srcswitch-" + path.get(0).getNodeId() + "-sw-" + sw;
                    mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            .setExact(MatchField.IP_PROTO, client.getNw_proto())
                            .setExact(MatchField.IPV4_SRC, client.getIpAddress())
                            .setExact(MatchField.IPV4_DST, client.getTargetIpAddress())
                            .setExact(MatchField.IN_PORT, path.get(i).getPortId());
                    if (client.getNw_proto().equals(IpProtocol.TCP)) {
                        mb.setExact(MatchField.TCP_SRC, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.UDP)) {
                        mb.setExact(MatchField.UDP_SRC, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.SCTP)) {
                        mb.setExact(MatchField.SCTP_SRC, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.ICMP)) {

                    } else {
                        log.error("未知IP协议 {} 在推送静态路由时.", client.getNw_proto());
                    }

                    if (sw.equals(pinSwitch.getId())) {
                        actions.add(pinSwitch.getOFFactory().actions().output(path.get(i + 1).getPortId(), Integer.MAX_VALUE));
                    } else {
                        try {
                            actions.add(switchService.getSwitch(path.get(i + 1).getNodeId()).getOFFactory().actions().output(path.get(i + 1).getPortId(), Integer.MAX_VALUE));
                        } catch (NullPointerException e) {
                            log.error("无法向离线交换机安装负载均衡器流表规则 {}.", path.get(i + 1).getNodeId());
                        }
                    }

                } else {
                    entryName = "inbound-lb-client-" + client.getIpAddress()
                            + "-srcport-" + client.getSrcPort() + "-dstport-" + client.getTargetPort()
                            + "-srcswitch-" + path.get(0).getNodeId() + "-sw-" + sw;
                    mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            .setExact(MatchField.IP_PROTO, client.getNw_proto())
                            .setExact(MatchField.IPV4_SRC, client.getTargetIpAddress())
                            .setExact(MatchField.IPV4_DST, client.getIpAddress())
                            .setExact(MatchField.IN_PORT, path.get(i).getPortId());
                    if (client.getNw_proto().equals(IpProtocol.TCP)) {
                        mb.setExact(MatchField.TCP_DST, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.UDP)) {
                        mb.setExact(MatchField.UDP_DST, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.SCTP)) {
                        mb.setExact(MatchField.SCTP_DST, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.ICMP)) {

                    } else {
                        log.error("未知IP协议 {} 在推送静态路由时.", client.getNw_proto());
                    }

                    if (sw.equals(pinSwitch.getId())) {
                        actions.add(pinSwitch.getOFFactory().actions().output(path.get(i + 1).getPortId(), Integer.MAX_VALUE));
                    } else {
                        try {
                            actions.add(switchService.getSwitch(path.get(i + 1).getNodeId()).getOFFactory().actions().output(path.get(i + 1).getPortId(), Integer.MAX_VALUE));
                        } catch (NullPointerException e) {
                            log.error("无法向离线交换机安装负载均衡器流表规则 {}.", path.get(i + 1).getNodeId());
                        }
                    }
                }

                fmb.setActions(actions);
                fmb.setPriority(U16.t(LB_PRIORITY));
                fmb.setMatch(mb.build());
                counterPacketOut.increment();
                sfpService.addFlow(entryName, fmb.build(), sw);
            }
        }
    }

    /**
     * 获取最佳路径
     *
     * @param srcId
     * @param srcPort
     * @param dstId
     * @param dstPort
     * @return
     */
    public Path getPath(DatapathId srcId, OFPort srcPort,
                        DatapathId dstId, OFPort dstPort) {
        //从缓存中获取路径
        Path r = getPathByACO(srcId, dstId);

        if (!srcId.equals(dstId) && r.getPath().isEmpty()) {
            return r;
        }

        List<NodePortTuple> nptList = new ArrayList<>(r.getPath());
        NodePortTuple npt = new NodePortTuple(srcId, srcPort);
        //添加起点
        nptList.add(0, npt);
        npt = new NodePortTuple(dstId, dstPort);
        //添加终点
        nptList.add(npt);

        PathId id = new PathId(srcId, dstId);
        Path resPath = new Path(id, nptList);
        List<Path> paths = pathcache.get(id);
        if (paths == null || paths.size() == 0) {
            List<Path> path = new ArrayList<>();
            path.add(r);
            pathcache.put(id, path);
        }else{
            paths.clear();
            List<Path> path = new ArrayList<>();
            path.add(r);
            pathcache.put(id, path);
        }
        return resPath;
    }

    /**
     * 从缓存中获取路径
     *
     * @param srcId
     * @param dstId
     * @return
     */
    public Path getPathByACO(DatapathId srcId, DatapathId dstId) {
        PathId id = new PathId(srcId, dstId);

        if (srcId.equals(dstId)) {
            return new Path(id, ImmutableList.of());
        }
        Path result = null;

        //缓存中查询
        try {
            if (!pathcache.get(id).isEmpty()) {
                result = pathcache.get(id).get(0);
            }
        } catch (Exception e) {
            log.warn("Could not find route from {} to {}. If the path exists, wait for the topology to settle, and it will be detected", srcId, dstId);
        }
        if (result == null || result.getPath().size() == 0) {
            result = computeOrderedPaths(srcId, dstId);
        }
        //获取拓扑结构
        if (log.isTraceEnabled()) {
            log.trace("getPath: {} -> {}", id, result);
        }
        return result == null ? new Path(id, ImmutableList.of()) : result;
    }

    /**
     * 构建解空间
     *
     * @param topologyLinks
     */
    private void buildGraph(Map<DatapathId, Set<Link>> topologyLinks) {
        topologyLinks.forEach(((dataPathId, links) -> {
            SwitchNode topology = new SwitchNode();
            topology.setSwitchDPID(dataPathId.toString());
            links.forEach(link -> {
                SwitchNode.Links linkInfo = new SwitchNode.Links();
                linkInfo.setSrcSwitch(link.getSrc().toString());
                linkInfo.setSrcPort(link.getSrcPort().getPortNumber());
                linkInfo.setDstSwitch(link.getDst().toString());
                linkInfo.setDstPort(link.getDstPort().getPortNumber());
                linkInfo.setLatency(link.getLatency().getValue());
                SwitchPortBandwidth bw = statisticsService.getBandwidthConsumption(dataPathId, link.getSrcPort());
                if (bw != null) {
                    long tx = bw.getBitsPerSecondTx().getValue();
                    long rx = bw.getBitsPerSecondRx().getValue();
                    long speed = bw.getLinkSpeedBitsPerSec().getValue();
                    double rate = (rx + tx) / (double) (speed);
                    DecimalFormat df = new DecimalFormat("#.###");
                    linkInfo.setRate(df.format(rate * 100));
//                    log.info("设备：{} 带宽利用率：{}", dataPathId, df.format(rate * 100));
                }
                topology.getLinks().add(linkInfo);
            });
            topoInstence.add(topology);
        }));
        if (topoInstence != null) {
            //构建解空间
            topoInstence.forEach(topology -> {
                graph.put(topology.getSwitchDPID(), new ArrayList<>());
                List<SwitchNode.Links> linkInfo = topology.getLinks();
                if (linkInfo != null && linkInfo.size() > 0) {
                    topology.getLinks().forEach(links -> {
                        if (!links.getDstSwitch().equals(topology.getSwitchDPID())) {
                            Edge edge = new Edge();
                            edge.setDstSwitch(links.getDstSwitch());
                            edge.setLatency(links.getLatency());
                            edge.setSrcPort(links.getSrcPort());
                            edge.setDstPort(links.getDstPort());
                            if (links.getRate() != null) {
                                edge.setRate(Double.parseDouble(links.getRate()));
                            }
                            //添加关联的交换机
                            graph.get(topology.getSwitchDPID()).add(edge);
                        }
                    });
                }
            });
        }
    }

    /**
     * 计算最佳路径
     */
    private Path computeOrderedPaths(DatapathId startSwitch, DatapathId endSwitch) {
        AntColonyOptimization aco = new AntColonyOptimization(6, 1.0, 2.0, 0.3, 8.0, 10, 10.0);
        List<String> pathString = aco.shortestPath(startSwitch.toString(), endSwitch.toString(), graph);
        if (pathString != null && pathString.size() > 0) {
            log.info("起点：{}到终点：{}----最佳路径：{}", startSwitch.toString(), endSwitch.toString(), JSON.toJSONString(pathString));
        } else {
            log.warn("起点：{}到终点：{}----最佳路径迭代失败！！！", startSwitch.toString(), endSwitch.toString());
        }
        //转化为Path
        PathId pathId = new PathId(startSwitch, endSwitch);
        List<NodePortTuple> nptList = new ArrayList<>();
        if (pathString != null && pathString.size() > 0) {
            for (int i = 0; i < pathString.size() - 1; i++) {
                String srcSwitch = pathString.get(i);
                String dstSwitch = pathString.get(i + 1);
                List<Edge> edges = graph.get(srcSwitch);
                for (Edge edge : edges) {
                    if (edge.getDstSwitch().equals(dstSwitch)) {
                        NodePortTuple nodePortTuple1 = new NodePortTuple(DatapathId.of(srcSwitch), OFPort.of(edge.getSrcPort()));
                        NodePortTuple nodePortTuple2 = new NodePortTuple(DatapathId.of(dstSwitch), OFPort.of(edge.getDstPort()));
                        nptList.add(nodePortTuple1);
                        nptList.add(nodePortTuple2);
                    }
                }
            }
            return new Path(pathId, nptList);
        }
        return null;
    }


    public Comparator<SwitchPort> clusterIdComparator =
            new Comparator<SwitchPort>() {
                @Override
                public int compare(SwitchPort d1, SwitchPort d2) {
                    DatapathId d1ClusterId = topologyService.getClusterId(d1.getNodeId());
                    DatapathId d2ClusterId = topologyService.getClusterId(d2.getNodeId());
                    return d1ClusterId.compareTo(d2ClusterId);
                }
            };

    @Override
    public void enableLoadBalance() {
        this.enableLoadBalance = true;
    }

    @Override
    public void disableLoadBalance() {
        this.enableLoadBalance = false;
    }
}
