package org.ubcomp.sts.method.spds;

import org.ubcomp.sts.object.PointList;

import java.util.List;

/**
 * @author syy
 */
public interface SpdsAlgorithmMergeDistance {
    void hasStayPoints(PointList pointList, List<Double> distances, double maxD, long minT);

    void hasNotStayPoints(PointList pointList, List<Double> distances, double maxD, long minT);
}
