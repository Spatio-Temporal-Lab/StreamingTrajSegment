package org.ubcomp.sts.operator;

import org.ubcomp.sts.method.spds.SpdsWithDistanceGird;
import org.ubcomp.sts.method.streamlof.StreamLof;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

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
    public long process(PointList pointList, GpsPoint point, StreamLof lof, Container container, long runtime) {
        if (!pointList.hasStayPoint) {
            //将点加入临时列表中
            pointList.add(point);
            double score = lof.update(point);
            if (score > 30 && score < 10000) {
            }
            long startTime = System.nanoTime();
            spdsWithDistanceGird.hasNotStayPoints(pointList, lof.lastPointDistances, maxD, minT);
            long endTime = System.nanoTime();
        } else {
            pointList.add(point);
            double score = lof.update(point);
            if (score > 30 && score < 10000) {
            }
            long startTime = System.nanoTime();
            spdsWithDistanceGird.hasStayPoints(pointList, lof.lastPointDistances, maxD, minT);
            long endTime = System.nanoTime();
        }
        return runtime;
    }

    @Override
    public String printResult() {
        return "SpdsMergeDistanceGird";
    }
}
