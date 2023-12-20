package org.ubcomp.sts.calf1score;

import org.ubcomp.sts.executor.AbstractLocalProcessFunctionForF1Score;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.CalculateDistance;
import org.ubcomp.sts.util.MapToGPSPoint;
import org.ubcomp.sts.util.WriteToFile;

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

        for (int i = 0; i <= gpsPointList.size() - 2; i++) {
            if (gpsPointList.get(i + 1).ingestionTime - gpsPointList.get(i).ingestionTime >= 300000) {
                gpsPointLists.add(new ArrayList<>(gpsPointList.subList(0, i + 1)));
                gpsPointList = new ArrayList<>(gpsPointList.subList(i + 1, gpsPointList.size()));
                i = 0;
            }
        }
        gpsPointLists.add(gpsPointList);
        pointList.pointList = new ArrayList<>();
        for (List<GpsPoint> ls : gpsPointLists) {
            for (int i = 0; i < ls.size() - 2; i++) {
                for (int j = i + 2; j <= ls.size() - 1; j++) {
                    if (CalculateDistance.calDistance(ls.get(i), ls.get(j)) > maxD) {
                        long diffTime = ls.get(j - 1).ingestionTime - ls.get(i).ingestionTime;
                        if (CalculateDistance.calDistance(ls.get(j), ls.get(j - 1)) > 150) {
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
                        if (CalculateDistance.calDistance(ls.get(j), ls.get(j - 1)) > 150) {
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
                GpsPoint gpsPoint = MapToGPSPoint.mapFunction(line);
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
                    boolean success;
                    File makeDir = new File(dir);
                    success = makeDir.mkdirs();
                    String filePath = outPath + i + ".txt";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        success = file.createNewFile();
                    }
                    if (success) {
                        WriteToFile.save(list, i, filePath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
