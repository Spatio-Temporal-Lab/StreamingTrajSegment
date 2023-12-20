package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;

import java.text.ParseException;

public class MapToGPSPoint {
    public static GpsPoint mapFunction(String line) throws ParseException {
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

    public static GpsPoint mapFunctionAcc(String line) throws ParseException {
        String[] result = line.split(",");
        String lng = result[0];
        String lat = result[1];
        String tid = result[2];
        long time = Long.parseLong(result[3]);
        Boolean isStayPoint = Boolean.parseBoolean(result[4]);

        return new GpsPoint(Double.parseDouble(lng),
                Double.parseDouble(lat),
                tid,
                time,
                0,
                isStayPoint);
    }
    public static GpsPoint mapFunction2(String line) throws ParseException {
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
