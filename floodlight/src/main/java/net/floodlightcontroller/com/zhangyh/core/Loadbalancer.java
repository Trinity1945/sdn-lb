package net.floodlightcontroller.com.zhangyh.core;

import com.alibaba.fastjson.JSON;
import net.floodlightcontroller.com.zhangyh.core.domain.Client;
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
import net.floodlightcontroller.packet.*;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Path;
import net.floodlightcontroller.routing.PathId;
import net.floodlightcontroller.staticentry.IStaticEntryPusherService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyManagerBackend;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.FlowModUtils;
import net.floodlightcontroller.util.OFMessageUtils;
import org.projectfloodlight.openflow.protocol.*;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.*;
import org.python.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author: zhangyh
 * @desc 负载均衡
 * @date: 2023/5/14  19:09
 */
public class Loadbalancer implements IFloodlightModule, IOFMessageListener {

    protected static Logger log = LoggerFactory.getLogger(Loadbalancer.class);

    protected IFloodlightProviderService floodlightProviderService;
    protected IRestApiService restApiService;

    protected IDebugCounterService debugCounterService;
    private IDebugCounter counterPacketOut;
    private IDebugCounter counterPacketIn;
    protected IDeviceService deviceManagerService;
    protected IRoutingService routingEngineService;
    protected ITopologyService topologyService;
    protected IStaticEntryPusherService sfpService;
    protected IOFSwitchService switchService;
    protected IStatisticsService statisticsService;
    protected IThreadPoolService threadService;

    private static ITopologyManagerBackend tm;

    /**
     * 保存需要负载均衡的客户端信息
     */
    protected Set<Client> lbClient;

    protected static int LB_PRIORITY = 33333;

    /**
     * 缓存路径
     */
    private Map<PathId, List<Path>>   pathcache;

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
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
        restApiService = context.getServiceImpl(IRestApiService.class);
        debugCounterService = context.getServiceImpl(IDebugCounterService.class);
        deviceManagerService = context.getServiceImpl(IDeviceService.class);
        routingEngineService = context.getServiceImpl(IRoutingService.class);
        topologyService = context.getServiceImpl(ITopologyService.class);
        sfpService = context.getServiceImpl(IStaticEntryPusherService.class);
        switchService = context.getServiceImpl(IOFSwitchService.class);
        statisticsService = context.getServiceImpl(IStatisticsService.class);
        threadService = context.getServiceImpl(IThreadPoolService.class);
        tm = (ITopologyManagerBackend) context.getServiceImpl(ITopologyService.class);

        lbClient = new HashSet<>();
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProviderService.addOFMessageListener(OFType.PACKET_IN, this);
        debugCounterService.registerModule(this.getName());
        counterPacketOut = debugCounterService.registerCounter(this.getName(), "packet-outs-written", "Packet outs written by the LoadBalancer", IDebugCounterService.MetaData.WARN);
        counterPacketIn = debugCounterService.registerCounter(this.getName(), "packet-ins-received", "Packet ins received by the LoadBalancer", IDebugCounterService.MetaData.WARN);
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
            if (statisticsService != null) {
                statisticsService.collectStatistics(true);
            }
//            IDevice dstDevice = IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_DST_DEVICE);
//            IDevice srcDevice = IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_SRC_DEVICE);
            doLoadBalance(client, sw, pi);
        }
        return Command.CONTINUE;
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
                on_same_island = true;
                if ((sw.getId().equals(dstSwDpid)) && OFMessageUtils.getInPort(pi).equals(dstDap.getPortId())) {
                    on_same_if = true;
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
                    //最佳路径获取
                    Path routeIn =
                            routingEngineService.getPath(srcDap.getNodeId(),
                                    srcDap.getPortId(),
                                    dstDap.getNodeId(),
                                    dstDap.getPortId());
                    Path routeOut =
                            routingEngineService.getPath(dstDap.getNodeId(),
                                    dstDap.getPortId(),
                                    srcDap.getNodeId(),
                                    srcDap.getPortId());

                    if (!routeIn.getPath().isEmpty()) {
                        pushStaticRoute(true, routeIn, client, sw);
                    }

                    if (!routeOut.getPath().isEmpty()) {
                        pushStaticRoute(false, routeOut, client, sw);
                    }
                    routeIn.getPath().forEach(e->{
                        log.info("入路由：{}====入端口：{}", e.getNodeId().toString(),e.getPortId());
                    });
                    routeOut.getPath().forEach(e->{
                        log.info("出路由：{}~~~~出端口：{}",e.getNodeId().toString(),e.getPortId());
                    });

                }
                iSrcDaps++;
                iDstDaps++;
            } else if (srcVsDest < 0) {
                iSrcDaps++;
            } else {
                iDstDaps++;
            }
        }
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

                fmb.setIdleTimeout(FlowModUtils.INFINITE_TIMEOUT);
                fmb.setHardTimeout(FlowModUtils.INFINITE_TIMEOUT);
                fmb.setBufferId(OFBufferId.NO_BUFFER);
                fmb.setOutPort(OFPort.ANY);
                fmb.setCookie(U64.of(0));
                fmb.setPriority(FlowModUtils.PRIORITY_MAX);

                if (inBound) {
                    entryName = "inbound-vip-client-"+client.getIpAddress()
                            +"-srcport-"+client.getSrcPort()+"-dstport-"+client.getTargetPort()
                            +"-srcswitch-"+path.get(0).getNodeId()+"-sw-"+sw;
                    mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            .setExact(MatchField.IP_PROTO, client.getNw_proto())
                            .setExact(MatchField.IPV4_SRC, client.getIpAddress())
                            .setExact(MatchField.IPV4_DST,client.getTargetIpAddress())
                            .setExact(MatchField.IN_PORT, path.get(i).getPortId());
                    if (client.getNw_proto().equals(IpProtocol.TCP)) {
                        mb.setExact(MatchField.TCP_SRC, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.UDP)) {
                        mb.setExact(MatchField.UDP_SRC, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.SCTP)) {
                        mb.setExact(MatchField.SCTP_SRC, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.ICMP)) {

                    } else {
                        log.error("Unknown IpProtocol {} detected during inbound static VIP route push.", client.getNw_proto());
                    }

                    if (sw.equals(pinSwitch.getId())) {
                            actions.add(pinSwitch.getOFFactory().actions().output(path.get(i+1).getPortId(), Integer.MAX_VALUE));
                    } else {
                        try{
                            actions.add(switchService.getSwitch(path.get(i+1).getNodeId()).getOFFactory().actions().output(path.get(i+1).getPortId(), Integer.MAX_VALUE));
                        }
                        catch(NullPointerException e){
                            log.error("Fail to install loadbalancer flow rules to offline switch {}.", path.get(i+1).getNodeId());
                        }
                    }

                }else{
                    entryName = "inbound-lb-client-"+client.getIpAddress()
                            +"-srcport-"+client.getSrcPort()+"-dstport-"+client.getTargetPort()
                            +"-srcswitch-"+path.get(0).getNodeId()+"-sw-"+sw;
                    mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
                            .setExact(MatchField.IP_PROTO, client.getNw_proto())
                            .setExact(MatchField.IPV4_DST, client.getIpAddress())
                            .setExact(MatchField.IN_PORT, path.get(i).getPortId());
                    if (client.getNw_proto().equals(IpProtocol.TCP)) {
                        mb.setExact(MatchField.TCP_DST, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.UDP)) {
                        mb.setExact(MatchField.UDP_DST, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.SCTP)) {
                        mb.setExact(MatchField.SCTP_DST, client.getSrcPort());
                    } else if (client.getNw_proto().equals(IpProtocol.ICMP)) {
                        /* no-op */
                    } else {
                        log.error("Unknown IpProtocol {} detected during outbound static VIP route push.", client.getNw_proto());
                    }

                    if (sw.equals(pinSwitch.getId())) {
                            actions.add(pinSwitch.getOFFactory().actions().output(path.get(i+1).getPortId(), Integer.MAX_VALUE));
                    } else {
                        try{
                            actions.add(switchService.getSwitch(path.get(i+1).getNodeId()).getOFFactory().actions().output(path.get(i+1).getPortId(), Integer.MAX_VALUE));
                        }
                        catch(NullPointerException e){
                            log.error("Fail to install loadbalancer flow rules to offline switches {}.", path.get(i+1).getNodeId());
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
     * @param srcId
     * @param srcPort
     * @param dstId
     * @param dstPort
     * @return
     */
    public Path getPath(DatapathId srcId, OFPort srcPort,
                        DatapathId dstId, OFPort dstPort) {
        //从缓存中获取路径
        Path r = getPathByCache(srcId, dstId);

        if (! srcId.equals(dstId) && r.getPath().isEmpty()) {
            return r;
        }

        List<NodePortTuple> nptList = new ArrayList<NodePortTuple>(r.getPath());
        NodePortTuple npt = new NodePortTuple(srcId, srcPort);
        //添加起点
        nptList.add(0, npt);
        npt = new NodePortTuple(dstId, dstPort);
        //添加终点
        nptList.add(npt);

        PathId id = new PathId(srcId, dstId);
        r = new Path(id, nptList);
        return r;
    }

    /**
     * 从缓存中获取路径
     * @param srcId
     * @param dstId
     * @return
     */
    public Path getPathByCache(DatapathId srcId, DatapathId dstId) {
        PathId id = new PathId(srcId, dstId);

        /* Return empty route if srcId equals dstId */
        if (srcId.equals(dstId)) {
            return new Path(id, ImmutableList.of());
        }

        Path result = null;

        try {
            if (!pathcache.get(id).isEmpty()) {
                result = pathcache.get(id).get(0);
            }
        } catch (Exception e) {
            log.warn("Could not find route from {} to {}. If the path exists, wait for the topology to settle, and it will be detected", srcId, dstId);
        }

        if (log.isTraceEnabled()) {
            log.trace("getPath: {} -> {}", id, result);
        }
        return result == null ? new Path(id, ImmutableList.of()) : result;
    }

    /**
     * 计算最佳路径
     */
    private void computeOrderedPaths(){

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
}
