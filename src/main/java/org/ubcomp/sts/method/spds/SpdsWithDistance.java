package org.ubcomp.sts.method.spds;

import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;
import org.ubcomp.sts.util.FindTimeT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author syy
 */
public class SpdsWithDistance implements SpdsAlgorithmMergeDistance, Serializable {

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
                } else {
                    distance = CalculateDistance.calculateDistance(pointList.getPointList().get(pointList.getSize() - 1),
                            pointList.getPointList().get(i));
                }
                if (distance >= maxD) {
                    // System.out.println("驻留点1："+ tempPointList.getPointList().subList(0,tempPointList.getStayPointFlag));
                    pointList.pointList = new ArrayList<>(pointList.getPointList()
                            .subList(pointList.getStayPointFlag, pointList.getSize()));
                    pointList.hasStayPoint = false;
                    pointList.getStayPointFlag = -1;
                    canMerge = false;
                    break;
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
            } else {
                distance = CalculateDistance.calculateDistance(pointList.getPointList().get(pointList.getSize() - 1),
                        pointList.getPointList().get(i));
            }
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
        }
    }
}
