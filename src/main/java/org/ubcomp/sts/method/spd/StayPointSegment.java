package org.ubcomp.sts.method.spd;

import org.ubcomp.sts.method.AbstractStayPointSegment;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;

public class StayPointSegment extends AbstractStayPointSegment {
    public StayPointSegment(PointList pointList, double maxD, long minT) {
        super(pointList, maxD, minT);
    }


    public void stayPointDetection() {
        GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
        for (int i = pointList.getSize() - 2; i >= 0; i--) {
            double distance;
            GpsPoint nowPoint = pointList.getPointList().get(i);
            distance = CalculateDistance.calDistance(latestGPSPoint, nowPoint);
            if (distance > maxD) {
                long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                if (timeInterval > minT) {
                    //find stay point
                    if (pointList.hasStayPoint) {
                        processWithStayPoints(true, i);
                    } else {
                        processWithoutStayPoints(true, i);
                    }
                } else {
                    //can't find stay point
                    if (pointList.hasStayPoint) {
                        processWithStayPoints(false, i);
                    } else {
                        processWithoutStayPoints(false, i);
                    }
                }
                return;
            }
        }

    }


    @Override
    public void processWithoutStayPoints(boolean findOrNot, int index) {
        if (findOrNot) {
            //Case 1.3 Only One Stay Point
            exactStayPoint(pointList, index);
        }
        //Case 2.3 No Stay Point(do nothing)
    }


    @Override
    public void processWithStayPoints(boolean findOrNot, int index) {
        if (findOrNot) {
            if (index <= pointList.stayPointEndLocalIndex) {
                //Case 1.2 Two Stay Points Intersected
                mergeStayPoint(pointList);
            } else {
                //Case 1.1 Two Stay Points Separated
                int sizeOfFirstStayPoint = pointList.stayPointEndLocalIndex;
                breakStayPoint(pointList);
                int startIndexOfSecondStayPoint = index - sizeOfFirstStayPoint;
                if (startIndexOfSecondStayPoint > 0) {
                    exactStayPoint(pointList, startIndexOfSecondStayPoint);
                }

            }
        } else {
            //Case 2.1 Far Away from the Stay Point
            GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
            GpsPoint stayPointEnd = pointList.getPointList().get(pointList.stayPointEndLocalIndex - 1);
            double distance = CalculateDistance.calDistance(latestGPSPoint, stayPointEnd);
            if (distance > maxD) {
                breakStayPoint(pointList);
            }
            //case 2.2(do nothing)
        }
    }
}
