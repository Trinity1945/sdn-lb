package com.example.faslbloadbalancer.admin.service;

import com.example.faslbloadbalancer.admin.core.ACO;
import com.example.faslbloadbalancer.admin.core.Edge;
import com.example.faslbloadbalancer.admin.model.vo.Device;
import com.example.faslbloadbalancer.admin.model.vo.Topology;
import com.example.faslbloadbalancer.common.util.ReactorWebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/15  10:02
 */
@Service
public class FloodlightService {

    @Resource
    private  ReactorWebClient client;

    public Mono<Device> getHostInfo(){
        ParameterizedTypeReference<Device> typeReference = new ParameterizedTypeReference<Device>() {};
       return client.getMono("/wm/device/", typeReference);
    }


    public Mono<List<Topology>> getTopology(){
        ParameterizedTypeReference<List<Topology>> typeReference = new ParameterizedTypeReference<List<Topology>>() {};
        return client.getMono("/wm/monitor/switch/link/json", typeReference);
    }

    /**
     * 构建邻接表
     * @return
     */
    public Mono<Map<String, List<Edge>>> buildTopology(){
        Map<String, List<Edge>> graph = new HashMap<>();
        ParameterizedTypeReference<List<Topology>> typeReference = new ParameterizedTypeReference<List<Topology>>() {};
        return client.getMono("/wm/monitor/switch/link/json", typeReference).map(topo->{
            topo.forEach(topology -> {
                graph.put(topology.getSwitchDPID(),new ArrayList<>());
                topology.getLinks().forEach(links -> {

                    if(!links.getDstSwitch().equals(topology.getSwitchDPID())){
                        Edge edge = new Edge();
                        edge.setDstSwitch(links.getDstSwitch());
                        edge.setLatency(links.getLatency());
                        edge.setSrcPort(links.getSrcPort());
                        edge.setDstPort(links.getDstPort());
                        edge.setRate(links.getRate());
                        //添加关联的交换机
                        graph.get(topology.getSwitchDPID()).add(edge);
                    }

                });
            });
            return graph;
        });
    }

    public Mono<List<String>> aco(String startSwitch,String endSwitch) {
        return Mono.defer(()->{
            ACO aco = new ACO(6, 1.0, 2.0, 0.3, 1.0, 50,10.0);
          return Mono.just(aco);
        }).zipWhen(e->this.buildTopology(),(aco,topology)-> aco.shortestPath(startSwitch, endSwitch, topology))
                .defaultIfEmpty(new ArrayList<>());
    }
}
