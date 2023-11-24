package org.ubcomp.sts.local;

import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.method.staypointsegment.AbstractStayPointSegment;
import org.ubcomp.sts.method.staypointsegment.StayPointSegmentWithGridOpt;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

public class LocalProcessFunctionGrid extends AbstractLocalProcessFunction {

    private final double maxD;
    private final long minT;
    private final Grid grid;


    public LocalProcessFunctionGrid(String path, double maxD, long minT) {
        super(path);
        this.maxD = maxD;
        this.minT = minT;
        grid = new Grid(maxD,1);
    }

    @Override
    public void process(PointList pointList, GpsPoint point) {


        if (!pointList.hasStayPoint) {
            grid.calGirdId(point);
            pointList.add(point);
            AbstractStayPointSegment stayPointSegment = new StayPointSegmentWithGridOpt(pointList, maxD, minT, grid);
            stayPointSegment.processWithoutStayPoints();
        } else {
            grid.calGirdId(point);
            pointList.add(point);
            AbstractStayPointSegment stayPointSegment = new StayPointSegmentWithGridOpt(pointList, maxD, minT, grid);
            stayPointSegment.processWithStayPoints();
        }
    }
}
