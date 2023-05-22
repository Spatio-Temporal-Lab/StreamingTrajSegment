package org.ubcomp.sts.index;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;

public class Grid implements Serializable {


    private static double lng1 = 106.3;
    private static double lng2 = 106.9;
    private static double lat1 = 29.2;
    private static double lat2 = 29.8;

    static double D = 45*Math.sqrt(2)/4;

    static double deltaLon = D * 360 / (2 * Math.PI * 6371004 * Math.cos((lat1 + lat2) * Math.PI / 360));
    //1.669231915437254E-4;
    static double deltaLat = D * 360 / (2 * Math.PI * 6371004);
    //1.4308110152011678E-4;

    public Grid() { }

    public static int[] calGrid(double lat, double lng) {
        int lonCol = (int) Math.floor((lng - deltaLon) / deltaLon);
        int latCol = (int) Math.floor((lat - deltaLat) / deltaLat);
        return new int[]{lonCol, latCol};

    }

   /* private static double lng1 = 106.3;
    private static double lng2 = 106.9;
    private static double lat1 = 29.2;
    private static double lat2 = 29.8;

    static double deltaX = 45 / Math.sqrt(Math.pow(lng2 - lng1, 2) + Math.pow(lat2 - lat1, 2));
    static double deltaY = 45 / Math.sqrt(Math.pow(lng2 - lng1, 2) + Math.pow(lat2 - lat1, 2));

    public Grid() { }

    public static int[] calGrid(double lat, double lng) {
        int lonCol = (int) Math.floor((lng - lng1) / deltaX);
        int latCol = (int) Math.floor((lat - lat1) / deltaY);
        return new int[]{lonCol, latCol};
    }*/

    public static int gridManhattan(GpsPoint p0, GpsPoint p1) {
        if (!p0.gridFlag) {
            int[] aa = Grid.calGrid(p0.lat, p0.lng);
            p0.latCol = aa[0];
            p0.lonCol = aa[1];
            p0.gridFlag = true;
        }
        if (!p1.gridFlag) {
            int[] aa = Grid.calGrid(p1.lat, p1.lng);
            p1.latCol = aa[0];
            p1.lonCol = aa[1];
            p1.gridFlag = true;
        }

        int x =  Math.abs(p0.lonCol - p1.lonCol);
        int y =  Math.abs(p0.latCol - p1.latCol);

        return Math.max(x, y);
    }
}
