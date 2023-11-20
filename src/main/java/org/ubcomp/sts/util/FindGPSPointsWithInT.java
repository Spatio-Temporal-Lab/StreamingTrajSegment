package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;

import java.util.List;

/**
 * @author syy
 */
public class FindGPSPointsWithInT {
    public static int findIndex(List<GpsPoint> pointList, long t) {
        int size = pointList.size();
        long maxT = pointList.get(size - 1).ingestionTime;
        int i = 0;
        while (i < size && maxT - pointList.get(i).ingestionTime >= t) {
            i++;
        }
        return i+1;
    }
}
