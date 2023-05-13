package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;

public class CalculateDistance {
    public static double calculateDistance(GpsPoint p1, GpsPoint p2) {
        double radLat1 = Math.toRadians(p1.lat);
        double radLon1 = Math.toRadians(p1.lng);
        double radLat2 = Math.toRadians(p2.lat);
        double radLon2 = Math.toRadians(p2.lng);

        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        return Math.sqrt(Math.pow(deltaLat, 2) + Math.pow(deltaLon, 2)) * 6378.137 * 1000 + 1e-8;
    }
}
