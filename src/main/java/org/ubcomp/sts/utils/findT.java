package org.ubcomp.sts.utils;

import org.ubcomp.sts.objects.gpsPoint;

import java.util.List;

public class findT {
    public static int findT(List<gpsPoint> pointList, long t) {
        long t2 = pointList.get(pointList.size() - 1).ingestionTime;
        for (int i = pointList.size() - 2; i >= 0; i--) {
            long dt = pointList.get(i).ingestionTime - t2;
            if (Math.abs(dt) > t) {
                return i;
            }
        }
        return 0;
    }
}
