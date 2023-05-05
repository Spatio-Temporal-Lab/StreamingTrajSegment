package org.ubcomp.sts.tlof;

import org.ubcomp.sts.objects.GpsPoint;
import org.ubcomp.sts.utils.CalculateDistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamLOF implements Serializable {

    public int k;
    public int w;
    public List<GpsPoint> dataPoints;
    public int currentIndex;
    public double templof = 0.0;
    public boolean flag = true;

    public List<Double> Distances;

    public StreamLOF(){}
    public StreamLOF(int k, int w) {
        this.k = k;
        this.w = w;
        this.dataPoints = new ArrayList<>();
        this.currentIndex = 0;
        this.Distances = new ArrayList<>();
    }

    public double update(GpsPoint point) {
        dataPoints.add(point);
        if (dataPoints.size() > w) {
            dataPoints.remove(0);
        }
        currentIndex++;
        if (currentIndex >= w) {
            double[] lofValues = calculateLOF(point);
            // 输出最新的点的LOF值
            //lofValues[w-1]>=0 && lofValues[w-1]<=100000
            templof = lofValues[w-1];
            flag = true;
            return templof;
        }
        flag = true;
        return  -1;
    }

    public double[] calculateLOF(GpsPoint p0) {
        List<GpsPoint> kNNs;
        List<GpsPoint> kNNkNNs = new ArrayList<>();
        double[] lofValues = new double[w];
        for (int i = w-1; i < w; i++) {
            GpsPoint currentDataPoint = dataPoints.get(i);
            kNNs = getKNNs(currentDataPoint, k, p0);
            double reachabilitySum = 0.0;

            for (GpsPoint kNN : kNNs) {
                double kDistance = CalculateDistance.calculateDistance(kNN, currentDataPoint);
                kNNkNNs = getKNNs(kNN, k, p0);
                double reachabilityDistance = Math.max(kDistance, getReachabilityDistance(currentDataPoint, kNN, kNNkNNs));
                reachabilitySum += reachabilityDistance;
            }
            double lrd = 1.0 / (reachabilitySum / k);
            double lof = 0.0;
            for (GpsPoint kNN : kNNs) {
                double kNNlrd = 1.0 / (getReachabilitySum(kNN, kNNkNNs, p0) / k);
                lof += kNNlrd / lrd;
            }
            lof /= k;
            lofValues[i] = lof;
        }
        return lofValues;
    }

    public List<GpsPoint> getKNNs(GpsPoint dataPoint, int k, GpsPoint point0) {
        List<GpsPoint> kNNs = new ArrayList<>();
        List<GpsPoint> allDataPoints = new ArrayList<>(dataPoints);
        allDataPoints.remove(dataPoint);
        List<PointDistance> distances = allDataPoints.stream()
                .map(point -> new PointDistance(point, CalculateDistance.calculateDistance(point, dataPoint)))
                .collect(Collectors.toList());

        if (dataPoint == point0 && flag==true){
            Distances = distances.stream()
                    .map(PointDistance::getDistance)
                    .collect(Collectors.toList());
            flag = false;
        };

        List<GpsPoint> sortedPoints = distances.stream()
                .sorted()
                .map(PointDistance::getPoint)
                .collect(Collectors.toList());

        for (int i = 0; i < k; i++) {
            kNNs.add(sortedPoints.get(i));
        }
        return kNNs;
    }


    public double getReachabilityDistance(GpsPoint a, GpsPoint b, List<GpsPoint> kNNs) {
        double kDistance = CalculateDistance.calculateDistance(b, a);
        double reachabilityDistance = kDistance;
        for (GpsPoint kNN : kNNs) {
            double kNNDistance = CalculateDistance.calculateDistance(kNN, a);
            reachabilityDistance = Math.max(reachabilityDistance, kNNDistance);
        }
        return reachabilityDistance;
    }

    public double getReachabilitySum(GpsPoint dataPoint, List<GpsPoint> kNNs, GpsPoint point0) {
        double reachabilitySum = 0.0;
        for (GpsPoint kNN : kNNs) {
            double kDistance = CalculateDistance.calculateDistance(kNN, dataPoint);
            List<GpsPoint> kNNkNNs = getKNNs(kNN, k,point0);
            double reachabilityDistance = Math.max(kDistance, getReachabilityDistance(dataPoint, kNN, kNNkNNs));
            reachabilitySum += reachabilityDistance;
        }
        return reachabilitySum;
    }

    public void deletePoint(){
        dataPoints.remove(dataPoints.size()-1);
        currentIndex--;
    }

    public class PointDistance implements Comparable<PointDistance> {
        public GpsPoint point;
        public  double distance;

        public PointDistance(){}

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
            return point +"  "+ distance;
        }
    }


}
