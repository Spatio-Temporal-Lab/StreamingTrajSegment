package org.ubcomp.sts.executor;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.MapToGPSPoint;
import org.ubcomp.sts.util.WriteToFile;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLocalProcessFunctionForF1Score {

    protected Map<String, PointList> arrayListMap = new HashMap<>();
    protected Map<String, PointList> result = new HashMap<>();
    protected String pathIn;
    protected String pathOut;
    protected String dir;

    public AbstractLocalProcessFunctionForF1Score(String pathIn, String pathOut, String dir) {
        this.pathIn = pathIn;
        this.pathOut = pathOut;
        this.dir = dir;
    }

    public void processElement() throws IOException, ParseException, FactoryException, TransformException {

        for (int i = 1; i <= 500; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(pathIn + i + ".txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                GpsPoint gpsPoint = MapToGPSPoint.mapFunction(line);
                PointList pointList = arrayListMap.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                PointList resultList = result.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                process(pointList, gpsPoint, resultList);
            }
        }
        Set<String> keySet = arrayListMap.keySet();
        for (String key : keySet) {
            PointList list = arrayListMap.get(key);
            result.get(key).getPointList().addAll(list.getPointList());
        }
        save(result, pathOut, dir);
    }

    public abstract void process(PointList pointList, GpsPoint point, PointList result) throws ParseException, IOException, FactoryException, TransformException;


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
