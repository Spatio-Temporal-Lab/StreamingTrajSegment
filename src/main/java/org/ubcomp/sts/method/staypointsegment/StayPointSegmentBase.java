package org.ubcomp.sts.method.staypointsegment;

import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;
import org.ubcomp.sts.util.FindGPSPointsWithInT;

public class StayPointSegmentBase extends AbstractStayPointSegment {
    public StayPointSegmentBase(PointList pointList, double maxD, long minT) {
        super(pointList, maxD, minT);
    }

    @Override
    public void processWithStayPoints() {
        int inIndex = FindGPSPointsWithInT.findIndex(pointList.pointList, minT);
        if (inIndex <= pointList.stayPointEndLocalIndex) {
            boolean canMerge = true;
            GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
            for (int i = pointList.getSize() - 2; i >= inIndex; i--) {
                double distance = CalculateDistance.calculateDistance(latestGPSPoint, pointList.getPointList().get(i));
                if (distance >= maxD) {
                    breakStayPoint(pointList);
                    canMerge = false;
                    break;
                }
            }
            if (canMerge) {
                mergeStayPoint(pointList);
            }
        } else {
            breakStayPoint(pointList);
        }
    }

    @Override
    public void processWithoutStayPoints() {
        for (int i = pointList.getSize() - 2; i >= 0; i--) {
            GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
            double distance = CalculateDistance.calculateDistance(latestGPSPoint, pointList.getPointList().get(i));
            if (distance > maxD) {
                long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                if (timeInterval > minT) {
                    exactStayPoint(pointList, i);
                }
                break;
            }
        }
    }
}
