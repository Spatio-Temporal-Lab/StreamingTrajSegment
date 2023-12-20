package org.ubcomp.sts.executor;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.method.staypointsegment.AbstractStayPointSegmentForF1Score;
import org.ubcomp.sts.method.staypointsegment.StayPointSegmentWithGridOptForF1Score;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

public class LocalProcessFunctionGridForF1Score extends AbstractLocalProcessFunctionForF1Score {

    private final double maxD;
    private final long minT;
    private final Grid grid;

    public LocalProcessFunctionGridForF1Score(String pathIn, String pathOut, String dir, double maxD, long minT, int gridSize) {
        super(pathIn, pathOut, dir);
        this.maxD = maxD;
        this.minT = minT;
        grid = new Grid(maxD, gridSize);
    }

    @Override
    public void process(PointList pointList, GpsPoint point, PointList result) throws FactoryException, TransformException {
        grid.calGirdId(point);
        pointList.add(point);
        AbstractStayPointSegmentForF1Score stayPointSegment = new StayPointSegmentWithGridOptForF1Score(pointList, maxD, minT, grid, result);
        stayPointSegment.stayPointDetection();
    }
}
