package org.ubcomp.sts.operator;


import org.ubcomp.sts.method.streamlof.StreamLof;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.PointList;

import java.text.ParseException;
import java.util.ArrayList;

import static org.ubcomp.sts.method.sws.Sws.processSws;

/**
 * @author syy
 */
public class ProcessFunctionBaselineSws extends AbstractProcessFunction {

    private static final int W = 14;
    private static final int ERROR = 7;

    public ProcessFunctionBaselineSws() {
        super();
    }


    @Override
    public long process(PointList pointList, GpsPoint point, StreamLof lof, Container container, long runtime) throws ParseException {

        pointList.add(point);
        double score = lof.update(point);
        if (score > 30 && score < 10000) {
        }
        long startTime = System.nanoTime();
        if (pointList.getSize() > W) {
            double error = processSws(pointList.getPointList().subList(pointList.getSize() - W, pointList.getSize()));
            if (error > ERROR) {
                pointList.pointList = new ArrayList<>();
            }
        }
        long endTime = System.nanoTime();
        runtime += endTime - startTime;
        return runtime;
    }

    @Override
    public String printResult() {
        return "Sws";
    }


}