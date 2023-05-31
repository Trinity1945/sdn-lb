package com.example.faslbloadbalancer;

import com.example.faslbloadbalancer.admin.core.AntColonyOptimization;
import com.example.faslbloadbalancer.admin.service.FloodlightService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@SpringBootTest
class FasLbLoadbalancerApplicationTests {

    @Resource
    private FloodlightService floodlightService;

    @Test
    void contextLoads() {
        Random random = new Random();
        double rand = random.nextDouble();
        System.out.println(rand);
    }

    @Test
    public void test2(){
        String start = "00:00:00:00:00:00:00:01";
        String end = "00:00:00:00:00:00:00:0f";
        List<String> path = AntColonyOptimization.shortestPath(start, end);
        System.out.println("Shortest path from " + start + " to " + end + " is " + path + " with latency " );
    }

}
