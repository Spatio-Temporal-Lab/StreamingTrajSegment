package org.ubcomp.sts.object;

import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author syy
 */
public class Container implements Serializable {

    public Container() {
    }

    public int count = 0;
    public List<GpsPoint> latePoints = new ArrayList<>();
    public List<GpsPoint> tempPoints = new ArrayList<>();
    public GpsPoint tempPoint;

    public int i = 1;
    public double radius = 0;
    public List<GpsPoint> centroids = new ArrayList<>();
    public List<Integer> cutofs = new ArrayList<>();
    public int nPoints = 1;
    public GpsPoint currentCentroid;

    public StreamAnomalyDetection lof = new StreamAnomalyDetection(20);
}
