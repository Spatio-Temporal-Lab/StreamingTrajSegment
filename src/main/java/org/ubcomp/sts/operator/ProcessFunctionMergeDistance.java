package org.ubcomp.sts.operator;

import org.ubcomp.sts.method.spds.SpdsWithDistance;
import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.Interpolator;

import java.text.ParseException;

/**
 * @author syy
 */
public class ProcessFunctionMergeDistance extends AbstractProcessFunction {

    private final SpdsWithDistance spdsWithDistance;
    private final double maxD;
    private final long minT;

    public ProcessFunctionMergeDistance(double d, long t) {
        super();
        spdsWithDistance = new SpdsWithDistance();
        maxD = d;
        minT = t;
    }

    @Override
    public long process(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, Container container, long runtime, int countPoints) throws ParseException {
        if (!pointList.hasStayPoint) {
            //将点加入临时列表中
            pointList.add(point);
            double score = lof.update(point);
            if (score > 10 && score < 10000) {
                if (pointList.getSize() >= 4) {
                    GpsPoint p = Interpolator.interpolatePosition(pointList.pointList.subList(
                            pointList.getSize() - 4, pointList.getSize() - 1), point.ingestionTime);
                    pointList.pointList.remove(pointList.getSize() - 1);
                    pointList.add(p);
                    lof.deletePoint();
                    lof.update(p);
                }
            }
            //long startTime = System.nanoTime();
            spdsWithDistance.hasNotStayPoints(pointList, lof.lastPointDistances, maxD, minT);
            //long endTime = System.nanoTime();
            //runtime += endTime - startTime;
        } else {
            pointList.add(point);
            double score = lof.update(point);
            if (score > 10 && score < 10000) {
                if (pointList.getSize() >= 4) {
                    GpsPoint p = Interpolator.interpolatePosition(pointList.pointList.subList(
                            pointList.getSize() - 4, pointList.getSize() - 1), point.ingestionTime);
                    pointList.pointList.remove(pointList.getSize() - 1);
                    pointList.add(p);
                    lof.deletePoint();
                    lof.update(p);
                }
            }
            //long startTime = System.nanoTime();
            spdsWithDistance.hasStayPoints(pointList, lof.lastPointDistances, maxD, minT);
            //long endTime = System.nanoTime();
            //runtime += endTime - startTime;
        }
        return 0;
    }

    @Override
    public String printResult() {
        return "SpdsMergeDistance";
    }
}
