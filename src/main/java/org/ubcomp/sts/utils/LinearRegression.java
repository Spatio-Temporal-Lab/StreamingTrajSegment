package org.ubcomp.sts.utils;

import org.ubcomp.sts.objects.gpsPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

public class LinearRegression implements Serializable {
    public double slope_x;
    public double intercept_x;
    public double slope_y;
    public double intercept_y;

    public LinearRegression(){}

    public void fit_x(List<gpsPoint> points) {
        int n = points.size();
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumXX = 0.0;

        for (int i = 0; i < n; i++) {
            double x = points.get(i).ingestionTime;
            double y = points.get(i).lng;

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double xMean = sumX / n;
        double yMean = sumY / n;
        double slope = (sumXY - sumX * yMean) / (sumXX - sumX * xMean);
        System.out.println( (sumXX - sumX * xMean));
        double intercept = yMean - slope * xMean;

        this.slope_x = slope;
        this.intercept_x = intercept;
    }
    public void fit_y(List<gpsPoint> points) {
        int n = points.size();
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumXX = 0.0;

        for (int i = 0; i < n; i++) {
            double x = points.get(i).ingestionTime;
            double y = points.get(i).lat;

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double xMean = sumX / n;
        double yMean = sumY / n;
        double slope = (sumXY - sumX * yMean) / (sumXX - sumX * xMean);
        double intercept = yMean - slope * xMean;

        this.slope_y = slope;
        this.intercept_y = intercept;
    }

    public gpsPoint predict(gpsPoint p) throws ParseException {
        long t = p.ingestionTime;
        return new gpsPoint(slope_x * t + intercept_x,slope_y * t + intercept_y,p.tid, p.ingestionTime,0);
    }



}
