package org.ubcomp.sts.method.staypointsegment;

import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;
import org.ubcomp.sts.util.FindGPSPointsWithInT;


public class StayPointSegmentWithGridOpt extends AbstractStayPointSegment {

    private static final int GREATER_D = 4;
    private static final int VALIDATE_D_1 = 2;
    private static final int VALIDATE_D_2 = 3;

    private final Grid grid;

    public StayPointSegmentWithGridOpt(PointList pointList, double maxD, long minT, Grid grid1) {
        super(pointList, maxD, minT);
        grid = grid1;
    }


    @Override
    public void processWithStayPoints() {
        int inIndex = FindGPSPointsWithInT.findIndex(pointList.pointList, minT);
        if (inIndex <= pointList.stayPointEndLocalIndex) {

            boolean canMerge = true;
            GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
            for (int i = pointList.getSize() - 2; i >= inIndex; i--) {
                double distance;

                int diffId = grid.gridManhattan(pointList.getPointList().get(i), latestGPSPoint);
                if (diffId >= GREATER_D) {
                    breakStayPoint(pointList);
                    canMerge = false;
                    break;
                } else if (diffId == VALIDATE_D_1 || diffId == VALIDATE_D_2) {
                    distance = CalculateDistance.calculateDistance(latestGPSPoint, pointList.getPointList().get(i));
                    if (distance >= maxD) {
                        breakStayPoint(pointList);
                        canMerge = false;
                        break;
                    }
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
        GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
        for (int i = pointList.getSize() - 2; i >= 0; i--) {
            double distance;
            int diffId = grid.gridManhattan(pointList.getPointList().get(i), latestGPSPoint);
            if (diffId == VALIDATE_D_1 || diffId == VALIDATE_D_2) {
                distance = CalculateDistance.calculateDistance(latestGPSPoint, pointList.getPointList().get(i));
                if (distance > maxD) {
                    long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                    if (timeInterval > minT) {
                        exactStayPoint(pointList, i);
                    }
                    break;
                }
            } else if (diffId >= GREATER_D) {
                long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                if (timeInterval > minT) {
                    exactStayPoint(pointList, i);
                }
                break;
            }
        }
    }
}
