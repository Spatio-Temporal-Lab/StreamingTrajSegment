package org.ubcomp.sts.method.streamlof;

import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.util.CalculateDistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author syy
 */
public class StreamLof implements Serializable {

    public int k;
    public int w;
    public List<GpsPoint> dataPoints;
    public int currentIndex;
    public double score = 0.0;
    public boolean isLastPoint = true;
    public List<Double> lastPointDistances;

    public StreamLof() {
    }

    public StreamLof(int k, int w) {
        this.k = k;
        this.w = w;
        this.dataPoints = new ArrayList<>();
        this.currentIndex = 0;
        this.lastPointDistances = new ArrayList<>();
    }

    public double update(GpsPoint point) {
        dataPoints.add(point);
        if (dataPoints.size() > w) {
            dataPoints.remove(0);
        }
        currentIndex++;
        if (currentIndex >= w) {
            double[] lofValues = calculateLof(point);
            score = lofValues[w - 1];
            isLastPoint = true;
            return score;
        }
        isLastPoint = true;
        return -1;
    }

    public double[] calculateLof(GpsPoint lastPoint) {
        List<GpsPoint> knns;
        List<GpsPoint> knnKnns = new ArrayList<>();
        double[] lofValues = new double[w];
        for (int i = w - 1; i < w; i++) {
            GpsPoint currentDataPoint = dataPoints.get(i);
            knns = getKnns(currentDataPoint, k, lastPoint);
            double reachabilitySum = 0.0;

            for (GpsPoint knn : knns) {
                double kDistance = CalculateDistance.calculateDistance(knn, currentDataPoint);
                knnKnns = getKnns(knn, k, lastPoint);
                double reachabilityDistance = Math.max(kDistance, getReachabilityDistance(currentDataPoint, knn, knnKnns));
                reachabilitySum += reachabilityDistance;
            }
            double lrd = 1.0 / (reachabilitySum / k);
            double lof = 0.0;
            for (GpsPoint knn : knns) {
                double knnLrd = 1.0 / (getReachabilitySum(knn, knnKnns, lastPoint) / k);
                lof += knnLrd / lrd;
            }
            lof /= k;
            lofValues[i] = lof;
        }
        return lofValues;
    }

    public List<GpsPoint> getKnns(GpsPoint dataPoint, int k, GpsPoint lastPoint) {
        List<GpsPoint> knns = new ArrayList<>();
        List<GpsPoint> allDataPoints = new ArrayList<>(dataPoints);
        allDataPoints.remove(dataPoint);
        List<PointDistance> distances = allDataPoints.stream()
                .map(point -> new PointDistance(point, CalculateDistance.calculateDistance(point, dataPoint)))
                .collect(Collectors.toList());
        if (dataPoint == lastPoint && isLastPoint) {
            this.lastPointDistances = distances.stream()
                    .map(PointDistance::getDistance)
                    .collect(Collectors.toList());
            isLastPoint = false;
        }

        List<GpsPoint> sortedPoints = distances.stream()
                .sorted()
                .map(PointDistance::getPoint)
                .collect(Collectors.toList());

        for (int i = 0; i < k; i++) {
            knns.add(sortedPoints.get(i));
        }
        return knns;
    }


    public double getReachabilityDistance(GpsPoint a, GpsPoint b, List<GpsPoint> knns) {
        double reachabilityDistance = CalculateDistance.calculateDistance(b, a);
        for (GpsPoint knn : knns) {
            //可优化
            double knnDistance = CalculateDistance.calculateDistance(knn, a);
            reachabilityDistance = Math.max(reachabilityDistance, knnDistance);
        }
        return reachabilityDistance;
    }

    public double getReachabilitySum(GpsPoint dataPoint, List<GpsPoint> knns, GpsPoint lastPoint) {
        double reachabilitySum = 0.0;
        for (GpsPoint knn : knns) {
            double kDistance = CalculateDistance.calculateDistance(knn, dataPoint);
            List<GpsPoint> knnKnns = getKnns(knn, k, lastPoint);
            double reachabilityDistance = Math.max(kDistance, getReachabilityDistance(dataPoint, knn, knnKnns));
            reachabilitySum += reachabilityDistance;
        }
        return reachabilitySum;
    }

    public void deletePoint() {
        dataPoints.remove(dataPoints.size() - 1);
        currentIndex--;
    }

    public static class PointDistance implements Comparable<PointDistance> {
        public GpsPoint point;
        public double distance;

        public PointDistance() {
        }

        public PointDistance(GpsPoint point, double distance) {
            this.point = point;
            this.distance = distance;
        }

        public GpsPoint getPoint() {
            return point;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(PointDistance other) {
            return Double.compare(distance, other.distance);
        }

        @Override
        public String toString() {
            return point + "  " + distance;
        }
    }


}
