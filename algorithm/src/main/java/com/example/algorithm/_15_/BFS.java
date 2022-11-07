package com.example.algorithm._15_;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

class Point {

    int x;
    int y;
}

public class BFS {

    int n; // 地图的行
    int m; // 地图的列
    int dx; // 安琪的位置x
    int dy; // 安琪的位置y
    int data[][]; // 邻接矩阵
    boolean mark[][]; // 标记数据 走过的位置

    public void bfs(int x, int y) { // x he y表示你当前的位置,就是求（x，y）->(dx,dy)能不能到
        if (x < 1 || x > n || y < 1 || y > m) return;
        if (x == dx && y == dy) {
            System.out.println("true");
            return;
        }
        mark[x][y] = true;
        //https://www.cnblogs.com/teach/p/10665199.html
        Queue<Point> queue = new ArrayBlockingQueue<Point>(n * m); // 因为最多也就是n*m个点
        Point start = new Point();
        start.x = x;
        start.y = y;
        queue.add(start);
        int next[][] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};    //ACM想到的
// 上  右 下 左
        while (!queue.isEmpty()) {        //O(n)
            Point point = queue.poll(); // 拿出队列的第一个点
            for (int i = 0; i < 4; i++) {
                // x y >  i=0 next[0][0],next[0][1] 上
                // x y >  i=1 next[1][0],next[1][1] 右
                // x y >  i=0 next[0][0],next[0][1] 下
                // x y >  i=0 next[0][0],next[0][1] 左
                int nextx = point.x + next[i][0];
                int nexty = point.y + next[i][1];
                if (nextx < 1 || nextx > n || nexty < 1 || nexty > m) continue; //超出界線 跳出此次迴圈，繼續其他迴圈
                if (data[nextx][nexty] == 0 && !mark[nextx][nexty]) {        //表示可以走
                    if (nextx == dx && nexty == dy) {        //表示可以到了 就不走了
                        System.out.println("true");
                        return;
                    }
                    Point newPoint = new Point();
                    newPoint.x = nextx;
                    newPoint.y = nexty;
                    mark[nextx][nexty] = true;
                    queue.add(newPoint);
                }
//				如果不能走就會跳到for內嘗試其他方向
            }

        }
        System.out.println("false");
        return;

    }

}
