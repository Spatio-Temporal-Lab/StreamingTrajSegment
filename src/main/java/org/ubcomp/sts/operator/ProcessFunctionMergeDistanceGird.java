package org.ubcomp.sts.operator;

import org.ubcomp.sts.method.spds.SpdsWithDistanceGird;
import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.Interpolator;

import java.text.ParseException;

/**
 * @author syy
 */
public class ProcessFunctionMergeDistanceGird extends AbstractProcessFunction {

    private final SpdsWithDistanceGird spdsWithDistanceGird;
    private final double maxD;
    private final long minT;

    public ProcessFunctionMergeDistanceGird(double d, long t) {
        super();
        spdsWithDistanceGird = new SpdsWithDistanceGird();
        maxD = d;
        minT = t;
    }

    @Override
    public long process(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, Container container, long runtime) throws ParseException {
        if (!pointList.hasStayPoint) {
            //将点加入临时列表中
            pointList.add(point);
            double score = lof.update(point);
            if (score > 30 && score < 10000) {
                if (pointList.getSize() > 4) {
                    GpsPoint p = Interpolator.interpolatePosition(pointList.pointList.subList(
                            pointList.getSize() - 4, pointList.getSize() - 1), point.ingestionTime);
                    pointList.pointList.remove(pointList.getSize() - 1);
                    pointList.add(p);
                    lof.deletePoint();
                    lof.update(p);
                }
            }
            //long startTime = System.nanoTime();
            spdsWithDistanceGird.hasNotStayPoints(pointList, lof.lastPointDistances, maxD, minT);
            //long endTime = System.nanoTime();
            //runtime += endTime - startTime;
        } else {
            pointList.add(point);
            double score = lof.update(point);
            if (score > 30 && score < 10000) {
                if (pointList.getSize() > 4) {
                    GpsPoint p = Interpolator.interpolatePosition(pointList.pointList.subList(
                            pointList.getSize() - 4, pointList.getSize() - 1), point.ingestionTime);
                    pointList.pointList.remove(pointList.getSize() - 1);
                    pointList.add(p);
                    lof.deletePoint();
                    lof.update(p);
                }
            }
            //long startTime = System.nanoTime();
            spdsWithDistanceGird.hasStayPoints(pointList, lof.lastPointDistances, maxD, minT);
            //long endTime = System.nanoTime();
            //runtime += endTime - startTime;
        }
        return runtime;
    }

    @Override
    public String printResult() {
        return "SpdsMergeDistanceGird";
    }
}
