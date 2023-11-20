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
    long totalDelay = 0;

    public AbstractLocalProcessFunction(String path) {
        this.filePath = path;
    }

    public long processElement() throws IOException, ParseException {

        for (int i = 1; i <= 500; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath + i + ".txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                GpsPoint gpsPoint = mapFunction(line);
                long s1 = System.nanoTime();

                PointList pointList = arrayListMap.computeIfAbsent(gpsPoint.tid, k -> new PointList());

                long delay = process(pointList, gpsPoint);

                countPoint++;
                arrayListMap.put(gpsPoint.tid, pointList);

                long s2 = System.nanoTime();
                totalDelay += s2 -s1;

            }
        }
        return totalDelay;
    }

    public abstract long process(PointList pointList, GpsPoint point) throws ParseException, IOException;

    public GpsPoint mapFunction(String line) throws ParseException {
        String[] result = line.replace("'", "")
                .replace("[", "").replace("]", "")
                .replace(" ", "").split(",");
        String t1 = result[0];
        String t2 = result[1];
        String lng = result[4];
        String lat = result[5];
        String tid = result[3];

        String time = t1.substring(0, 4) + "-" + t1.substring(4, 6) + "-" + t1.substring(6, 8) + " " + t2.substring(0, 2) + ":" + t2.substring(2, 4) + ":" + t2.substring(4, 6);
        GpsPoint point = new GpsPoint(Double.parseDouble(lng),
                Double.parseDouble(lat),
                tid,
                time,
                0);
        long time1 = System.currentTimeMillis();
        //point.processTime = Long.parseLong(result[6]);
        //point.processTime = time1;
        return point;
    }


    public GpsPoint mapFunction2(String line) throws ParseException {
        String[] result = line.split(",");
        String t1 = result[2];
        String t2 = result[3];
        String lng = result[1];
        String lat = result[0];
        String tid = result[4];

        String time = t1 +" "+t2;
        GpsPoint point = new GpsPoint(Double.parseDouble(lng),
                Double.parseDouble(lat),
                tid,
                time,
                0);
        long time1 = System.currentTimeMillis();
        //point.processTime = Long.parseLong(result[6]);
        //point.processTime = time1;
        return point;
    }








}
