package net.floodlightcontroller.com.zhangyh.core.algorithm;

import com.alibaba.fastjson.JSON;
import net.floodlightcontroller.com.zhangyh.core.domain.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhangyh
 * @desc 蚁群算法
 * @date: 2023/4/12  8:12
 */
public class AntColonyOptimization {
    /**
     * 解空间
     */
    private  Map<String, List<Edge>> graph;
    /**
     * 蚂蚁数量
     */
    private Integer numAnts;
    /**
     * alpha参数
     */
    private Double alpha;
    /**
     *  beta参数
     */
    private Double beta;
    /**
     * 信息素挥发速率
     */
    private Double evaporationRate;
    /**
     * 蚂蚁释放信息素强度
     */
    private Double Q;
    /**
     * 初始信息素浓度
     */
    private Double initialPheromone;
    /**
     * 最大迭代次数
     */
    private Integer maxIterations;

    protected static Logger log = LoggerFactory.getLogger(AntColonyOptimization.class);

    /**
     * 信息素矩阵
     */
    Map<String, Map<String, Double>> pheromones = new HashMap<>();

    public AntColonyOptimization(Integer numAnts, Double alpha, Double beta, Double evaporationRate, Double initialPheromone, Integer maxIterations, Double Q) {
        this.numAnts = numAnts;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.initialPheromone = initialPheromone;
        this.maxIterations = maxIterations;
        this.Q=Q;
    }

    /**
     * 路径迭代选择
     * @param start 起点
     * @param end 终点
     * @return 最佳路径
     */
    public  List<String> shortestPath(String start, String end,Map<String, List<Edge>> graph) {
        //构建觅食空间
        this.graph=graph;
        //初始化信息素
        initPheromones(initialPheromone);
        int iterations=0;
        List<String> bestPath = null;
        int bestLatency = Integer.MAX_VALUE;
        while (iterations < maxIterations) {
            iterations++;
            // 初始化蚂蚁
            List<Ant> ants = new ArrayList<>();
            for (int j = 0; j < numAnts; j++) {
                ants.add(new Ant(start,graph));
            }
            // 蚂蚁走路
            for (Ant ant : ants) {
                while (!ant.path.get(ant.path.size() - 1).equals(end)) {
                    String s = ant.chooseNext(pheromones, alpha, beta);
                    if(s==null){
                        break;
                    }
                }
            }
            // 更新信息素
            for (String src : graph.keySet()) {
                for (String dst : graph.keySet()) {
                    if (src.equals(dst)) {
                        continue;
                    }
                    pheromones.get(src).put(dst, pheromones.get(src).get(dst) * (1 - evaporationRate));
                    for (Ant ant : ants) {
                        if (ant.visited.contains(dst)) {
                            pheromones.get(src).put(dst, pheromones.get(src).get(dst) + Q / ant.latency);
                        }
                    }
                }
            }
            // 找到最短路径
            for (Ant ant : ants) {
                if (ant.path.get(ant.path.size() - 1).equals(end) && ant.latency < bestLatency) {
                    bestPath = ant.path;
                    bestLatency = ant.latency;
                    log.info("ACO Iteration {} th time; The best path of this iteration is: {}",iterations,JSON.toJSONString(bestPath));
                }
            }
        }
        return bestPath;
    }


    /**
     * 初始化信息素
     */
    public  void initPheromones(Double initialPheromone){
        for (String src : graph.keySet()) {
            pheromones.put(src, new HashMap<>());
            for (String dst : graph.keySet()) {
                pheromones.get(src).put(dst, initialPheromone);
            }
        }
    }
}
