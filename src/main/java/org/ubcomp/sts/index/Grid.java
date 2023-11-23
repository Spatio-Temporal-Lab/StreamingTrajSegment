package org.ubcomp.sts.index;

import org.ubcomp.sts.object.GpsPoint;

import java.io.Serializable;

public class Grid implements Serializable {

    private final static double lng1 = 103.987863;
    private final static double lat1 = 10.901805;
    private final static double lat2 = 31.367676;
    private final double condition;
    //0~10
    int[][] matrix1 = {
        {0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100},
        {1, 2, 5, 10, 17, 26, 37, 50, 65, 82, 101},
        {4, 5, 8, 13, 20, 29, 40, 53, 68, 85, 104},
        {9, 10, 13, 18, 25, 34, 45, 58, 73, 90, 109},
        {16, 17, 20, 25, 32, 41, 52, 65, 80, 97, 116},
        {25, 26, 29, 34, 41, 50, 61, 74, 89, 106, 125},
        {36, 37, 40, 45, 52, 61, 72, 85, 100, 117, 136},
        {49, 50, 53, 58, 65, 74, 85, 98, 113, 130, 149},
        {64, 65, 68, 73, 80, 89, 100, 113, 128, 145, 164},
        {81, 82, 85, 90, 97, 106, 117, 130, 145, 162, 181},
        {100, 101, 104, 109, 116, 125, 136, 149, 164, 181, 200}
    };
    int[][] matrix2 = {
        {4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144},
        {9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169},
        {16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196},
        {25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225},
        {36, 49, 64, 81, 100, 121, 144, 169, 196, 225, 256},
        {49, 64, 81, 100, 121, 144, 169, 196, 225, 256, 289},
        {64, 81, 100, 121, 144, 169, 196, 225, 256, 289, 324},
        {81, 100, 121, 144, 169, 196, 225, 256, 289, 324, 361},
        {100, 121, 144, 169, 196, 225, 256, 289, 324, 361, 400},
        {121, 144, 169, 196, 225, 256, 289, 324, 361, 400, 441},
        {144, 169, 196, 225, 256, 289, 324, 361, 400, 441, 484}
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
        if (diffLon <= -1 && diffLat <= -1) {
            if (matrix1[diffLat][diffLon] >= condition) {
                return AreaEnum.PRUNED_AREA;
            } else if (matrix2[diffLat][diffLon] <= condition) {
                return AreaEnum.CONFIRMED_AREA;
            }
        } else {
            int condition1_Lat = diffLat - 1;
            int condition1_Lon = diffLon - 1;
            int addAll1 = condition1_Lat * condition1_Lat + condition1_Lon * condition1_Lon;
            if (addAll1 >= condition) {
                return AreaEnum.PRUNED_AREA;
            }
            int condition2_Lat = diffLat + 1;
            int condition2_Lon = diffLon + 1;
            int addAll2 = condition2_Lat * condition2_Lat + condition2_Lon * condition2_Lon;
            if (addAll2 <= condition) {
                return AreaEnum.CONFIRMED_AREA;
            }
        }
        return AreaEnum.CHECK_AREA;
    }
}
