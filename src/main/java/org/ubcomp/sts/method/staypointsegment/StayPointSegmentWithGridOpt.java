package org.ubcomp.sts.method.staypointsegment;

import org.ubcomp.sts.index.AreaEnum;
import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;


public class StayPointSegmentWithGridOpt extends AbstractStayPointSegment {
    private final Grid grid;

    public StayPointSegmentWithGridOpt(PointList pointList, double maxD, long minT, Grid grid1) {
        super(pointList, maxD, minT);
        grid = grid1;
    }

    @Override
    public void stayPointDetection() {
        GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
        for (int i = pointList.getSize() - 2; i >= 0; i--) {
            double distance;
            GpsPoint nowPoint = pointList.getPointList().get(i);
            AreaEnum area = grid.getArea(nowPoint, latestGPSPoint);
            if (area == AreaEnum.CHECK_AREA) {
                distance = CalculateDistance.calDistance(latestGPSPoint, nowPoint);
                if (distance > maxD) {
                    long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                    if (timeInterval > minT) {
                        //找到了驻留点
                        if (pointList.hasStayPoint) {
                            processWithStayPoints(true, i);
                        } else {
                            processWithoutStayPoints(true, i);
                        }
                    } else {
                        //没找到驻留点
                        if (pointList.hasStayPoint) {
                            processWithStayPoints(false, i);
                        } else {
                            processWithoutStayPoints(false, i);
                        }
                    }
                    return;
                }
            } else if (area == AreaEnum.PRUNED_AREA) {
                long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                if (timeInterval > minT) {
                    //找到了驻留点
                    if (pointList.hasStayPoint) {
                        processWithStayPoints(true, i);
                    } else {
                        processWithoutStayPoints(true, i);
                    }
                    return;
                } else {
                    //没找到驻留点
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
                if (startIndexOfSecondStayPoint >= 0) {
                    exactStayPoint(pointList, startIndexOfSecondStayPoint);
                }

            }
        } else {
            //Case 2.1 Far Away from the Stay Point
            GpsPoint latestGPSPoint = pointList.getPointList().get(pointList.getSize() - 1);
            GpsPoint stayPointEnd = pointList.getPointList().get(pointList.stayPointEndLocalIndex - 1);
            AreaEnum area = grid.getArea(stayPointEnd, latestGPSPoint);
            if (area == AreaEnum.CHECK_AREA) {
                double distance = CalculateDistance.calDistance(latestGPSPoint, stayPointEnd);
                if (distance > maxD) {
                    breakStayPoint(pointList);
                }
            } else if (area == AreaEnum.PRUNED_AREA) {
                breakStayPoint(pointList);
            }
            //case 2.2(do nothing)
        }
    }

}
