package org.ubcomp.sts.local;

import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.text.ParseException;
import java.util.ArrayList;

import static org.ubcomp.sts.method.sws.Sws.processSws;

public class LocalProcessFunctionBaselineSws extends AbstractLocalProcessFunction{

    private static final int W = 15;
    private static final int ERROR = 5000;

    public LocalProcessFunctionBaselineSws(String path) {
        super(path);
    }

    @Override
    public void process(PointList pointList, GpsPoint point) throws ParseException {
        pointList.add(point);
        if (pointList.getSize() > W) {
            double error = processSws(pointList.getPointList().subList(pointList.getSize() - W, pointList.getSize()));
            if (error > ERROR) {
                pointList.pointList = new ArrayList<>();
            }
        }
    }
}
