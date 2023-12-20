package org.ubcomp.sts.method.staypointsegment;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.PointList;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class AbstractStayPointSegment implements Serializable {
    protected PointList pointList;
    protected double maxD;
    protected long minT;

    public AbstractStayPointSegment(PointList pointList, double maxD, long minT) {
        this.pointList = pointList;
        this.maxD = maxD;
        this.minT = minT;
    }

    public abstract void processWithStayPoints(boolean findOrNot, int index) throws FactoryException, TransformException;

    public abstract void processWithoutStayPoints(boolean findOrNot, int index);


    public abstract void stayPointDetection() throws FactoryException, TransformException;

    protected void breakStayPoint(PointList pointList) {
        pointList.pointList = new ArrayList<>(pointList.getPointList()
                .subList(pointList.stayPointEndLocalIndex, pointList.getSize()));
        pointList.hasStayPoint = false;
        pointList.stayPointEndLocalIndex = -1;

    }

    protected void mergeStayPoint(PointList pointList) {
        int mergeListSize = pointList.getSize() - pointList.stayPointEndLocalIndex;
        pointList.stayPointEndGlobalIndex = pointList.stayPointEndGlobalIndex + mergeListSize;
        pointList.stayPointEndLocalIndex = pointList.getSize();
    }

    protected void exactStayPoint(PointList pointList, int currentIndex) {
        pointList.stayPointStartGlobalIndex += currentIndex + 2;
        pointList.hasStayPoint = true;
        pointList.pointList = new ArrayList<>(
                pointList.getPointList().subList(currentIndex + 1, pointList.getSize()));
        pointList.stayPointEndGlobalIndex = pointList.stayPointStartGlobalIndex + pointList.getSize() - 1;
        pointList.stayPointEndLocalIndex = pointList.getSize();
    }
}
