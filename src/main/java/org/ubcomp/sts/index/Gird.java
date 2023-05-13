package org.ubcomp.sts.index;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;

public class Gird implements Serializable {
    private static final double LNG1 = 76;
    private static final double LNG2 = 123;
    private static final double LAT1 = 21;
    private static final double LAT2 = 45;

    static double D = 15.90990257669732;
    static double deltaLon = 1.7060465335112093E-4;
    static double deltaLat = 1.4308110152011678E-4;

    public Gird() {
    }

    public static double[] calGird(double lat, double lng) {
        double lonCol = Math.floor((lng - (LNG1 - deltaLon) / 2) / deltaLon);
        double latCol = Math.floor((lat - (LAT1 - deltaLat) / 2) / deltaLat);
        return new double[]{lonCol, latCol};
    }

    public static int inArea(GpsPoint p0, GpsPoint p1) {
        if (!p0.flag) {
            double[] aa = Gird.calGird(p0.lat, p0.lng);
            p0.latCol = aa[0];
            p0.lonCol = aa[1];
            p0.flag = true;
        }
        if (!p1.flag) {
            double[] aa = Gird.calGird(p1.lat, p1.lng);
            p1.latCol = aa[0];
            p1.lonCol = aa[1];
            p1.flag = true;
        }

        double lonCol = p0.lonCol;
        double latCol = p0.latCol;

        int x = (int) Math.abs(lonCol - p1.lonCol);
        int y = (int) Math.abs(latCol - p1.latCol);

        return Math.max(x, y);
    }
}
