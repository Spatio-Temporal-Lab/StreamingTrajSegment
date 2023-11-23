package org.ubcomp.sts.lcaol;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class LocalTest {
    public static void main(String[] args) throws InterruptedException, IOException, ParseException {

        long t1;
        long t2;
        double run;
        double run2;

        //String path = "D:\\data\\cq_taxi\\batch-taxi-1000\\taxi-1000-";
        //String path1 = "/home/yangyangsun/StreamingTrajSegment/data/cq_taxi/batch-taxi-1000/taxi-1000-";
        //String path2 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/cq_taxi/batch-taxi-10000/taxi-1000-";
        String path3 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/cq_taxi/batch-taxi-30000/taxi-30000-";
        //String path4 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/cq_taxi/batch-taxi-50000/taxi-50000-";
        //String path5 = "/home/yangyangsun/yangyangsun/StreamingTrajSegment/data/cq_taxi/batch-taxi-100000/taxi-100000-";
        List<String> pathList = new ArrayList<>();
        //pathList.add(path1);
        //pathList.add(path2);
        pathList.add(path3);
        //pathList.add(path4);
        //pathList.add(path5);
        List<Double> list_D = new ArrayList<>();
        List<Long> list_T = new ArrayList<>();

        addD(list_D);
        addT(list_T);
        for (String path : pathList){
            System.out.println("############################################");
            System.out.println("Now: " + path);


            for (double maxD : list_D) {
                Thread.sleep(2000);
                LocalProcessFunctionBase spds = new LocalProcessFunctionBase(path, maxD, 300000);
                t1 = System.nanoTime();
                long t0 = spds.processElement();
                t2 = System.nanoTime();
                run = ((t2 - t1 + t0) / 1000000.0);
                run2 = ((t2 - t1 ) / 1000000.0);
                //System.out.println("参数D=" + maxD + " spds-runtime: " + run / 1000 + " s");
                System.out.println("参数D=" + maxD + " spds-avgLatency: " + run / spds.countPoint + " ms");
                //System.out.println(spds.countPoint +"  "+run2 + "  " + run +"    "+ t0);
                System.out.println("参数D=" + maxD + " spds-throughput: " + spds.countPoint * 1000 / run2 + " records/s");
                System.out.println();
                spds = null;
                Thread.sleep(2000);

                LocalProcessFunctionGrid spds_d_g = new LocalProcessFunctionGrid(path, maxD, 300000);
                t1 = System.nanoTime();
                t0 = spds_d_g.processElement();
                t2 = System.nanoTime();
                run = ((t2 - t1 + t0) / 1000000.0);
                run2 = ((t2 - t1 ) / 1000000.0);
                //System.out.println("参数D=" + maxD + " spds_d_g-runtime: " + run / 1000 + " s");
                System.out.println("参数D=" + maxD + " spds_d_g-avgLatency: " + run / spds_d_g.countPoint + " ms");
                System.out.println("参数D=" + maxD + " spds_d_g-throughput: " + spds_d_g.countPoint * 1000 / run2 + " records/s");
                System.out.println();
                spds_d_g = null;
                Thread.sleep(2000);

                LocalProcessFunctionBaselineSws sws = new LocalProcessFunctionBaselineSws(path);
                t1 = System.nanoTime();
                t0 = sws.processElement();
                t2 = System.nanoTime();
                run = ((t2 - t1 + t0) / 1000000.0);
                run2 = ((t2 - t1 ) / 1000000.0);
                //System.out.println("参数D=" + maxD + "sws-runtime: " + run / 1000 + " s");
                System.out.println("参数D=" + maxD + " sws-avgLatency: " + run / sws.countPoint + " ms");
                System.out.println("参数D=" + maxD + " sws-throughput: " + sws.countPoint * 1000 / run2 + " records/s");
                System.out.println();
                sws = null;
                Thread.sleep(2000);
                System.out.println("################################");
            }

            System.out.println("################################");
            //参数T
            for (long minT : list_T) {
                LocalProcessFunctionBase spds = new LocalProcessFunctionBase(path, 50, minT);
                t1 = System.nanoTime();
                long t0 = spds.processElement();
                t2 = System.nanoTime();
                run = ((t2 - t1 + t0) / 1000000.0);
                run2 = ((t2 - t1 ) / 1000000.0);
                //System.out.println("参数T=" + minT + " spds-runtime: " + run / 1000 + " s");
                System.out.println("参数T=" + minT + " spds-avgLatency: " + run / spds.countPoint + " ms");
                System.out.println("参数T=" + minT + " spds-throughput: " + spds.countPoint * 1000 / run2 + " records/s");
                System.out.println();
                spds = null;
                Thread.sleep(2000);

                /*LocalProcessFunctionMergeDistance spds_d = new LocalProcessFunctionMergeDistance(path, 50, minT);
                t1 = System.nanoTime();
                t0 = spds_d.processElement();
                t2 = System.nanoTime();
                run = ((t2 - t1 + t0) / 1000000.0);
                run2 = ((t2 - t1 ) / 1000000.0);
                //System.out.println("参数T=" + minT + " spds_d-runtime: " + run / 1000 + " s");
                System.out.println("参数T=" + minT + " spds_d-avgLatency: " + run / spds_d.countPoint + " ms");
                System.out.println("参数T=" + minT + " spds_d-throughput: " + spds_d.countPoint * 1000 / run2 + " records/s");
                System.out.println();
                spds_d = null;
                Thread.sleep(2000);*/

                LocalProcessFunctionGrid spds_d_g = new LocalProcessFunctionGrid(path, 50, minT);
                t1 = System.nanoTime();
                t0 = spds_d_g.processElement();
                t2 = System.nanoTime();
                run = ((t2 - t1 + t0) / 1000000.0);
                run2 = ((t2 - t1 ) / 1000000.0);
                //System.out.println("参数T=" + minT + " spds_d_g-runtime: " + run / 1000 + " s");
                System.out.println("参数T=" + minT + " spds_d_g-avgLatency: " + run / spds_d_g.countPoint + " ms");
                System.out.println("参数T=" + minT + " spds_d_g-throughput: " + spds_d_g.countPoint * 1000 / run2 + " records/s");
                System.out.println();
                spds_d_g = null;
                Thread.sleep(2000);

                LocalProcessFunctionBaselineSws sws = new LocalProcessFunctionBaselineSws(path);
                t1 = System.nanoTime();
                t0 = sws.processElement();
                t2 = System.nanoTime();
                run = ((t2 - t1 + t0) / 1000000.0);
                run2 = ((t2 - t1 ) / 1000000.0);
                //System.out.println("参数T=" + minT + "sws-runtime: " + run / 1000 + " s");
                System.out.println("参数T=" + minT + " sws-avgLatency: " + run / sws.countPoint + " ms");
                System.out.println("参数T=" + minT + " sws-throughput: " + sws.countPoint * 1000 / run2 + " records/s");
                System.out.println();
                sws = null;
                Thread.sleep(2000);
                System.out.println("################################");
            }
            System.out.println();
            System.out.println();
        }
        //参数D
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
        //list_T.add(1200000L);//20min
    }

}
