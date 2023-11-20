package org.ubcomp.sts.index;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;

public class Grid implements Serializable {

    double maxD;

    private static double lng1 = 103.987863;
    private static double lng2 = 110.825806;
    private static double lat1 = 10.901805;
    private static double lat2 = 31.367676;

    double D ;

    double deltaLon ;
    //1.669231915437254E-4;
    double deltaLat;
    //1.4308110152011678E-4;


    public Grid(double maxD) {
        this.maxD = maxD;
        D = maxD * Math.sqrt(2) / 4;
        deltaLat = D * 360 / (2 * Math.PI * 6371004);
        deltaLon = D * 360 / (2 * Math.PI * 6371004 * Math.cos((lat1 + lat2) * Math.PI / 360));
    }

    public  int[] calGrid(double lat, double lng) {

        int lonCol = (int) ((lng - lng1 ) / deltaLon);
        int latCol = (int) ((lat - lat1 ) / deltaLat);

        //System.out.println(lonCol+" "+latCol+" "+lonCol1+" "+latCol1);

        return new int[]{lonCol, latCol};



    }

    public int gridManhattan(GpsPoint p0, GpsPoint p1) {
        if (!p0.gridFlag) {
            int[] aa = calGrid(p0.lat, p0.lng);
            p0.latCol = aa[0];
            p0.lonCol = aa[1];
            p0.gridFlag = true;
        }
        if (!p1.gridFlag) {
            int[] aa = calGrid(p1.lat, p1.lng);
            p1.latCol = aa[0];
            p1.lonCol = aa[1];
            p1.gridFlag = true;
        }

        int x = ((p0.lonCol - p1.lonCol) < 0) ? -(p0.lonCol - p1.lonCol) : (p0.lonCol - p1.lonCol);

        int y = ((p0.latCol - p1.latCol) < 0) ? -(p0.latCol - p1.latCol) : (p0.latCol - p1.latCol);

        return Math.max(x, y);
    }

    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }



}
