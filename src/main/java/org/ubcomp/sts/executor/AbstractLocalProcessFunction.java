package org.ubcomp.sts.executor;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLocalProcessFunction {

    protected Map<String, PointList> arrayListMap = new HashMap<>();
    protected String filePath;
    public long countPoint = 0;

    public AbstractLocalProcessFunction(String path) {
        this.filePath = path;
    }

    public double[] processElement() throws IOException, ParseException, FactoryException, TransformException {

        double delay = 0;
        double totalDelay = 0;

        for (int i = 1; i <= 500; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath + i + ".txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                GpsPoint gpsPoint = mapFunction(line);
                PointList pointList = arrayListMap.computeIfAbsent(gpsPoint.tid, k -> new PointList());

                long s1 = System.nanoTime();
                process(pointList, gpsPoint);
                long s2 = System.nanoTime();

                countPoint++;
                double timeDiff = (s2 - s1) / 1000000.0;
                delay += timeDiff;
                totalDelay = totalDelay + delay;
            }
        }
        return new double[]{totalDelay, delay};
    }

    public abstract void process(PointList pointList, GpsPoint point) throws ParseException, IOException, FactoryException, TransformException;

    //you can add your mapFunction here
    private GpsPoint mapFunction(String line) throws ParseException {
        String[] result = line.replace("'", "")
                .replace("[", "").replace("]", "")
                .replace(" ", "").split(",");
        String t1 = result[0];
        String t2 = result[1];
        String lng = result[4];
        String lat = result[5];
        String tid = result[3];

        String time = t1.substring(0, 4) + "-" + t1.substring(4, 6) + "-" + t1.substring(6, 8) + " " + t2.substring(0, 2) + ":" + t2.substring(2, 4) + ":" + t2.substring(4, 6);
        return new GpsPoint(Double.parseDouble(lng),
                Double.parseDouble(lat),
                tid,
                time,
                0);
    }


    /*public GpsPoint mapFunction2(String line) throws ParseException {
        String[] result = line.split(",");
        String t1 = result[2];
        String t2 = result[3];
        String lng = result[1];
        String lat = result[0];
        String tid = result[4];

        String time = t1 + " " + t2;
        return new GpsPoint(Double.parseDouble(lng),
            Double.parseDouble(lat),
            tid,
            time,
            0);
    }*/
}