package org.ubcomp.sts.method.sws;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.util.CalculateDistance;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

/**
 * @author syy
 */
public class Sws implements Serializable {
    public static double processSws(List<GpsPoint> pointList) throws ParseException, FactoryException, TransformException {

        int mid = pointList.size() / 2 + 1;
        List<GpsPoint> pointList1 = pointList.subList(0, mid);
        List<GpsPoint> pointList2 = pointList.subList(mid, pointList.size());
        GpsPoint p1 = baseSws(pointList1);
        GpsPoint p2 = baseSws(pointList2);

        GpsPoint p0 = new GpsPoint((p1.lng + p2.lng) / 2, (p1.lat + p2.lat) / 2, p1.tid, pointList.get(mid).ingestionTime, 0);

        return CalculateDistance.calDistance(p0, pointList.get(mid));
    }

    static GpsPoint baseSws(List<GpsPoint> pointList) throws ParseException {
        int mid = pointList.size() / 2 + 1;
        List<GpsPoint> pointListF = pointList.subList(0, mid);
        List<GpsPoint> pointListB = pointList.subList(mid, pointList.size());
        long tf = pointListF.get(0).ingestionTime;
        long tb = pointListB.get(pointListB.size() - 1).ingestionTime;

        SimpleRegression regression1Y = new SimpleRegression();
        for (GpsPoint point : pointListF) {
            regression1Y.addData(point.ingestionTime - tf, point.lng);
        }
        double predictForwardY = regression1Y.predict(pointList.get(mid).ingestionTime - tf);
        SimpleRegression regression1X = new SimpleRegression();
        for (GpsPoint point : pointListF) {
            regression1X.addData(point.ingestionTime - tf, point.lat);
        }
        double predictForwardX = regression1X.predict(pointList.get(mid).ingestionTime - tf);
        GpsPoint p1 = new GpsPoint(predictForwardY, predictForwardX, pointList.get(mid).tid, pointList.get(mid).ingestionTime, 0);

        SimpleRegression regression2Y = new SimpleRegression();
        for (GpsPoint point : pointListB) {
            regression2Y.addData(point.ingestionTime - tb, point.lng);
        }
        double predictBackY = regression2Y.predict(pointList.get(mid).ingestionTime - tb);
        SimpleRegression regression2X = new SimpleRegression();
        for (GpsPoint point : pointListB) {
            regression2X.addData(point.ingestionTime - tb, point.lat);
        }
        double predictBackX = regression2X.predict(pointList.get(mid).ingestionTime - tb);
        GpsPoint p2 = new GpsPoint(predictBackY, predictBackX, pointList.get(mid).tid, pointList.get(mid).ingestionTime, 0);

        return new GpsPoint((p1.lng + p2.lng) / 2, (p1.lat + p2.lat) / 2, p1.tid, p1.ingestionTime, 0);

    }

}
