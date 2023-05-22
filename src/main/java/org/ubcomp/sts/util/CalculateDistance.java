package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;

public class CalculateDistance {
    public static double calculateDistance(GpsPoint p1, GpsPoint p2) {
       /* double radLat1 = Math.toRadians(p1.lat);
        double radLon1 = Math.toRadians(p1.lng);
        double radLat2 = Math.toRadians(p2.lat);
        double radLon2 = Math.toRadians(p2.lng);

        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        return Math.sqrt(Math.pow(deltaLat, 2) + Math.pow(deltaLon, 2)) * 6378.137 * 1000 + 1e-8;*/


        double lat1 = p1.lat;
        double lat2 = p2.lat;
        double lng1 = p1.lng;
        double lng2 = p2.lng;
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s+1e-8;


    }

    public static double rad(double a) {
        return a * Math.PI / 180.0;
    }
}
