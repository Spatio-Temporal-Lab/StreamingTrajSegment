package org.ubcomp.sts.operator;


import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.Interpolator;

import java.text.ParseException;
import java.util.ArrayList;

import static org.ubcomp.sts.method.sws.Sws.processSws;

/**
 * @author syy
 */
public class ProcessFunctionBaselineSws extends AbstractProcessFunction {

    private static final int W = 7;
    private static final int ERROR = 5000;

    public ProcessFunctionBaselineSws() {
        super();
    }


    @Override
    public long process(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, Container container, long runtime, int countPoints) throws ParseException {
        long latetime = 0;
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
        if (pointList.getSize() > W) {
            latetime = (point.ingestionTime - pointList.pointList.get(pointList.getSize() - 4).ingestionTime)*1000000;
            double error = processSws(pointList.getPointList().subList(pointList.getSize() - W, pointList.getSize()));
            if (error > ERROR) {
                pointList.pointList = new ArrayList<>();
            }
        }
        //long endTime = System.nanoTime();
        //runtime += endTime - startTime;
        return latetime;
    }

    @Override
    public String printResult() {
        return "Sws";
    }


}