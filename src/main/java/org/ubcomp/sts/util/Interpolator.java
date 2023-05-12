package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author syy
 */
public class Interpolator implements Serializable {
    public Interpolator() {
    }

    public List<GpsPoint> interpolatePoints(List<GpsPoint> points, GpsPoint newPoint) throws ParseException {

        GpsPoint prevPoint1 = points.get(0);
        GpsPoint prevPoint2 = points.get(1);
        GpsPoint prevPoint3 = points.get(2);
        List<GpsPoint> interpolatedPoints = new ArrayList<>();
        long timeDiff1 = prevPoint2.ingestionTime - prevPoint1.ingestionTime;
        long timeDiff2 = prevPoint3.ingestionTime - prevPoint2.ingestionTime;
        long avgTimeDiff = (timeDiff1 + timeDiff2) / 2;
        long timeDiff = newPoint.ingestionTime - prevPoint3.ingestionTime;
        int numInterpolatedPoints;
        if (avgTimeDiff == 0) {
            numInterpolatedPoints = 0;
        } else {
            numInterpolatedPoints = (int) ((newPoint.ingestionTime - prevPoint3.ingestionTime) / avgTimeDiff) - 1;
        }
        if (numInterpolatedPoints <= 0) {
            interpolatedPoints.add(newPoint);
            return interpolatedPoints;
        }
        long step = timeDiff / (numInterpolatedPoints + 1);
        List<Long> interpolatedTimestamps = new ArrayList<>();
        for (int i = 0; i < numInterpolatedPoints; i++) {
            long interpolatedTimestamp = prevPoint3.ingestionTime + (i + 1) * step;
            interpolatedTimestamps.add(interpolatedTimestamp);
        }
        double deltaIngestionTime1 = prevPoint2.ingestionTime - prevPoint1.ingestionTime;
        double deltaIngestionTime2 = prevPoint3.ingestionTime - prevPoint2.ingestionTime;
        for (long interpolatedTimestamp : interpolatedTimestamps) {
            double ratio2 = (double) (interpolatedTimestamp - prevPoint2.ingestionTime) / deltaIngestionTime2;
            double ratio1 = (double) (prevPoint2.ingestionTime - interpolatedTimestamp) / deltaIngestionTime1;
            double interpolatedLat = (prevPoint1.lat + prevPoint2.lat * ratio1 + prevPoint3.lat * ratio2) / (1 + ratio1 + ratio2);
            double interpolatedLng = (prevPoint1.lng + prevPoint2.lng * ratio1 + prevPoint3.lng * ratio2) / (1 + ratio1 + ratio2);
            interpolatedPoints.add(new GpsPoint(interpolatedLng, interpolatedLat, newPoint.tid, new Timestamp(interpolatedTimestamp).toString(), 0));
        }
        interpolatedPoints.add(newPoint);
        return interpolatedPoints;
    }


    public GpsPoint interpolatePosition(List<GpsPoint> points, long t) throws ParseException {
        GpsPoint p1 = points.get(0);
        GpsPoint p2 = points.get(1);
        GpsPoint p3 = points.get(2);
        long t1 = p1.ingestionTime;
        long t2 = p2.ingestionTime;
        long t3 = p3.ingestionTime;
        double dt1 = t - t1;
        double dt2 = t - t2;
        double dt3 = t - t3;
        double w1 = dt2 * dt3 / ((t1 - t2) * (t1 - t3));
        double w2 = dt1 * dt3 / ((t2 - t1) * (t2 - t3));
        double w3 = dt1 * dt2 / ((t3 - t1) * (t3 - t2));
        double latInterpolated = w1 * p1.lat + w2 * p2.lat + w3 * p3.lat;
        double lngInterpolated = w1 * p1.lng + w2 * p2.lng + w3 * p3.lng;
        String tid = p1.tid;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ingestionTime = formatter.format(new Date(t));
        return new GpsPoint(lngInterpolated, latInterpolated, tid, ingestionTime, 0);
    }

}
