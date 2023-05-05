package org.ubcomp.sts.index;


import org.ubcomp.sts.objects.GpsPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Gird implements Serializable {
    private static double lng1 = 76;
    private static double lng2 = 123;
    private static double lat1 = 21;
    private static double lat2 = 45;

    //static double D = 45*Math.sqrt(2)/4;
    static double D = 15.90990257669732;
    //static double D = 450;

    //static double deltaLon = D * 360 / (2 * Math.PI * 6371004 * Math.cos((lat1 + lat2) * Math.PI / 360));
    static double deltaLon = 1.7060465335112093E-4;
            //1.669231915437254E-4;
    //static double deltaLat = D * 360 / (2 * Math.PI * 6371004);
    static double deltaLat = 1.4308110152011678E-4;
                    //1.4308110152011678E-4;

    public Gird(){}

    public static List calUGird(double lat, double lng){
        double LONCOL = Math.floor( (lng - (lng1 - deltaLon)/2)/deltaLon  );
        double LATCOL = Math.floor( (lat - (lat1 - deltaLat)/2)/deltaLat  );
        List<Double> a = new ArrayList<>();
        a.add(LONCOL);
        a.add(LATCOL);
        return a;
    }

    public static int inArea(GpsPoint p0, GpsPoint p1){
        if (p0.flag == false){
            List<Double> aa = Gird.calUGird(p0.lat, p0.lng);
            p0.LATCOL = aa.get(0);
            p0.LONCOL = aa.get(1);
            p0.flag = true;
        }
        if (p1.flag == false){
            List<Double> aa = Gird.calUGird(p1.lat, p1.lng);
            p1.LATCOL = aa.get(0);
            p1.LONCOL = aa.get(1);
            p1.flag = true;
        }

        double LONCOL =  p0.LONCOL;
        double LATCOL =  p0.LATCOL;

        int x = Math.abs((int) (LONCOL - p1.LONCOL));
        int y = Math.abs((int) (LATCOL - p1.LATCOL));



        return Math.max(x,y);

    }
}
