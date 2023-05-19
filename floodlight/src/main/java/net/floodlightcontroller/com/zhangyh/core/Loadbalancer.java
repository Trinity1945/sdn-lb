package net.floodlightcontroller.com.zhangyh.core;

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
import net.floodlightcontroller.debugcounter.IDebugCounter;
import net.floodlightcontroller.debugcounter.IDebugCounterService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.packet.*;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.staticentry.IStaticEntryPusherService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyService;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
        return (type.equals(OFType.PACKET_IN) &&
                (name.equals("topology") ||
                        name.equals("devicemanager") ||
                        name.equals("virtualizer")));
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return (type.equals(OFType.PACKET_IN) && name.equals("forwarding"));
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
        if (pkt instanceof IPv4) {
            IPv4 ip_pkt = (IPv4) pkt;
            Client client = new Client();
            client.setIpAddress(ip_pkt.getSourceAddress());
            client.setNw_proto(ip_pkt.getProtocol());
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
            if(statisticsService!=null){
                statisticsService.collectStatistics(true);
            }
        }


        return Command.CONTINUE;
    }
}
