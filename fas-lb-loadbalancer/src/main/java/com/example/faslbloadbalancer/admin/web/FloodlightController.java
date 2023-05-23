package com.example.faslbloadbalancer.admin.web;

import com.example.faslbloadbalancer.admin.core.Edge;
import com.example.faslbloadbalancer.admin.model.vo.Device;
import com.example.faslbloadbalancer.admin.model.vo.Topology;
import com.example.faslbloadbalancer.admin.service.FloodlightService;
import com.example.faslbloadbalancer.common.util.HexString;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/14  21:12
 */
@RestController
@RequestMapping("/flood")
public class FloodlightController {

    @Resource
    private FloodlightService floodlightService;

    @GetMapping("/deviceOfHost")
    public Mono<Device> getHostInfo() {
        return floodlightService.getHostInfo();
    }

    @GetMapping("/getTopology")
    public Mono<List<Topology>> getTopology() {
        return floodlightService.getTopology();
    }

    @GetMapping("/buildTopology")
    public Mono<Map<String, List<Edge>>> buildTopology() {
        return floodlightService.buildTopology();
    }

    @GetMapping("/aco")
    public Mono<List<String>> aco(@RequestParam Integer startSwitch,@RequestParam Integer endSwitch) {
        return floodlightService.aco(HexString.toHexString(startSwitch),HexString.toHexString(endSwitch));
    }

}
