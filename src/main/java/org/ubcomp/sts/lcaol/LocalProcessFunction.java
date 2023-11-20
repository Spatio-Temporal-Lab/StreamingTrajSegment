package org.ubcomp.sts.lcaol;

import org.ubcomp.sts.method.staypointsegment.StayPointSegmentBase;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.IOException;
import java.text.ParseException;

public class LocalProcessFunction extends AbstractLocalProcessFunction{

    private final double maxD;
    private final long minT;

    public LocalProcessFunction(String path, double maxD, long minT) {
        super(path);
        this.maxD = maxD;
        this.minT = minT;
    }

    @Override
    public long process(PointList pointList, GpsPoint point) throws ParseException, IOException {
        if (!pointList.hasStayPoint) {
            //将点加入临时列表中
            pointList.add(point);
            StayPointSegmentBase stayPointDetectSegment = new StayPointSegmentBase(pointList, maxD, minT);
            stayPointDetectSegment.processWithoutStayPoints();
        } else {
            StayPointSegmentBase stayPointDetectSegment = new StayPointSegmentBase(pointList, maxD, minT);
            stayPointDetectSegment.processWithStayPoints();
        }
        return 0;
    }
}
