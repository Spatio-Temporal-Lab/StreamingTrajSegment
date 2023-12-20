package org.ubcomp.sts.executor;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLocalProcessFunctionForF1Score {

    protected Map<String, PointList> arrayListMap = new HashMap<>();
    protected Map<String, PointList> result = new HashMap<>();
    protected String pathIn;
    protected String pathOut;

    protected String dir;
    long countPoint = 0;

    public AbstractLocalProcessFunctionForF1Score(String pathIn, String pathOut, String dir) {
        this.pathIn = pathIn;
        this.pathOut = pathOut;
        this.dir = dir;
    }

    public double[] processElement() throws IOException, ParseException, FactoryException, TransformException {

        for (int i = 1; i <= 500; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(pathIn + i + ".txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                GpsPoint gpsPoint = mapFunction2(line);
                PointList pointList = arrayListMap.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                PointList resultList = result.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                process(pointList, gpsPoint, resultList);
            }
        }
        Set<String> keySet = arrayListMap.keySet();
        for (String key : keySet) {
            PointList list = arrayListMap.get(key);
            //varf

            result.get(key).getPointList().addAll(list.getPointList());
        }
        save(result, pathOut, dir);
        return null;
    }

    public abstract void process(PointList pointList, GpsPoint point, PointList result) throws ParseException, IOException, FactoryException, TransformException;

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
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                        // 追加内容到文件
                        GpsPoint point = list.getPointList().get(i - 1);
                        writer.write(point.lng + "," + point.lat + "," + point.tid + "," + point.ingestionTime + "," + point.isStayPoint);
                        writer.newLine();
                    } catch (IOException e) {
                        System.err.println("Error writing to the file: " + e.getMessage());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void verify(List<GpsPoint> list){

    }


}
