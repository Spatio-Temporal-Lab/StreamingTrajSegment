package org.ubcomp.sts.local;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class LocalTest {
    public static void main(String[] args) throws IOException, ParseException, FactoryException, TransformException {

        double run;
        double run2;
        int count = 10;
        double[] delay;
        ArrayList<Double> latencyResults = new ArrayList<>();
        ArrayList<Double> throughputResult = new ArrayList<>();

        //String path = "D:\\data\\cq_taxi\\batch-taxi-1000\\taxi-1000-";
        //String path1 = "/home/yangyangsun/StreamingTrajSegment/data/cq_taxi/batch-taxi-1000/taxi-1000-";
        String path2 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/walk/batch-walk-10000/walk-10000-";
        String path3 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/walk/batch-walk-30000/walk-30000-";
        String path4 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/walk/batch-walk-50000/walk-50000-";
        String path5 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/walk/batch-walk-100000/walk-100000-";
        List<String> pathList = new ArrayList<>();
        //pathList.add(path1);
        pathList.add(path2);
        pathList.add(path3);
        pathList.add(path4);
        pathList.add(path5);
        List<Double> list_D = new ArrayList<>();
        List<Long> list_T = new ArrayList<>();
        List<Integer> list_GridSize = new ArrayList<>();

        addD(list_D);
        addT(list_T);
        addN(list_GridSize);
        for (String path : pathList) {
            System.out.println("############################################");
            System.out.println("Now: " + path);
           /* //System.out.println("##################### parameter d #####################");
            //参数D
            for (double maxD : list_D) {
                //init
                LocalProcessFunctionBase STEPInit = new LocalProcessFunctionBase(path, maxD, 300000);
                STEPInit.processElement();
                //start Base method
                for (int i = 1; i <= count; i++) {
                    LocalProcessFunctionBase STEP = new LocalProcessFunctionBase(path, maxD, 300000);
                    delay = STEP.processElement();
                    run = (delay[0]);
                    run2 = (delay[1]);
                    latencyResults.add(run / STEP.countPoint);
                    throughputResult.add(STEP.countPoint * 1000 / run2);
                }
                System.out.println("参数D=" + maxD + " STEP_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("参数D=" + maxD + " STEP_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
                //start Grid method
                for (int i = 1; i <= count; i++) {
                    LocalProcessFunctionGrid STEP_GRID = new LocalProcessFunctionGrid(path, maxD, 300000, 20);
                    delay = STEP_GRID.processElement();
                    run = (delay[0]);
                    run2 = (delay[1]);
                    latencyResults.add(run / STEP_GRID.countPoint);
                    throughputResult.add(STEP_GRID.countPoint * 1000 / run2);
                }
                System.out.println("参数D=" + maxD + " STEP_Grid_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("参数D=" + maxD + " STEP_Grid_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
            }*/
            /*System.out.println("##################### parameter t #####################");
            //参数T
            for (long minT : list_T) {
                //start Base method
                for (int i = 1; i <= count; i++) {
                    LocalProcessFunctionBase STEP = new LocalProcessFunctionBase(path, 50, minT);
                    delay = STEP.processElement();
                    run = (delay[0]);
                    run2 = (delay[1]);
                    latencyResults.add(run / STEP.countPoint);
                    throughputResult.add(STEP.countPoint * 1000 / run2);
                }
                System.out.println("参数T=" + minT + " STEP_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("参数T=" + minT + " STEP_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
                //start Grid method
                for (int i = 1; i <= count; i++) {
                    LocalProcessFunctionGrid STEP_GRID = new LocalProcessFunctionGrid(path, 50, minT,20 );
                    delay = STEP_GRID.processElement();
                    run = (delay[0]);
                    run2 = (delay[1]);
                    latencyResults.add(run / STEP_GRID.countPoint);
                    throughputResult.add(STEP_GRID.countPoint * 1000 / run2);
                }
                System.out.println("参数T=" + minT + " STEP_GRID_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("参数T=" + minT + " STEP_GRID_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
            }*/
           /* System.out.println("##################### parameter n #####################");
            //参数T
            for (int n : list_GridSize) {
                //start Grid method
                for (int i = 1; i <= count; i++) {
                    LocalProcessFunctionGrid STEP_GRID = new LocalProcessFunctionGrid(path, 50, 300000, n);
                    delay = STEP_GRID.processElement();
                    run = (delay[0]);
                    run2 = (delay[1]);
                    latencyResults.add(run / STEP_GRID.countPoint);
                    throughputResult.add(STEP_GRID.countPoint * 1000 / run2);
                }
                System.out.println("参数n=" + n + " STEP_GRID_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("参数n=" + n + " STEP_GRID_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
            }*/
            System.out.println("##################### SWS #####################");
            //start sws
            for (int i = 1; i <= count; i++) {
                LocalProcessFunctionBaselineSws sws = new LocalProcessFunctionBaselineSws(path);
                delay = sws.processElement();
                run = (delay[0] );
                run2 = (delay[1] );
                latencyResults.add(run / sws.countPoint);
                throughputResult.add(sws.countPoint * 1000 / run2);
            }
            System.out.println(" sws: " + avg(latencyResults) + " ms");
            System.out.println(" sws: " + avg(throughputResult) + " records/s");
            System.out.println();
            latencyResults.clear();
            throughputResult.clear();
        }

    }

    public static void addD(List<Double> list_D) {
        //list_D.add(10.0);
        //list_D.add(20.0);
        list_D.add(50.0);
        //list_D.add(75.0);
        //list_D.add(100.0);
    }

    public static void addT(List<Long> list_T) {
        //list_T.add(60000L);//1min
        //list_T.add(180000L);//3min
        list_T.add(300000L);//5min
        //list_T.add(600000L);//10min
        //list_T.add(900000L);//15min
    }

    public static void addN(List<Integer> list_N) {
        list_N.add(1);
        list_N.add(2);
        list_N.add(3);
        list_N.add(5);
        list_N.add(7);
        list_N.add(10);
        list_N.add(15);
        list_N.add(20);
        list_N.add(25);
        list_N.add(30);
    }

    public static double avg(ArrayList<Double> results) {
        double sum = 0;
        double count = 0;
        for (double result : results) {
            sum += result;
            count++;
        }
        return sum / count;
    }

}
