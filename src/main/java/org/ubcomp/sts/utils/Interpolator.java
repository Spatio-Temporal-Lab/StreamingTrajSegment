package org.ubcomp.sts.utils;

import org.ubcomp.sts.objects.gpsPoint;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Interpolator implements Serializable {
    public Interpolator(){}
    public  List<gpsPoint> interpolatePoints(List<gpsPoint> points, gpsPoint newPoint) throws  ParseException {

        gpsPoint prevPoint1 = points.get(0);
        gpsPoint prevPoint2 = points.get(1);
        gpsPoint prevPoint3 = points.get(2);
        List<gpsPoint> interpolatedPoints = new ArrayList<>();

        // 计算插值个数
        long timeDiff1 = prevPoint2.ingestionTime - prevPoint1.ingestionTime;
        long timeDiff2 = prevPoint3.ingestionTime - prevPoint2.ingestionTime;
        long avgTimeDiff = (timeDiff1 + timeDiff2) / 2;
        long timeDiff = newPoint.ingestionTime - prevPoint3.ingestionTime;
        int numInterpolatedPoints;
        if (avgTimeDiff == 0){
             numInterpolatedPoints = 0;
        }else {
             numInterpolatedPoints = (int) ((newPoint.ingestionTime - prevPoint3.ingestionTime) / avgTimeDiff) - 1;
        }
        if (numInterpolatedPoints <= 0) {
            // 如果插值个数小于等于0，则不需要插值，直接返回
            interpolatedPoints.add(newPoint);
            return interpolatedPoints;
        }

        // 计算每个插值点的时间戳
        long step = timeDiff / (numInterpolatedPoints + 1);
        List<Long> interpolatedTimestamps = new ArrayList<>();
        for (int i = 0; i < numInterpolatedPoints; i++) {
            long interpolatedTimestamp = prevPoint3.ingestionTime + (i + 1) * step;
            interpolatedTimestamps.add(interpolatedTimestamp);
        }

        // 计算每个插值点的经纬度
        double deltaIngestionTime1 = prevPoint2.ingestionTime - prevPoint1.ingestionTime;
        double deltaIngestionTime2 = prevPoint3.ingestionTime - prevPoint2.ingestionTime;
        for (long interpolatedTimestamp : interpolatedTimestamps) {
            double ratio2 = (double) (interpolatedTimestamp - prevPoint2.ingestionTime) / deltaIngestionTime2;
            double ratio1 = (double) (prevPoint2.ingestionTime - interpolatedTimestamp) / deltaIngestionTime1;
            double interpolatedLat = (prevPoint1.lat + prevPoint2.lat * ratio1 + prevPoint3.lat * ratio2) / (1 + ratio1 + ratio2);
            double interpolatedLng = (prevPoint1.lng + prevPoint2.lng * ratio1 + prevPoint3.lng * ratio2) / (1 + ratio1 + ratio2);
            interpolatedPoints.add(new gpsPoint(interpolatedLng, interpolatedLat, newPoint.tid, new Timestamp(interpolatedTimestamp).toString(), 0));
        }
        // 将插值点和最新点加入结果列表并返回
        interpolatedPoints.add(newPoint);
        return interpolatedPoints;
    }


    public  gpsPoint interpolatePosition(List<gpsPoint> points , long t) throws  ParseException {
        gpsPoint p1 = points.get(0);
        gpsPoint p2 = points.get(1);
        gpsPoint p3 = points.get(2);
        long t1 = p1.ingestionTime;
        long t2 = p2.ingestionTime;
        long t3 = p3.ingestionTime;
        double dt1 = t - t1;
        double dt2 = t - t2;
        double dt3 = t - t3;
        double w1 = dt2 * dt3 / ((t1 - t2) * (t1 - t3));
        double w2 = dt1 * dt3 / ((t2 - t1) * (t2 - t3));
        double w3 = dt1 * dt2 / ((t3 - t1) * (t3 - t2));
        double lat_interpolated = w1 * p1.lat + w2 * p2.lat + w3 * p3.lat;
        double lng_interpolated = w1 * p1.lng + w2 * p2.lng + w3 * p3.lng;
        String tid = p1.tid; // or p2.tid or p3.tid, it doesn't matter since they should be the same
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ingestionTime = formatter.format(new Date(t));
        gpsPoint interpolatedPoint = new gpsPoint(lng_interpolated, lat_interpolated, tid, ingestionTime, 0);
        return interpolatedPoint;
    }

}
