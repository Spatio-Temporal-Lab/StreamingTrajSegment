package org.ubcomp.sts.method;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStayPointSegmentForF1Score implements Serializable {
    protected PointList pointList;
    protected PointList result;
    protected double maxD;
    protected long minT;

    public AbstractStayPointSegmentForF1Score(PointList pointList, double maxD, long minT, PointList reuslt) {
        this.pointList = pointList;
        this.maxD = maxD;
        this.minT = minT;
        this.result = reuslt;
    }

    public abstract void processWithStayPoints(boolean findOrNot, int index) throws FactoryException, TransformException;

    public abstract void processWithoutStayPoints(boolean findOrNot, int index);


    public abstract void stayPointDetection() throws FactoryException, TransformException;

    protected void breakStayPoint(PointList pointList, PointList reuslt) {
        List<GpsPoint> list = pointList.getPointList().subList(0, pointList.stayPointEndLocalIndex);
        reuslt.getPointList().addAll(list);
        //System.out.println(list);
        pointList.pointList = new ArrayList<>(pointList.getPointList()
                .subList(pointList.stayPointEndLocalIndex, pointList.getSize()));
        pointList.hasStayPoint = false;
        pointList.stayPointEndLocalIndex = -1;

    }

    protected void mergeStayPoint(PointList pointList) {
        for (GpsPoint p : pointList.getPointList()) {
            p.isStayPoint = true;
        }
        int mergeListSize = pointList.getSize() - pointList.stayPointEndLocalIndex;
        pointList.stayPointEndGlobalIndex = pointList.stayPointEndGlobalIndex + mergeListSize;
        pointList.stayPointEndLocalIndex = pointList.getSize();
    }

    protected void exactStayPoint(PointList pointList, int currentIndex, PointList reuslt) {
        List<GpsPoint> list = pointList.getPointList().subList(0, currentIndex + 1);
        reuslt.getPointList().addAll(list);
        pointList.stayPointStartGlobalIndex += currentIndex + 2;
        pointList.hasStayPoint = true;
        pointList.pointList = new ArrayList<>(
                pointList.getPointList().subList(currentIndex + 1, pointList.getSize()));
        for (GpsPoint p : pointList.getPointList()) {
            p.isStayPoint = true;
        }
        pointList.stayPointEndGlobalIndex = pointList.stayPointStartGlobalIndex + pointList.getSize() - 1;
        pointList.stayPointEndLocalIndex = pointList.getSize();
    }
}
