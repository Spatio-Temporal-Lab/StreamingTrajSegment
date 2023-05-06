package org.ubcomp.sts.operator;

import org.ubcomp.sts.method.srd.Srd;
import org.ubcomp.sts.method.streamlof.StreamLof;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.PointList;

/**
 * @author paopaotang
 */
public class ProcessFunctionBaselineSrd extends AbstractProcessFunction {

    private static final double MIN_R = 10000;
    private static final double MIN_DENSITY = 0.5;

    public ProcessFunctionBaselineSrd() {
        super();
    }

    @Override
    public long process(PointList pointList, GpsPoint point, StreamLof lof, Container container, long runtime) {

        pointList.add(point);
        double score = lof.update(point);
        if (score > 30 && score < 10000) {
        }
        long startTime = System.nanoTime();
        Srd.processSrd(pointList, point, container, MIN_R, MIN_DENSITY);
        long endTime = System.nanoTime();
        runtime += endTime - startTime;
        return runtime;
    }

    @Override
    public String printResult() {
        return "Srd";
    }
}