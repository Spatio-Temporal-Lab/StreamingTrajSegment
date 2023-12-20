package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;

public class CalculateDistance {
    public static double calDistance(GpsPoint p1, GpsPoint p2) {
        double lat1 = p1.lat;
        double lat2 = p2.lat;
        double lng1 = p1.lng;
        double lng2 = p2.lng;
        double radLat11 = calRad(lat1);
        double radLat21 = calRad(lat2);
        double a = radLat11 - radLat21;
        double b = calRad(lng1) - calRad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat11) * Math.cos(radLat21)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }

    public static double calRad(double a) {
        return a * Math.PI / 180.0;
    }
}
