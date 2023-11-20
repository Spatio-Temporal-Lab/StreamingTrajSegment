package org.ubcomp.sts.lcaol;

import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.IOException;
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
    public long process(PointList pointList, GpsPoint point) throws ParseException, IOException {
        long lateTime = 0;
        pointList.add(point);
        /*double score = lof.update(point);
        if (score > 10 && score < 10000) {
            if (pointList.getSize() >= 4) {
                GpsPoint p = Interpolator.interpolatePosition(pointList.pointList.subList(
                        pointList.getSize() - 4, pointList.getSize() - 1), point.ingestionTime);
                pointList.pointList.remove(pointList.getSize() - 1);
                pointList.add(p);
                lof.deletePoint();
                lof.update(p);
            }
        }*/
        if (pointList.getSize() > W) {
            lateTime = (point.ingestionTime - pointList.pointList.get(pointList.getSize() - 4).ingestionTime);
            double error = processSws(pointList.getPointList().subList(pointList.getSize() - W, pointList.getSize()));
            if (error > ERROR) {
                pointList.pointList = new ArrayList<>();
            }
        }
        return lateTime;
    }
}
