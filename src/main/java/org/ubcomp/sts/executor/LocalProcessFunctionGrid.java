package org.ubcomp.sts.executor;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.method.staypointsegment.AbstractStayPointSegment;
import org.ubcomp.sts.method.staypointsegment.StayPointSegmentWithGridOpt;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

public class LocalProcessFunctionGrid extends AbstractLocalProcessFunction {

    private final double maxD;
    private final long minT;
    private final Grid grid;

    public LocalProcessFunctionGrid(String path, double maxD, long minT, int gridSize) {
        super(path);
        this.maxD = maxD;
        this.minT = minT;
        grid = new Grid(maxD, gridSize);
    }

    @Override
    public void process(PointList pointList, GpsPoint point) throws FactoryException, TransformException {
            grid.calGirdId(point);
            pointList.add(point);
            AbstractStayPointSegment stayPointSegment = new StayPointSegmentWithGridOpt(pointList, maxD, minT, grid);
            stayPointSegment.stayPointDetection();
    }
}
