package org.ubcomp.sts.method.staypointsegment;

import org.ubcomp.sts.index.AreaEnum;
import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;
import org.ubcomp.sts.util.FindGPSPointsWithInT;


public class StayPointSegmentWithGridOpt extends AbstractStayPointSegment {
    private final Grid grid;
    public StayPointSegmentWithGridOpt(PointList pointList, double maxD, long minT, Grid grid1) {
        super(pointList, maxD, minT);
        grid = grid1;
    }

    @Override
    public void processWithoutStayPoints() {
        GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
        for (int i = pointList.getSize() - 2; i >= 0; i--) {
            double distance;
            GpsPoint nowPoint = pointList.getPointList().get(i);
            AreaEnum area = grid.getArea(nowPoint, latestGPSPoint);
            if (area == AreaEnum.CHECK_AREA) {
                distance = CalculateDistance.calculateDistance(latestGPSPoint, nowPoint);
                if (distance > maxD) {
                    long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                    if (timeInterval > minT) {
                        exactStayPoint(pointList, i);
                    }
                    break;
                }
            } else if (area == AreaEnum.PRUNED_AREA) {
                long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                if (timeInterval > minT) {
                    exactStayPoint(pointList, i);
                }
                break;
            }
        }
    }

    @Override
    public void processWithStayPoints() {
        int inIndex = FindGPSPointsWithInT.findIndex(pointList.pointList, minT);
        if (inIndex <= pointList.stayPointEndLocalIndex) {

            boolean canMerge = true;
            GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
            for (int i = pointList.getSize() - 2; i >= inIndex; i--) {
                double distance;

                GpsPoint nowPoint = pointList.getPointList().get(i);
                AreaEnum area = grid.getArea(nowPoint, latestGPSPoint);
                if (area == AreaEnum.PRUNED_AREA) {
                    breakStayPoint(pointList);
                    canMerge = false;
                    break;
                } else if (AreaEnum.CHECK_AREA == area) {
                    distance = CalculateDistance.calculateDistance(latestGPSPoint, nowPoint);
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



}
