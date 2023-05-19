package com.example.faslbloadbalancer.admin.core;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/9  19:40
 */
public class AntColonyOptimization {
    // 定义邻接表表示图
    private static Map<String, List<Neighbor>> graph = new HashMap<>();

    // 定义邻居类
    private static class Neighbor {
        public String dstSwitch;
        public int srcPort;
        public int dstPort;
        public int latency;

        public Neighbor(String dstSwitch, int srcPort, int dstPort, int latency) {
            this.dstSwitch = dstSwitch;
            this.srcPort = srcPort;
            this.dstPort = dstPort;
            this.latency = latency;
        }
    }

    // 定义蚂蚁类
    private static class Ant {
        public String start;
        public String end;
        public List<String> path;
        public Set<String> visited;
        public int latency;

        public Ant(String start, String end) {
            this.start = start;
            this.end = end;
            this.path = new ArrayList<>();
            this.path.add(start);
            this.visited = new HashSet<>();
            this.visited.add(start);
            this.latency = 0;
        }

        public String chooseNext(Map<String, Map<String, Double>> pheromones, double alpha, double beta) {
            // 计算每个邻居的概率
            List<Map.Entry<String, Double>> probabilities = new ArrayList<>();
            for (Neighbor neighbor : graph.get(this.path.get(this.path.size() - 1))) {
                if (!this.visited.contains(neighbor.dstSwitch)) {
                    double pheromone = pheromones.get(this.path.get(this.path.size() - 1)).get(neighbor.dstSwitch);
                    double distance = neighbor.latency;
                    double probability = Math.pow(pheromone, alpha) * Math.pow(1.0 / distance, beta);
                    probabilities.add(new AbstractMap.SimpleEntry<>(neighbor.dstSwitch, probability));
                }
            }
            // 根据概率选择下一个节点
            if (!probabilities.isEmpty()) {
                double totalProb = probabilities.stream().mapToDouble(Map.Entry::getValue).sum();
                probabilities = probabilities.stream().map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue() / totalProb)).collect(Collectors.toList());
                String nextNode = probabilities.stream().map(Map.Entry::getKey).collect(Collectors.toList()).get(chooseIndex(probabilities.stream().mapToDouble(Map.Entry::getValue).toArray()));
                int nextLatency = graph.get(this.path.get(this.path.size() - 1)).stream().filter(n -> n.dstSwitch.equals(nextNode)).findFirst().get().latency;
                this.path.add(nextNode);
                this.visited.add(nextNode);
                this.latency += nextLatency;
                return nextNode;
            } else {
                return null;
            }
        }
    }

    // 计算最短路径
    public static List<String> shortestPath(String start, String end) {
        // 初始化邻接表
        graph.put("00:00:00:00:00:00:00:02", Arrays.asList(new Neighbor("00:00:00:00:00:00:00:01", 3, 1, 1)));
        graph.put("00:00:00:00:00:00:00:03", Arrays.asList(new Neighbor("00:00:00:00:00:00:00:01", 3, 2, 1)));
        graph.put("00:00:00:00:00:00:00:01", Arrays.asList(new Neighbor("00:00:00:00:00:00:00:02", 1, 3, 1), new Neighbor("00:00:00:00:00:00:00:03", 2, 3, 0)));

        // 初始化信息素
        Map<String, Map<String, Double>> pheromones = new HashMap<>();
        for (String src : graph.keySet()) {
            pheromones.put(src, new HashMap<>());
            for (String dst : graph.keySet()) {
                pheromones.get(src).put(dst, 1.0);
            }
        }

        // 迭代次数和蚂蚁数量
        int iterations = 100;
        int numAnts = 10;
        double alpha = 1;
        double beta = 2;
        double evaporationRate = 0.5;

        // 迭代
        List<String> bestPath = null;
        int bestLatency = Integer.MAX_VALUE;
        for (int i = 0; i < iterations; i++) {
            // 初始化蚂蚁
            List<Ant> ants = new ArrayList<>();
            for (int j = 0; j < numAnts; j++) {
                ants.add(new Ant(start, end));
            }
            // 蚂蚁走路
            for (Ant ant : ants) {
                while (!ant.path.get(ant.path.size() - 1).equals(end)) {
                    ant.chooseNext(pheromones, alpha, beta);
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
                            pheromones.get(src).put(dst, pheromones.get(src).get(dst) + 1.0 / ant.latency);
                        }
                    }
                }
            }
            // 找到最短路径
            for (Ant ant : ants) {
                if (ant.end.equals(end) && ant.latency < bestLatency) {
                    bestPath = ant.path;
                    bestLatency = ant.latency;
                }
            }
        }

        return bestPath;
    }

    // 随机选择下标
    private static int chooseIndex(double[] probabilities) {

        return probabilities.length - 1;
    }

    // 测试
    public static void main(String[] args) {
        String start = "00:00:00:00:00:00:00:03";
        String end = "00:00:00:00:00:00:00:02";
        List<String> path = shortestPath(start, end);
        System.out.println("Shortest path from " + start + " to " + end + " is " + path + " with latency " );
//        System.out.println( (path.size() > 1 ? path.subList(1, path.size()).stream().mapToInt(p -> graph.get(p).stream().filter(n -> n.dstSwitch.equals(path.get(path.indexOf(p) + 1))).findFirst().get().latency).sum() : 0));
    }
}
