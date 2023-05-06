package org.ubcomp.sts.operator;

import org.ubcomp.sts.method.spds.Spds;
import org.ubcomp.sts.method.streamlof.StreamLof;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

/**
 * @author syy
 */
public class ProcessFunction extends AbstractProcessFunction {

    private final Spds spds;
    private final double maxD;
    private final long minT;

    public ProcessFunction(double d, long t) {
        super();
        spds = new Spds();
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
            spds.hasNotStayPoints(pointList, maxD, minT);
            long endTime = System.nanoTime();
            runtime += endTime - startTime;
        } else {
            pointList.add(point);
            double score = lof.update(point);
            if (score > 30 && score < 10000) {

            }
            long startTime = System.nanoTime();
            spds.hasStayPoints(pointList, maxD, minT);
            long endTime = System.nanoTime();
            runtime += endTime - startTime;
        }
        return runtime;
    }

    @Override
    public String printResult() {
        return "Spds";
    }
}
