package org.ubcomp.sts.method.staypointsegment;

import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;
import org.ubcomp.sts.util.FindGPSPointsWithInT;

import java.util.List;

public class StayPointSegmentWithDistanceGridOpt extends AbstractStayPointSegment {
    private final List<Double> distances;

    private static final int GREATER_D = 4;
    private static final int VALIDATE_D_1 = 2;
    private static final int VALIDATE_D_2 = 3;

    public StayPointSegmentWithDistanceGridOpt(PointList pointList, List<Double> distances, double maxD, long minT) {
        super(pointList, maxD, minT);
        this.distances = distances;
    }


    @Override
    public void processWithStayPoints() {

        int inIndex = FindGPSPointsWithInT.findIndex(pointList.pointList, minT);
        // current window has a stay point
        if (inIndex <= pointList.stayPointEndLocalIndex) {
            boolean canMerge = true;
            int distanceSize = distances.size() - 1;
            GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
            for (int i = pointList.getSize() - 2; i >= inIndex; i--) {
                double distance;
                if (distanceSize >= 0) {
                    distance = distances.get(distanceSize);
                    distanceSize--;
                    if (distance >= maxD) {
                        breakStayPoint(pointList);
                        canMerge = false;
                        break;
                    }
                } else {
                    int diffId = Grid.gridManhattan(pointList.getPointList().get(i), latestGPSPoint);
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
        int distanceSize = distances.size() - 1;
        GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
        for (int i = pointList.getSize() - 2; i >= 0; i--) {
            double distance;
            if (distanceSize >= 0) {
                distance = distances.get(distanceSize);
                distanceSize--;
                if (distance > maxD) {
                    long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                    if (timeInterval > minT) {
                        exactStayPoint(pointList, i);
                    }
                    break;
                }
            } else {
                int diffId = Grid.gridManhattan(pointList.getPointList().get(i), latestGPSPoint);
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
}
