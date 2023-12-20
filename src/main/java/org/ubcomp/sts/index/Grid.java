package org.ubcomp.sts.index;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;

public class Grid implements Serializable {

    private final static double lng1 = 103.987863;
    private final static double lat1 = 10.901805;
    private final static double lat2 = 31.367676;
    private final double condition;
    //0~100
    private final double deltaLon;
    private final double deltaLat;

    public Grid(double maxD, int n) {
        this.condition = 8 * n * n;
        double d = maxD * Math.sqrt(2) / (4 * n);
        deltaLat = d * 360 / (2 * Math.PI * 6371004);
        deltaLon = d * 360 / (2 * Math.PI * 6371004 * Math.cos((lat1 + lat2) * Math.PI / 360));
    }

    public void calGirdId(GpsPoint point) {
        point.latCol = (int) ((point.lat - lat1) / deltaLat);
        point.lonCol = (int) ((point.lng - lng1) / deltaLon);
    }

    public AreaEnum getArea(GpsPoint p0, GpsPoint p1) {
        int diffLon = Math.abs(p1.lonCol - p0.lonCol);
        int diffLat = Math.abs(p1.latCol - p0.latCol);
        int diffLonMinusOne = diffLon - 1;
        int diffLatMinusOne = diffLat - 1;
        int diffLonPlusOne = diffLon + 1;
        int diffLatPlusOne = diffLat + 1;

        /*if (diffLonPlusOne <= 100 && diffLatPlusOne <= 100) {
            if (squareArray[diffLonMinusOne + 1] + squareArray[diffLatMinusOne + 1] >= condition) {
                return AreaEnum.PRUNED_AREA;
            } else if (squareArray[diffLonPlusOne + 1] + squareArray[diffLatPlusOne + 1] <= condition) {
                return AreaEnum.CONFIRMED_AREA;
            } else {
                return AreaEnum.CHECK_AREA;
            }
        } else {*/
        if (diffLonMinusOne * diffLonMinusOne + diffLatMinusOne * diffLatMinusOne >= condition) {
            return AreaEnum.PRUNED_AREA;
        }
        if (diffLonPlusOne * diffLonPlusOne + diffLatPlusOne * diffLatPlusOne <= condition) {
            return AreaEnum.CONFIRMED_AREA;
        }
        return AreaEnum.CHECK_AREA;
        // }
    }
}