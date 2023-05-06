package org.ubcomp.sts.method.spds;

import org.ubcomp.sts.object.PointList;

/**
 * @author syy
 */
public interface SpdsAlgorithm {
    void hasStayPoints(PointList pointList, double maxD, long minT);

    void hasNotStayPoints(PointList pointList, double maxD, long minT);

}
