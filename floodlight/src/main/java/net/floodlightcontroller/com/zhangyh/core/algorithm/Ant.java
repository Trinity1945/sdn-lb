package net.floodlightcontroller.com.zhangyh.core.algorithm;


import net.floodlightcontroller.com.zhangyh.model.SwitchNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zhangyh
 * @desc 蚂蚁
 * @date: 2023/5/12  23:54
 */
public class Ant {
    /**
     * 路径起点
     */
    public String start;
    /**
     * 路径终点
     */
    public String end;
    /**
     * 路径记忆向量
     */
    public List<String> path;
    /**
     * 禁忌表, 记录走过的城市
     */
    public Set<String> visited;

    /**
     * 路径距离
     */
    public int latency;
    /**
     * 觅食空间--邻接表
     */
    private Map<String, List<SwitchNode.Links>> graph ;

    public Ant(String start, String end,Map<String, List<SwitchNode.Links>> graph) {
        this.start = start;
        this.end = end;
        this.path = new ArrayList<>();
        this.path.add(start);
        this.visited = new HashSet<>();
        this.visited.add(start);
        this.latency = 0;
        this.graph=graph;
    }
    public String chooseNext(Map<String, Map<String, Double>> pheromones, double alpha, double beta) {
        // 计算每个邻居的概率
        List<Map.Entry<String, Double>> probabilities = new ArrayList<>();
        for (SwitchNode.Links neighbor : graph.get(this.path.get(this.path.size() - 1))) {
            if (!this.visited.contains(neighbor.dstSwitch)) {
                double pheromone = pheromones.get(this.path.get(this.path.size() - 1)).get(neighbor.dstSwitch);
                double distance = (0.7*neighbor.latency)+(Double.parseDouble(neighbor.getRate())*0.3);
                double probability = Math.pow(pheromone, alpha) * Math.pow(1.0 / distance, beta);
                probabilities.add(new AbstractMap.SimpleEntry<>(neighbor.dstSwitch, probability));
            }
        }
        // 根据概率选择下一个节点
        if (!probabilities.isEmpty()) {
            double totalProb = probabilities.stream().mapToDouble(Map.Entry::getValue).sum();
            probabilities = probabilities.stream().map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue() / totalProb)).collect(Collectors.toList());
            String nextNode = probabilities.stream().map(Map.Entry::getKey).collect(Collectors.toList()).get(chooseIndex(probabilities.stream().mapToDouble(Map.Entry::getValue).toArray()));
            long nextLatency = graph.get(this.path.get(this.path.size() - 1)).stream().filter(n -> n.dstSwitch.equals(nextNode)).findFirst().get().latency;
            this.path.add(nextNode);
            this.visited.add(nextNode);
            this.latency += nextLatency;
            return nextNode;
        } else {
            return null;
        }
    }

    // 随机选择下标
    private static int chooseIndex(double[] probabilities) {
        double totalProb = Arrays.stream(probabilities).sum();
        Random random = new Random();
        double rand = random.nextDouble() * totalProb;
        for (int i = 0; i < probabilities.length; i++) {
            if (rand < probabilities[i]) {
                return i;
            }
            rand -= probabilities[i];
        }
        return probabilities.length - 1;
    }
}
