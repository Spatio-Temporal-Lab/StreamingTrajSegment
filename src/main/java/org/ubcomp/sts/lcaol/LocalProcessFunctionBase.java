package org.ubcomp.sts.lcaol;

import org.ubcomp.sts.method.staypointsegment.AbstractStayPointSegment;
import org.ubcomp.sts.method.staypointsegment.StayPointSegmentBase;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

public class LocalProcessFunctionBase extends AbstractLocalProcessFunction{

    private final double maxD;
    private final long minT;

    public LocalProcessFunctionBase(String path, double maxD, long minT) {
        super(path);
        this.maxD = maxD;
        this.minT = minT;
    }

    @Override
    public long process(PointList pointList, GpsPoint point){


        if (!pointList.hasStayPoint) {
            pointList.add(point);
            AbstractStayPointSegment stayPointDetectSegment = new StayPointSegmentBase(pointList, maxD, minT);
            stayPointDetectSegment.processWithoutStayPoints();
        } else {
            pointList.add(point);
            AbstractStayPointSegment stayPointDetectSegment = new StayPointSegmentBase(pointList, maxD, minT);
            stayPointDetectSegment.processWithStayPoints();
        }
        return 0;
    }
}
