package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;

import java.util.List;

/**
 * @author syy
 */
public class FindTimeT {
    public static int findT(List<GpsPoint> pointList, long t) {
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
