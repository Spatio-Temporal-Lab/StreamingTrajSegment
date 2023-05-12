package org.ubcomp.sts.method.spds;

import org.ubcomp.sts.index.Gird;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;
import org.ubcomp.sts.util.FindTimeT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author syy
 */
public class SpdsWithDistanceGird implements SpdsAlgorithmMergeDistance, Serializable {

    private static final int GREATER_D = 4;
    private static final int VALIDATE_D_1 = 2;
    private static final int VALIDATE_D_2 = 3;

    @Override
    public void hasStayPoints(PointList pointList, List<Double> distances, double maxD, long minT) {
        int inTimeT = FindTimeT.findT(pointList.pointList, minT);
        if (inTimeT <= pointList.getStayPointFlag) {
            boolean canMerge = true;
            int distanceSize = distances.size() - 1;
            for (int i = pointList.getSize() - 2; i >= inTimeT; i--) {
                double distance;
                if (distanceSize >= 0) {
                    distance = distances.get(distanceSize);
                    distanceSize--;
                    if (distance >= maxD) {
                        //System.out.println("驻留点1："+ temp_point_list.getPointList().subList(0,temp_point_list.getStayPointFlag));
                        pointList.pointList = new ArrayList<>(pointList.getPointList()
                                .subList(pointList.getStayPointFlag, pointList.getSize()));
                        pointList.hasStayPoint = false;
                        pointList.getStayPointFlag = -1;
                        canMerge = false;
                        break;
                    }
                } else {
                    int diffId = Gird.inArea(pointList.getPointList().get(i),
                            pointList.getPointList().get(pointList.getSize() - 1));
                    if (diffId >= GREATER_D) {
                        //System.out.println("驻留点："+ temp_point_list.getPointList().subList(0,temp_point_list.getStayPointFlag));
                        pointList.pointList = new ArrayList<>(pointList.getPointList()
                                .subList(pointList.getStayPointFlag, pointList.getSize()));
                        pointList.hasStayPoint = false;
                        pointList.getStayPointFlag = -1;
                        canMerge = false;
                        break;
                    } else if (diffId == VALIDATE_D_1 || diffId == VALIDATE_D_2) {
                        distance = CalculateDistance.calculateDistance(pointList.getPointList().get(pointList.getSize() - 1),
                                pointList.getPointList().get(i));
                        if (distance > maxD) {
                            //System.out.println("驻留点：" + temp_point_list.getPointList().subList(0, temp_point_list.getStayPointFlag));
                            pointList.pointList = new ArrayList<>(pointList.getPointList()
                                    .subList(pointList.getStayPointFlag, pointList.getSize()));
                            pointList.hasStayPoint = false;
                            pointList.getStayPointFlag = -1;
                            canMerge = false;
                            break;
                        }
                    }
                }
            }
            if (canMerge) {
                int mergeListSize = pointList.getSize() - pointList.getStayPointFlag;
                pointList.stayPointEnd = pointList.stayPointEnd + mergeListSize;
                pointList.getStayPointFlag = pointList.getSize();
            }
        } else {
            //System.out.println("驻留点2："+ tempPointList.getPointList().subList(0,tempPointList.getStayPointFlag));
            pointList.pointList = new ArrayList<>(pointList.getPointList()
                    .subList(pointList.getStayPointFlag, pointList.getSize()));
            pointList.hasStayPoint = false;
            pointList.getStayPointFlag = -1;
        }
    }

    @Override
    public void hasNotStayPoints(PointList pointList, List<Double> distances, double maxD, long minT) {
        int distanceSize = distances.size() - 1;
        for (int i = pointList.getSize() - 2; i >= 0; i--) {
            double distance;
            if (distanceSize >= 0) {
                distance = distances.get(distanceSize);
                distanceSize--;
                if (distance > maxD) {
                    long timeInterval = pointList.getPointList().get(pointList.getSize() - 1).ingestionTime -
                            pointList.getPointList().get(i + 1).ingestionTime;
                    if (timeInterval > minT) {
                        pointList.stayPointStart += i + 1 + 1;
                        pointList.hasStayPoint = true;
                        pointList.pointList = new ArrayList<>(
                                pointList.getPointList().subList(i + 1, pointList.getSize()));
                        pointList.stayPointEnd = pointList.stayPointStart + pointList.getSize() - 1;
                        pointList.getStayPointFlag = pointList.getSize();
                    }
                    break;
                }

            } else {
                int diffId = Gird.inArea(pointList.getPointList().get(i),
                        pointList.getPointList().get(pointList.getSize() - 1));
                if (diffId == VALIDATE_D_1 || diffId == VALIDATE_D_2) {
                    distance = CalculateDistance.calculateDistance(pointList.getPointList().get(pointList.getSize() - 1),
                            pointList.getPointList().get(i));
                    if (distance > maxD) {
                        long timeInterval = pointList.getPointList().get(pointList.getSize() - 1).ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                        if (timeInterval > minT) {
                            pointList.stayPointStart += i + 1 + 1;
                            pointList.hasStayPoint = true;
                            pointList.pointList = new ArrayList<>(
                                    pointList.getPointList().subList(i + 1, pointList.getSize()));
                            pointList.stayPointEnd = pointList.stayPointStart + pointList.getSize() - 1;
                            pointList.getStayPointFlag = pointList.getSize();
                        }
                        break;
                    }
                } else if (diffId >= GREATER_D) {
                    long timeInterval = pointList.getPointList().get(pointList.getSize() - 1).ingestionTime - pointList.getPointList().get(i + 1).ingestionTime;
                    if (timeInterval > minT) {
                        pointList.stayPointStart += i + 1 + 1;
                        pointList.hasStayPoint = true;
                        pointList.pointList = new ArrayList<>(
                                pointList.getPointList().subList(i + 1, pointList.getSize()));
                        pointList.stayPointEnd = pointList.stayPointStart + pointList.getSize() - 1;
                        pointList.getStayPointFlag = pointList.getSize();
                    }
                    break;
                }
            }
        }
    }
}
