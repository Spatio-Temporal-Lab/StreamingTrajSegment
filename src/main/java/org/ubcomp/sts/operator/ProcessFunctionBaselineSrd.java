package org.ubcomp.sts.operator;

import org.ubcomp.sts.method.srd.Srd;
import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.Interpolator;

import java.text.ParseException;

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
    public long process(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, Container container, long runtime) throws ParseException {

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
        Srd.processSrd(pointList, point, container, MIN_R, MIN_DENSITY);
        //long endTime = System.nanoTime();
        // += endTime - startTime;
        return runtime;
    }

    @Override
    public String printResult() {
        return "Srd";
    }
}