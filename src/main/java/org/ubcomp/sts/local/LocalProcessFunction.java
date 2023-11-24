package org.ubcomp.sts.local;

import org.ubcomp.sts.method.staypointsegment.AbstractStayPointSegment;
import org.ubcomp.sts.method.staypointsegment.StayPointSegment;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

public class LocalProcessFunction extends AbstractLocalProcessFunction {

    private final double maxD;
    private final long minT;

    public LocalProcessFunction(String path, double maxD, long minT) {
        super(path);
        this.maxD = maxD;
        this.minT = minT;
    }

    @Override
    public void process(PointList pointList, GpsPoint point) {
        pointList.add(point);
        AbstractStayPointSegment stayPointDetectSegment = new StayPointSegment(pointList, maxD, minT);
        if (!pointList.hasStayPoint) {
            stayPointDetectSegment.processWithoutStayPoints();
        } else {
            stayPointDetectSegment.processWithStayPoints();
        }
    }
}
