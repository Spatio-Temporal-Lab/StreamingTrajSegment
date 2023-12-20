package org.ubcomp.sts.calf1score;

import org.ubcomp.sts.executor.AbstractLocalProcessFunctionForF1Score;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class SPDOffline {
    public Map<String, PointList> arrayListMap = new HashMap<>();

    public void process(String pathIn, String pathOut, String dir, double maxD, long minT) throws IOException, ParseException {
        Map<String, PointList> arrayListMap;
        arrayListMap = read(pathIn);
        Set<String> keySet = arrayListMap.keySet();
        for (String key : keySet) {
            PointList list = arrayListMap.get(key);
            spd(list, maxD, minT);
        }
        save(arrayListMap, pathOut, dir);
    }

    public void spd(PointList pointList, double maxD, long minT) {

        List<GpsPoint> gpsPointList = pointList.getPointList();
        List<List<GpsPoint>> gpsPointLists = new ArrayList<>();

        for (int i = 0; i<=gpsPointList.size()-2;i++){
            if (gpsPointList.get(i+1).ingestionTime - gpsPointList.get(i).ingestionTime >= 300000){
                gpsPointLists.add(new ArrayList<>(gpsPointList.subList(0,i+1)));
                gpsPointList = new ArrayList<>(gpsPointList.subList(i+1,gpsPointList.size()));
                i=0;
            }
        }
        gpsPointLists.add(gpsPointList);
        pointList.pointList = new ArrayList<>();
        for (List<GpsPoint> ls :gpsPointLists){
            for (int i = 0; i < ls.size() - 2; i++) {
                for (int j = i + 2; j <= ls.size() - 1; j++) {
                    if (CalculateDistance.calDistance(ls.get(i),ls.get(j)) > maxD) {
                        long diffTime = ls.get(j - 1).ingestionTime - ls.get(i).ingestionTime;
                        if (CalculateDistance.calDistance(ls.get(j), ls.get(j-1)) > 150 ){
                            break;
                        }
                        if (diffTime > minT) {
                            //find stay point
                            for (int k = i; k <= j - 1; k++) {
                                ls.get(k).isStayPoint = true;
                            }
                            i = j;
                        }
                        break;
                    }
                    if (j == ls.size() - 1) {
                        long diffTime = ls.get(j - 1).ingestionTime - ls.get(i).ingestionTime;
                        if (CalculateDistance.calDistance(ls.get(j), ls.get(j-1)) > 150 ){
                            break;
                        }
                        if (diffTime > minT) {
                            //find stay point
                            for (int k = i; k <= j; k++) {
                                ls.get(k).isStayPoint = true;
                            }
                        }
                    }
                }
            }
            pointList.pointList.addAll(ls);
        }
    }

    public Map<String, PointList> read(String filePath) throws IOException, ParseException {
        for (int i = 1; i <= 500; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath + i + ".txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                GpsPoint gpsPoint = mapFunction2(line);
                PointList pointList = arrayListMap.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                pointList.add(gpsPoint);
            }
            reader.close();
        }
        return arrayListMap;
    }

    public void save(Map<String, PointList> arrayListMap, String outPath, String dir) {
        Set<String> keySet = arrayListMap.keySet();
        for (String key : keySet) {
            PointList list = arrayListMap.get(key);
            for (int i = 1; i <= 500; i++) {
                try {
                    File makeDir = new File(dir);
                    makeDir.mkdirs();
                    String filePath = outPath + i + ".txt";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    AbstractLocalProcessFunctionForF1Score.write(list, i, filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

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

    public GpsPoint mapFunctionAcc(String line) throws ParseException {
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


}
