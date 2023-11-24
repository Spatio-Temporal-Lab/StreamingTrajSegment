package org.ubcomp.sts.local;

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
    long countPoint = 0;

    public AbstractLocalProcessFunction(String path) {
        this.filePath = path;
    }

    public long processElement() throws IOException, ParseException {

        long totalDelay = 0;

        for (int i = 1; i <= 500; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath + i + ".txt"));
            int a = 1;
            String line;
            while ((line = reader.readLine()) != null) {
           /* while (a == 1) {//(line = reader.readLine()) != null
                a += 1;
                while (a != 6) {
                    line = reader.readLine();
                    a += 1;
                }*/
                GpsPoint gpsPoint = mapFunction(line);
                PointList pointList = arrayListMap.computeIfAbsent(gpsPoint.tid, k -> new PointList());

                long s1 = System.nanoTime();
                process(pointList, gpsPoint);
                long s2 = System.nanoTime();

                countPoint++;

                totalDelay += s2 - s1;
            }
        }
        return totalDelay;
    }

    public abstract void process(PointList pointList, GpsPoint point) throws ParseException, IOException;

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


    public GpsPoint mapFunction2(String line) throws ParseException {
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
    }


}
