import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.calf1score.SPDOffline;
import org.ubcomp.sts.executor.LocalProcessFunctionGridForF1Score;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.MapToGPSPoint;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class F1ScoreTest {
    public static void main(String[] args) throws IOException, ParseException, FactoryException, TransformException {

        //set the datasets catalog
        String pathIn = "/home/datasets/Cq-taxi/taxi-30000-";
        //set default parameter T
        long defaultT = 300000;
        //set default parameter D
        double defaultD = 50;
        //set default parameter N
        int defaultN = 20;
        List<Double> list_D = new ArrayList<>();
        List<Long> list_T = new ArrayList<>();
        addD(list_D);
        addT(list_T);

        System.out.println("############################################");
        System.out.println("Now dataset: " + pathIn);
        //parameter D
        System.out.println("##################### parameter d #####################");
        for (double maxD : list_D) {
            System.out.println("now-> D:" + maxD);
            // set label and result store catalog
            String dirOutVal = "/home/datasets/dataLabel/Cq-taxi/D-" + maxD;
            String pathOutVal = "/home/datasets/dataLabel/Cq-taxi/D-" + maxD + "/taxi-30000-";
            String dirOutResult = "/home/datasets/dataResult/Cq-taxi/D-" + maxD;
            String pathOutResult = "/home/datasets/dataResult/Cq-taxi/D-" + maxD + "/taxi-30000-";
            System.out.println("start create label");
            SPDOffline SPD = new SPDOffline();
            SPD.process(pathIn, pathOutVal, dirOutVal, maxD, defaultT);

            System.out.println("start create result");
            LocalProcessFunctionGridForF1Score STEP = new LocalProcessFunctionGridForF1Score(pathIn, pathOutResult, dirOutResult, maxD, defaultT, defaultN);
            STEP.processElement();

            //**************************************************
            System.out.println("start calculate F1-score");
            double count = 0;
            double correct = 0;
            double isTrue = 0;
            double isTrueToFalse = 0;
            double isFalseToTrue = 0;
            Map<String, PointList> label = new HashMap<>();
            Map<String, PointList> result = new HashMap<>();

            // Store before reading to prevent memory overflow
            for (int i = 1; i <= 500; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(pathOutVal + i + ".txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    GpsPoint gpsPoint = MapToGPSPoint.mapFunctionAcc(line);
                    PointList pointList = label.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                    pointList.add(gpsPoint);
                }
                reader.close();
            }
            for (int i = 1; i <= 500; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(pathOutResult + i + ".txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    GpsPoint gpsPoint = MapToGPSPoint.mapFunctionAcc(line);
                    PointList pointList = result.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                    pointList.add(gpsPoint);
                }
                reader.close();
            }
            Set<String> keySet = label.keySet();
            for (String key : keySet) {
                ArrayList<GpsPoint> valList = label.get(key).getPointList();
                ArrayList<GpsPoint> resultList = result.get(key).getPointList();
                for (int i = 0; i < valList.size(); i++) {
                    if (valList.get(i).isStayPoint == resultList.get(i).isStayPoint) {
                        correct++;
                    }
                    if (valList.get(i).isStayPoint) {
                        isTrue++;
                        if (!resultList.get(i).isStayPoint) {
                            isTrueToFalse++;
                        }
                    } else {
                        if (resultList.get(i).isStayPoint) {
                            isFalseToTrue++;
                        }
                    }
                    count++;
                }
            }
            double TP = isTrue - isTrueToFalse;
            double FP = isFalseToTrue;
            double FN = isTrueToFalse;
            double precision = TP / (TP + FP);
            double recall = TP / (TP + FN);
            double F_Score = 2 * recall * precision / (recall + precision);
            System.out.println("accuracy：" + correct / count);
            System.out.println("recall：" + recall);
            System.out.println("F1-Score:" + F_Score);
        }

        //parameter T
        for (long minT : list_T) {
            System.out.println("now-> T:" + minT);
            String dirOutVal = "/home/datasets/dataLabel/Cq-taxi/T-" + minT;
            String pathOutVal = "/home/datasets/dataLabel/Cq-taxi/T-" + minT + "/taxi-30000-";
            String dirOutResult = "/home/datasets/dataResult/Cq-taxi/T-" + minT;
            String pathOutResult = "/home/datasets/dataResult/Cq-taxi/T-" + minT + "/taxi-30000-";

            System.out.println("start create label");
            SPDOffline SPD = new SPDOffline();
            SPD.process(pathIn, pathOutVal, dirOutVal, defaultD, minT);

            System.out.println("start create result");
            LocalProcessFunctionGridForF1Score STEP = new LocalProcessFunctionGridForF1Score(pathIn, pathOutResult, dirOutResult, defaultD, minT, defaultN);
            STEP.processElement();

            //**************************************************
            System.out.println("start calculate F1-score");
            double count = 0;
            double correct = 0;
            double isTrue = 0;
            double isTrueToFalse = 0;
            double isFalseToTrue = 0;
            Map<String, PointList> label = new HashMap<>();
            Map<String, PointList> result = new HashMap<>();

            for (int i = 1; i <= 500; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(pathOutVal + i + ".txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    GpsPoint gpsPoint = MapToGPSPoint.mapFunctionAcc(line);
                    PointList pointList = label.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                    pointList.add(gpsPoint);
                }
                reader.close();
            }
            for (int i = 1; i <= 500; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(pathOutResult + i + ".txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    GpsPoint gpsPoint = MapToGPSPoint.mapFunctionAcc(line);
                    PointList pointList = result.computeIfAbsent(gpsPoint.tid, k -> new PointList());
                    pointList.add(gpsPoint);
                }
                reader.close();
            }
            Set<String> keySet = label.keySet();
            for (String key : keySet) {
                ArrayList<GpsPoint> valList = label.get(key).getPointList();
                ArrayList<GpsPoint> resultList = result.get(key).getPointList();
                for (int i = 0; i < valList.size(); i++) {
                    if (valList.get(i).isStayPoint == resultList.get(i).isStayPoint) {
                        correct++;
                    }
                    if (valList.get(i).isStayPoint) {
                        isTrue++;
                        if (!resultList.get(i).isStayPoint) {
                            isTrueToFalse++;
                        }
                    } else {
                        if (resultList.get(i).isStayPoint) {
                            isFalseToTrue++;
                        }
                    }
                    count++;
                }
            }
            double TP = isTrue - isTrueToFalse;
            double FP = isFalseToTrue;
            double FN = isTrueToFalse;
            double precision = TP / (TP + FP);
            double recall = TP / (TP + FN);
            double F_Score = 2 * recall * precision / (recall + precision);
            System.out.println("accuracy：" + correct / count);
            System.out.println("recall：" + recall);
            System.out.println("F1-Score:" + F_Score);
        }
    }

    public static void addD(List<Double> list_D) {
        list_D.add(10.0);
        list_D.add(20.0);
        list_D.add(50.0);
        list_D.add(75.0);
        list_D.add(100.0);
    }

    public static void addT(List<Long> list_T) {
        list_T.add(60000L);//1min
        list_T.add(180000L);//3min
        list_T.add(300000L);//5min
        list_T.add(600000L);//10min
        list_T.add(900000L);//15min
    }
}