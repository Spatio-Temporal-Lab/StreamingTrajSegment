package org.ubcomp.sts.method.srd;

import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.object.SrdContainer;
import org.ubcomp.sts.util.CalculateDistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author syy
 */
public class Srd implements Serializable {
    public static void processSrd(PointList pointList, GpsPoint point, SrdContainer container, double minR, double minDensity) {
        GpsPoint currentCentroid = container.currentCentroid;
        int nPoints = container.nPoints;
        double radius = container.radius;
        List<Integer> cutofs = container.cutofs;
        int i = container.i;
        List<GpsPoint> centroids = container.centroids;
        if (currentCentroid == null) {
            currentCentroid = point;
        }
        pointList.add(point);
        nPoints++;
        double distance = CalculateDistance.calculateDistance(point, currentCentroid);
        radius = Math.max(radius, distance);
        if (radius > minR) {
            double density = nPoints / (Math.PI * radius * radius);
            //|| pointList.getSize()>5000
            if (density < minDensity) {
                cutofs.add(i);
                centroids.add(currentCentroid);
                pointList.pointList = new ArrayList<>();
                pointList.add(point);
                currentCentroid = point;
                radius = 0;
                nPoints = 1;
            }
        }
        currentCentroid.lng = ((nPoints - 1) * currentCentroid.lng + point.lng) / nPoints;
        currentCentroid.lat = ((nPoints - 1) * currentCentroid.lat + point.lat) / nPoints;
        i++;
        container.i = i;
        container.radius = radius;
        container.centroids = centroids;
        container.cutofs = cutofs;
        container.nPoints = nPoints;
        container.currentCentroid = currentCentroid;
    }
}
