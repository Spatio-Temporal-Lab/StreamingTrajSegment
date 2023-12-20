package org.ubcomp.sts.executor;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.method.staypointsegment.AbstractStayPointSegment;
import org.ubcomp.sts.method.staypointsegment.StayPointSegment;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

public class LocalProcessFunctionBase extends AbstractLocalProcessFunction {

    private final double maxD;
    private final long minT;


    public LocalProcessFunctionBase(String path, double maxD, long minT) {
        super(path);
        this.maxD = maxD;
        this.minT = minT;
    }

    @Override
    public void process(PointList pointList, GpsPoint point) throws FactoryException, TransformException {
        pointList.add(point);
        AbstractStayPointSegment stayPointDetectSegment = new StayPointSegment(pointList, maxD, minT);
        stayPointDetectSegment.stayPointDetection();
    }
}
