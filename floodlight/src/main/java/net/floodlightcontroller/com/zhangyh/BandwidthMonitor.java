package net.floodlightcontroller.com.zhangyh;

import net.floodlightcontroller.com.zhangyh.route.BandwidthMonitorRoutable;
import net.floodlightcontroller.com.zhangyh.service.BandwidthMonitorService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangyh
 * @desc 网络外宽监测
 * @date: 2023/4/5  17:52
 */
public class BandwidthMonitor implements IFloodlightModule, BandwidthMonitorService {

    //统计模块
    IStatisticsService statisticsService;
    IThreadPoolService threadPoolService;

    IRestApiService restApiService;
    private static final Logger log = LoggerFactory.getLogger(BandwidthMonitor.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(BandwidthMonitorService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(BandwidthMonitorService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        // MyModule 模块依赖的其他服务接口
        List<Class<? extends IFloodlightService>> dependencies = new ArrayList<>();
        dependencies.add(IStatisticsService.class);
        dependencies.add(IRestApiService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        log.info("执行我的模块");
        statisticsService = context.getServiceImpl(IStatisticsService.class);
        threadPoolService = context.getServiceImpl(IThreadPoolService.class);
        restApiService = context.getServiceImpl(IRestApiService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        log.info("监测带宽中----------------------->>>");
        int portStatsInterval = 4;
        threadPoolService
                .getScheduledExecutor()
                .scheduleAtFixedRate(this::getAllBandwidth, portStatsInterval, portStatsInterval, TimeUnit.SECONDS);
        log.warn("Statistics collection thread(s) started");
        restApiService.addRestletRoutable(new BandwidthMonitorRoutable());
    }

    /**
     * 获取网络带宽状况
     *
     * @return
     */
    public Map<NodePortTuple, SwitchPortBandwidth> getAllBandwidth() {
        log.info("带宽--------------------->>>");
        //获取交换机带宽情况
        statisticsService.collectStatistics(true);
        Map<NodePortTuple, SwitchPortBandwidth> bandwidthConsumption = statisticsService.getBandwidthConsumption();
        bandwidthConsumption.forEach((key, value) -> {
            long rx = value.getBitsPerSecondRx().getValue();
            long tx = value.getBitsPerSecondTx().getValue();
            long speed = value.getLinkSpeedBitsPerSec().getValue(); // 端口速度转换为 Mbps
            double rate = (rx + tx) / (double)(speed); // 将除数转换为double类型，确保计算结果为小数
            DecimalFormat df = new DecimalFormat("#.###"); // 创建DecimalFormat对象，设置保留三位小数
            log.info("设备带宽：{}==RX:{}  TX:{} speed:{}  利用率：{}", key, rx, tx, speed, df.format(rate * 100) );
        });
        return bandwidthConsumption;
    }
}
