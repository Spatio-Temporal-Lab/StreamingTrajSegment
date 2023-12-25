package org.ubcomp.sts.executor;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.MapToGPSPoint;

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
                GpsPoint gpsPoint = MapToGPSPoint.mapFunction(line);
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


}
