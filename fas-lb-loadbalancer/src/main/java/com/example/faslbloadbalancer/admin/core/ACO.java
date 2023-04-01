package com.example.faslbloadbalancer.admin.core;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/1  20:39
 */
public class ACO {
    private int antNum;//蚂蚁数量
    private int cityNum;//城市数量
    private int maxGen;//迭代次数
    private double[][] pheromone;//信息素矩阵
    private int[][] distance;//距离矩阵
    private int bestLength;//最优路径长度
    private int[] bestTour;//最优路径

    private double alpha;//信息素重要程度因子
    private double beta;//启发函数重要程度因子
    private double rho;//信息素挥发因子
    private double Q;//信息素增加强度系数

    private int curAnt;//当前蚂蚁编号
    private int[] curTour;//当前路径
    private boolean[] visited;//是否已访问

    //构造函数
    public ACO(int antNum, int cityNum, int maxGen, double alpha, double beta, double rho, double Q) {
        this.antNum = antNum;
        this.cityNum = cityNum;
        this.maxGen = maxGen;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;
        this.Q = Q;

        this.distance = new int[cityNum][cityNum];
        this.pheromone = new double[cityNum][cityNum];
        this.visited = new boolean[cityNum];

        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                pheromone[i][j] = 1.0;//初始化信息素矩阵
            }
        }
    }

    //计算距离矩阵
    private void calcDistance(int[][] cityPos) {
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                int x = cityPos[i][0] - cityPos[j][0];
                int y = cityPos[i][1] - cityPos[j][1];
                distance[i][j] = distance[j][i] = (int) Math.sqrt(x * x + y * y);
            }
        }
    }

    //计算启发函数
    private double calcHeuristic(int i, int j) {
        return 1.0 / distance[i][j];
    }

    //选择下一个城市
    private int selectNextCity() {
        double[] p = new double[cityNum];//每个城市被选中的概率
        double sum = 0.0;
        int curCity = curTour[curAnt];
        for (int i = 0; i < cityNum; i++) {
            if (!visited[i]) {
                p[i] = Math.pow(pheromone[curCity][i], alpha) * Math.pow(calcHeuristic(curCity, i), beta);
                sum += p[i];
            }
        }
        if (sum == 0.0) {
            return -1;//没有可选的城市
        }
        double r = Math.random() * sum;//随机数
        double sum1 = 0.0;
        for (int i = 0; i < cityNum; i++) {
            if (!visited[i]) {
                sum1 += p[i];
                if (sum1 >= r) {
                    return i;//返回选中的城市
                }
            }
        }
        return -1;
    }

    //搜索路径
    private void search() {
        initAnts();//初始化蚂蚁
        for (int g = 0; g < maxGen; g++) {//循环迭代
            moveAnts();//蚂蚁移动
            updatePheromone();//信息素更新
            findBestTour();//找到最优路径
        }
    }

    //初始化蚂蚁
    private void initAnts() {
        curTour = new int[cityNum];//当前路径
        bestLength = Integer.MAX_VALUE;//最优路径长度
        bestTour = new int[cityNum];//最优路径

        for (int i = 0; i < antNum; i++) {//多个蚂蚁同时开始搜索
            curAnt = i;
            for (int j = 0; j < cityNum; j++) {
                visited[j] = false;
            }
            int startCity = (int) (Math.random() * cityNum);//随机选择一个起点
            curTour[0] = startCity;
            visited[startCity] = true;
            for (int j = 1; j < cityNum; j++) {
                int nextCity = selectNextCity();//选择下一个城市
                if (nextCity == -1) {
                    break;
                }
                curTour[j] = nextCity;
                visited[nextCity] = true;
            }
            int curLength = calculateTourLength(curTour);//计算路径长度
            if (curLength < bestLength) {//更新最优路径
                bestLength = curLength;
                System.arraycopy(curTour, 0, bestTour, 0, cityNum);
            }
        }
    }

    //蚂蚁移动
    private void moveAnts() {
        for (int i = 0; i < antNum; i++) {
            curAnt = i;
            for (int j = 1; j < cityNum; j++) {
                int nextCity = selectNextCity();//选择下一个城市
                if (nextCity == -1) {//没有可选的城市
                    break;
                }
                curTour[j] = nextCity;
                visited[nextCity] = true;
            }
            int curLength = calculateTourLength(curTour);//计算路径长度
            if (curLength < bestLength) {//更新最优路径
                bestLength = curLength;
                System.arraycopy(curTour, 0, bestTour, 0, cityNum);
            }
        }
    }

    //信息素更新
    private void updatePheromone() {
        for (int i = 0; i < cityNum; i++) {
            for (int j = i + 1; j < cityNum; j++) {
                pheromone[i][j] = pheromone[j][i] = (1 - rho) * pheromone[i][j];//信息素挥发
                for (int k = 0; k < antNum; k++) {
                    double delta = 0.0;
                    int curCity = bestTour[k * cityNum + i];
                    int nextCity = bestTour[k * cityNum + j];
                    if (curCity == i && nextCity == j) {
                        delta = Q / bestLength;
                    }
                    pheromone[i][j] += delta;//信息素增加
                    pheromone[j][i] = pheromone[i][j];
                }
            }
        }
    }

    //找到最优路径
    private void findBestTour() {
        curAnt = 0;
        for (int i = 0; i < cityNum; i++) {
            visited[i] = false;
        }
        int startCity = bestTour[0];
        curTour[0] = startCity;
        visited[startCity] = true;
        for (int i = 1; i < cityNum; i++) {
            int nextCity = selectNextCity();//选择下一个城市
            if (nextCity == -1) {//没有可选的城市
                break;
            }
            curTour[i] = nextCity;
            visited[nextCity] = true;
        }
        bestLength = calculateTourLength(curTour);//计算路径长度
        System.arraycopy(curTour, 0, bestTour, 0, cityNum);
    }

    //计算路径长度
    private int calculateTourLength(int[] tour) {
        int len = 0;
        for (int i = 0; i < cityNum; i++) {
            int j = (i + 1) % cityNum;
            len += distance[tour[i]][tour[j]];
        }
        return len;
    }

    //输出结果
    public void printResult() {
        System.out.println("最短路径长度: " + bestLength);
        System.out.print("最短路径: ");
        for (int i = 0; i < cityNum; i++) {
            System.out.print(bestTour[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int antNum = 10;//蚂蚁数量
        int cityNum = 20;//城市数量
        int maxGen = 50;//迭代次数
        double alpha = 1.0;//信息素重要程度因子
        double beta = 5.0;//启发函数重要程度因子
        double rho = 0.1;//信息素挥发因子
        double Q = 100.0;//信息素增加强度系数

        final ACO aco = new ACO(antNum, cityNum, maxGen, alpha, beta, rho, Q);
        aco.calcDistance(new int[][]{{1,2},{2,3},{4,5}});
        aco.initAnts();
        aco.search();
        aco.printResult();

    }
}
