package org.ubcomp.sts.method.staypointsegment;

import org.ubcomp.sts.index.AreaEnum;
import org.ubcomp.sts.index.Grid;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;


public class StayPointSegmentWithGridOptForF1Score extends AbstractStayPointSegmentForF1Score {
    private final Grid grid;

    public StayPointSegmentWithGridOptForF1Score(PointList pointList, double maxD, long minT, Grid grid1, PointList result) {
        super(pointList, maxD, minT, result);
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
            if (i == 0) {
                long timeInterval = latestGPSPoint.ingestionTime - pointList.getPointList().get(0).ingestionTime;
                if (timeInterval > minT) {
                    //找到了驻留点
                    if (pointList.hasStayPoint) {
                        processWithStayPoints(true, i - 1);
                    } else {
                        processWithoutStayPoints(true, i - 1);
                    }
                    return;
                } else {
                    //没找到驻留点
                    if (pointList.hasStayPoint) {
                        processWithStayPoints(false, i - 1);
                    } else {
                        processWithoutStayPoints(false, i - 1);
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
            exactStayPoint(pointList, index, result);
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
                breakStayPoint(pointList, result);
                int startIndexOfSecondStayPoint = index - sizeOfFirstStayPoint;
                if (startIndexOfSecondStayPoint >= 0) {
                    exactStayPoint(pointList, startIndexOfSecondStayPoint, result);
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
                    breakStayPoint(pointList, result);
                }
            } else if (area == AreaEnum.PRUNED_AREA) {
                breakStayPoint(pointList, result);
            }
            //case 2.2(do nothing)
        }
    }

}
