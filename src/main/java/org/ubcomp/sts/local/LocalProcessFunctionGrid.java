package org.ubcomp.sts.local;

import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.method.staypointsegment.AbstractStayPointSegment;
import org.ubcomp.sts.method.staypointsegment.StayPointSegmentWithGridOpt;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.IOException;
import java.text.ParseException;

public class LocalProcessFunctionGrid extends AbstractLocalProcessFunction {

    private final double maxD;
    private final long minT;
    private final Grid grid;


    public LocalProcessFunctionGrid(String path, double maxD, long minT) {
        super(path);
        this.maxD = maxD;
        this.minT = minT;
        grid = new Grid(maxD);
    }

    @Override
    public long process(PointList pointList, GpsPoint point) throws ParseException, IOException {
        AbstractStayPointSegment stayPointSegment = new StayPointSegmentWithGridOpt(pointList,  maxD, minT, grid);
        if (!pointList.hasStayPoint) {
            pointList.add(point);
            stayPointSegment.processWithoutStayPoints();
        } else {
            pointList.add(point);
            stayPointSegment.processWithStayPoints();
        }
        return 0;
    }
}
