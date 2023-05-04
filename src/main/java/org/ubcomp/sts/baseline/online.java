package org.ubcomp.sts.baseline;

import java.util.ArrayList;
import java.util.List;

public class online {
    private static final double MIN_RECTANGLE_SIZE = 0.1; // 矩形的最小边长，单位：经纬度
    private static final double MIN_DENSITY = 0.5; // 最小密度

    public static void main(String[] args) {
        List<Point> trajectory = new ArrayList<>(); // 示例输入轨迹数据，假设为经纬度坐标轨迹
        trajectory.add(new Point(0, 0));
        trajectory.add(new Point(1, 1));
        trajectory.add(new Point(2, 1));
        trajectory.add(new Point(3, 0));
        trajectory.add(new Point(4, -1));
        trajectory.add(new Point(5, 0));
        double minR = 2.0; // 最小半径，单位：千米
        List<Integer> cutoffs = new ArrayList<>(); // 分割点列表
        List<Point> centroids = new ArrayList<>(); // 质心列表

        Point currentCentroid = trajectory.get(0);
        int nPoints = 1;
        double maxLatitude = currentCentroid.y;
        double minLatitude = currentCentroid.y;
        double maxLongitude = currentCentroid.x;
        double minLongitude = currentCentroid.x;

        cutoffs.add(0);

        for (int i = 1; i < trajectory.size(); i++) {
            Point point = trajectory.get(i);
            nPoints++;
            maxLatitude = Math.max(maxLatitude, point.y);
            minLatitude = Math.min(minLatitude, point.y);
            maxLongitude = Math.max(maxLongitude, point.x);
            minLongitude = Math.min(minLongitude, point.x);

            if (Math.abs(maxLatitude - minLatitude) > MIN_RECTANGLE_SIZE
                    || Math.abs(maxLongitude - minLongitude) > MIN_RECTANGLE_SIZE) {
                double density = nPoints / ((maxLatitude - minLatitude) * (maxLongitude - minLongitude));
                if (density < MIN_DENSITY) {
                    cutoffs.add(i);
                    centroids.add(currentCentroid);
                    currentCentroid = point;
                    nPoints = 1;
                    maxLatitude = currentCentroid.y;
                    minLatitude = currentCentroid.y;
                    maxLongitude = currentCentroid.x;
                    minLongitude = currentCentroid.x;
                    continue;
                }
            }

            currentCentroid = new Point(
                    ((nPoints - 1) * currentCentroid.x + point.x) / nPoints,
                    ((nPoints - 1) * currentCentroid.y + point.y) / nPoints
            );
        }

        cutoffs.add(trajectory.size());
        centroids.add(currentCentroid);

        System.out.println("Cutoffs: " + cutoffs);
        System.out.println("Centroids: " + centroids);
    }

    // 定义二维点的类
    static class Point {
        double x; // 经度
        double y; // 纬度

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
