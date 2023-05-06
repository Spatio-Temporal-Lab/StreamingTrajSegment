package org.ubcomp.sts.index;


import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author syy
 */
public class Gird implements Serializable {
    private static final double LNG1 = 76;
    private static final double LNG2 = 123;
    private static final double LAT1 = 21;
    private static final double LAT2 = 45;

    //static double D = 45*Math.sqrt(2)/4;
    static double D = 15.90990257669732;
    //static double D = 450;

    //static double deltaLon = D * 360 / (2 * Math.PI * 6371004 * Math.cos((lat1 + lat2) * Math.PI / 360));
    static double deltaLon = 1.7060465335112093E-4;
    //1.669231915437254E-4;
    //static double deltaLat = D * 360 / (2 * Math.PI * 6371004);
    static double deltaLat = 1.4308110152011678E-4;
    //1.4308110152011678E-4;

    public Gird() {
    }

    public static List<Double> calGird(double lat, double lng) {
        double lonCol = Math.floor((lng - (LNG1 - deltaLon) / 2) / deltaLon);
        double latCol = Math.floor((lat - (LAT1 - deltaLat) / 2) / deltaLat);
        List<Double> a = new ArrayList<>();
        a.add(lonCol);
        a.add(latCol);
        return a;
    }

    public static int inArea(GpsPoint p0, GpsPoint p1) {

        if (!p0.flag) {
            List<Double> aa = Gird.calGird(p0.lat, p0.lng);
            p0.latCol = aa.get(0);
            p0.lonCol = aa.get(1);
            p0.flag = true;
        }
        if (!p1.flag) {
            List<Double> aa = Gird.calGird(p1.lat, p1.lng);
            p1.latCol = aa.get(0);
            p1.lonCol = aa.get(1);
            p1.flag = true;
        }

        double lonCol = p0.lonCol;
        double latCol = p0.latCol;

        int x = Math.abs((int) (lonCol - p1.lonCol));
        int y = Math.abs((int) (latCol - p1.latCol));


        return Math.max(x, y);

    }
}
