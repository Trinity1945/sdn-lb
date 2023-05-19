package com.example.faslbloadbalancer.admin.ff;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/12  11:21
 */
public class AntColonyOptimization {
    private int antCount; // 蚂蚁数量
    private int maxIteration; // 最大迭代次数
    private double alpha; // 信息素重要度因子
    private double beta; // 启发式因子
    private double rho; // 信息素挥发因子
    private double Q; // 蚂蚁释放信息素强度
    private double[][] distance; // 距离矩阵
    private double[][] pheromone; // 信息素矩阵
    private int cityCount; // 城市数量
    private int[] bestPath; // 最佳路径
    private double bestLength; // 最佳路径长度
    private int startCity; // 起点
    private int endCity; // 终点

    public AntColonyOptimization(int antCount, int maxIteration, double alpha, double beta, double rho, double Q, double[][] distance, int startCity, int endCity) {
        this.antCount = antCount;
        this.maxIteration = maxIteration;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;
        this.Q = Q;
        this.distance = distance;
        this.cityCount = distance.length;
        this.pheromone = new double[cityCount][cityCount];
        this.startCity = startCity;
        this.endCity = endCity;
        initPheromone();
    }

    // 初始化信息素
    private void initPheromone() {
        double initPheromone = 1.0 / (cityCount * cityCount);
        for (int i = 0; i < cityCount; i++) {
            for (int j = 0; j < cityCount; j++) {
                pheromone[i][j] = initPheromone;
            }
        }
    }

    // 计算路径长度
    private double calculatePathLength(int[] path) {
        double length = 0;
        for (int i = 0; i < cityCount - 1; i++) {
            length += distance[path[i]][path[i + 1]];
        }
        length += distance[path[cityCount - 1]][path[0]];
        return length;
    }

    // 选择下一个城市
    private int selectNextCity(int ant, boolean[] visited, double[][] probability) {
        int currentCity = -1;
        double p = Math.random();
        if (p < 0.9) {
            double maxProbability = -1.0;
            for (int i = 0; i < cityCount; i++) {
                if (!visited[i]) {
                    if (probability[ant][i] > maxProbability) {
                        maxProbability = probability[ant][i];
                        currentCity = i;
                    }
                }
            }
        }
        if (currentCity == -1) {
            do {
                currentCity = (int) (Math.random() * cityCount);
            } while (visited[currentCity]);
        }
        return currentCity;
    }

    // 更新信息素
    private void updatePheromone(List<int[]> antPathList) {
        // 信息素挥发
        for (int i = 0; i < cityCount; i++) {
            for (int j = 0; j < cityCount; j++) {
                pheromone[i][j] *= (1 - rho);
            }
        }
        // 信息素更新
        for (int[] path : antPathList) {
            double deltaPheromone = Q / calculatePathLength(path);
            for (int i = 0; i < cityCount - 1; i++) {
                pheromone[path[i]][path[i + 1]] += deltaPheromone;
            }
            pheromone[path[cityCount - 1]][path[0]] += deltaPheromone;
        }
    }

    // 蚁群算法主体
    public void solve() {
        bestLength = Double.MAX_VALUE;
        for (int iteration = 0; iteration < maxIteration; iteration++) {
            List<int[]> antPathList = new ArrayList<>();
            for (int ant = 0; ant < antCount; ant++) {
                int[] path = new int[cityCount];
                path[0] = startCity;
                boolean[] visited = new boolean[cityCount];
                visited[startCity] = true;
                double[][] probability = new double[antCount][cityCount];
                for (int i = 1; i < cityCount; i++) {
                    double totalProbability = 0.0;
                    for (int j = 0; j < cityCount; j++) {
                        if (!visited[j]) {
                            probability[ant][j] = Math.pow(pheromone[path[i - 1]][j], alpha) * Math.pow(1.0 / distance[path[i - 1]][j], beta);
                            totalProbability += probability[ant][j];
                        }
                    }
                    for (int j = 0; j < cityCount; j++) {
                        probability[ant][j] /= totalProbability;
                    }
                    int nextCity;
                    if (i == cityCount - 1) {
                        nextCity = endCity;
                    } else {
                        nextCity = selectNextCity(ant, visited, probability);
                    }

                    path[i] = nextCity;
                    visited[nextCity] = true;
                }

                double length = calculatePathLength(path);
                if (length < bestLength) {
                    bestLength = length;
                    bestPath = path.clone();
                }
                antPathList.add(path);
            }
            updatePheromone(antPathList);
        }
    }

    public int[] getBestPath() {
        return bestPath;
    }

    public double getBestLength() {
        return bestLength;
    }

    public static void main(String[] args) {
        // 测试用例
        int cityCount = 10;
        double[][] distance = new double[cityCount][cityCount];
        for (int i = 0; i < cityCount; i++) {
            for (int j = 0; j < cityCount; j++) {
                if (i == j) {
                    distance[i][j] = 0;
                } else {
                    distance[i][j] = Math.abs((i - j) * 10) + 1;
                    distance[j][i] = distance[i][j];
                }
            }
        }
        System.out.println(JSON.toJSONString(distance));
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入起点和终点（0-" + (cityCount - 1) + "）:");
        int startCity = scanner.nextInt();
        int endCity = scanner.nextInt();

        AntColonyOptimization aco = new AntColonyOptimization(50, 200, 1, 5, 0.5, 100, distance, startCity, endCity);
        aco.solve();
        System.out.println("Best length: " + aco.getBestLength());
        System.out.print("Best path: ");
        for (int i = 0; i < cityCount; i++) {
            System.out.print(aco.getBestPath()[i] + " ");
        }
        System.out.println();
    }
}
