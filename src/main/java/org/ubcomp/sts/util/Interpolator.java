package org.ubcomp.sts.util;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Interpolator implements Serializable {
    public Interpolator() {
    }

    public static List<GpsPoint> interpolatePoints(List<GpsPoint> points, GpsPoint newPoint) throws ParseException {

        GpsPoint prevPoint1 = points.get(0);
        GpsPoint prevPoint2 = points.get(1);
        GpsPoint prevPoint3 = points.get(2);
        List<GpsPoint> interpolatedPoints = new ArrayList<>();

        // 计算插值个数
        long timeDiff1 = prevPoint2.ingestionTime - prevPoint1.ingestionTime;
        long timeDiff2 = prevPoint3.ingestionTime - prevPoint2.ingestionTime;
        if (timeDiff1 == 0 || timeDiff2 == 0) {
            interpolatedPoints.add(newPoint);
            return interpolatedPoints;
        }
        long avgTimeDiff = 15000;
        long timeDiff = newPoint.ingestionTime - prevPoint3.ingestionTime;
        int numInterpolatedPoints;
        numInterpolatedPoints = (int) ((newPoint.ingestionTime - prevPoint3.ingestionTime) / avgTimeDiff) - 1;
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
            interpolatedPoints.add(new GpsPoint(interpolatedLng, interpolatedLat, newPoint.tid, interpolatedTimestamp, 0));
        }
        // 将插值点和最新点加入结果列表并返回
        interpolatedPoints.add(newPoint);
        return interpolatedPoints;
    }


    public static GpsPoint interpolatePosition(List<GpsPoint> points, long t) throws ParseException {

        long tf = points.get(0).ingestionTime;

        SimpleRegression regression1_y = new SimpleRegression();
        for (GpsPoint point : points) {
            regression1_y.addData(point.ingestionTime - tf, point.lng);
        }
        double p_f_y = regression1_y.predict(t - tf);
        SimpleRegression regression1_x = new SimpleRegression();
        for (GpsPoint point : points) {
            regression1_x.addData(point.ingestionTime - tf, point.lat);
        }
        double p_f_x = regression1_x.predict(t - tf);
        return new GpsPoint(p_f_y, p_f_x, points.get(0).tid, t, 0);
    }

}
