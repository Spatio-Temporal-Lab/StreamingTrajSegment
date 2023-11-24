package org.ubcomp.sts.index;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;

public class Grid implements Serializable {

    private final static double lng1 = 103.987863;
    private final static double lat1 = 10.901805;
    private final static double lat2 = 31.367676;
    private final double condition;
    //0~100
    private final static int[] squareArray = {
            1, 0, 1, 4, 9, 16, 25, 36, 49, 64, 81,
            100, 121, 144, 169, 196, 225, 256, 289, 324, 361,
            400, 441, 484, 529, 576, 625, 676, 729, 784, 841,
            900, 961, 1024, 1089, 1156, 1225, 1296, 1369, 1444, 1521,
            1600, 1681, 1764, 1849, 1936, 2025, 2116, 2209, 2304, 2401,
            2500, 2601, 2704, 2809, 2916, 3025, 3136, 3249, 3364, 3481,
            3600, 3721, 3844, 3969, 4096, 4225, 4356, 4489, 4624, 4761,
            4900, 5041, 5184, 5329, 5476, 5625, 5776, 5929, 6084, 6241,
            6400, 6561, 6724, 6889, 7056, 7225, 7396, 7569, 7744, 7921,
            8100, 8281, 8464, 8649, 8836, 9025, 9216, 9409, 9604, 9801,
            10000
    };
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

        if (diffLonPlusOne <= 100 && diffLatPlusOne <= 100) {
            if (squareArray[diffLonMinusOne + 1] + squareArray[diffLatMinusOne + 1] >= condition) {
                return AreaEnum.PRUNED_AREA;
            } else if (squareArray[diffLonPlusOne + 1] + squareArray[diffLatPlusOne + 1] <= condition) {
                return AreaEnum.CONFIRMED_AREA;
            } else {
                return AreaEnum.CHECK_AREA;
            }
        } else {
            if (diffLonMinusOne * diffLonMinusOne + diffLatMinusOne * diffLatMinusOne >= condition) {
                return AreaEnum.PRUNED_AREA;
            }
            if (diffLonPlusOne * diffLonPlusOne + diffLatPlusOne * diffLatPlusOne <= condition) {
                return AreaEnum.CONFIRMED_AREA;
            }
            return AreaEnum.CHECK_AREA;
        }
    }
}