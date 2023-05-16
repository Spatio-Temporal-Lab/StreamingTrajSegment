package org.ubcomp.sts.index;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;

public class Gird implements Serializable {
    private static final double REF_LNG = 76;
    private static final double REF_LAT = 21;
    static double deltaLon = 1.7060465335112093E-4;
    static double deltaLat = 1.4308110152011678E-4;

    public Gird() { }

    public static double[] calGird(double lat, double lng) {
        double lonCol = Math.floor((lng - REF_LNG) / deltaLon);
        double latCol = Math.floor((lat - REF_LAT) / deltaLat);
        return new double[]{lonCol, latCol};
    }

    public static int gridManhattan(GpsPoint p0, GpsPoint p1) {
        if (!p0.gridFlag) {
            double[] aa = Gird.calGird(p0.lat, p0.lng);
            p0.latCol = aa[0];
            p0.lonCol = aa[1];
            p0.gridFlag = true;
        }
        if (!p1.gridFlag) {
            double[] aa = Gird.calGird(p1.lat, p1.lng);
            p1.latCol = aa[0];
            p1.lonCol = aa[1];
            p1.gridFlag = true;
        }

        int x = (int) Math.abs(p0.lonCol - p1.lonCol);
        int y = (int) Math.abs(p0.latCol - p1.latCol);

        return Math.max(x, y);
    }
}
