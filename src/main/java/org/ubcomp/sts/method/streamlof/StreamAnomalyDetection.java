package org.ubcomp.sts.method.streamlof;


import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.util.CalculateDistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author syy
 */
public class StreamAnomalyDetection implements Serializable {
    public int k;
    public List<GpsPoint> dataPoints;
    public List<Double> lastPointDistances;
    public List<Double> lrd;

    public StreamAnomalyDetection( ) {
    }

    public StreamAnomalyDetection(int k) {
        this.k = k;
        this.dataPoints = new ArrayList<>();
        this.lastPointDistances = new ArrayList<>();
        this.lrd = new ArrayList<>();
    }

    public double update(GpsPoint newPoint) {
        dataPoints.add(newPoint);

        if (dataPoints.size() > k) {
            dataPoints.remove(0);
            lrd.remove(0);
        }
        if (dataPoints.size() == k) {
            return calculateScore(newPoint);
        }

        lrd.add(-1.0);
        return -1;
    }

    public double calculateScore(GpsPoint newPoint) {
        lastPointDistances.clear();
        double sumDistance = 0;
        for (int i = dataPoints.size() - 2; i >= 0; i--) {
            GpsPoint currentPoint = dataPoints.get(i);
            double distance = CalculateDistance.calculateDistance(currentPoint, newPoint);
            sumDistance += distance;
            lastPointDistances.add(distance);
        }
        double newLrd = (dataPoints.size() - 1) / sumDistance;
        lrd.add(newLrd);

        double neighborLrd = 0;
        double neighborCount = 0;
        for (double nLrd : lrd) {
            if (nLrd != -1.0) {
                neighborLrd += nLrd;
                neighborCount++;
            }
        }
        double avgNeighborLrd =  neighborLrd / neighborCount;

        return avgNeighborLrd / newLrd;
    }

    public void deletePoint() {
        dataPoints.remove(dataPoints.size() - 1);
        lrd.remove(lrd.size() - 1);
    }

}
