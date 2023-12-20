package org.ubcomp.sts.local;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class LocalTest2 {
    public static void main(String[] args) throws IOException, ParseException, FactoryException, TransformException {

        double run;
        double run2;
        String path3 = "src/main/resources/batch-taxi-1000/taxi-1000-";
        //String path3 = "src/main/resources/batch-wh-30000/wh-30000-";
        List<String> pathList = new ArrayList<>();
        pathList.add(path3);
        List<Double> list_D = new ArrayList<>();
        List<Long> list_T = new ArrayList<>();

        addD(list_D);
        addT(list_T);
        for (String path : pathList) {
            System.out.println("############################################");
            System.out.println("Now: " + path);

            for (double maxD : list_D) {

                LocalProcessFunctionBase spds = new LocalProcessFunctionBase(path, maxD, 300000);
                double[] delay = spds.processElement();
                run = (delay[0] / 1000000.0);
                run2 = (delay[1] / 1000000.0);
                System.out.println("参数D=" + maxD + " spds-avgLatency: " + run / spds.countPoint + " ms");
                System.out.println("参数D=" + maxD + " spds-throughput: " + spds.countPoint * 1000 / run2 + " records/s");
                System.out.println();


                LocalProcessFunctionGrid spds_g = new LocalProcessFunctionGrid(path, maxD, 300000, 1);
                delay = spds_g.processElement();
                run = (delay[0] / 1000000.0);
                run2 = (delay[1] / 1000000.0);
                System.out.println("参数D=" + maxD + " spds_d_g-avgLatency: " + run / spds_g.countPoint + " ms");
                System.out.println("参数D=" + maxD + " spds_d_g-throughput: " + spds_g.countPoint * 1000 / run2 + " records/s");
                System.out.println();

                System.out.println("############################################");



            }
        }
    }

    public static void addD(List<Double> list_D) {
        //list_D.add(10.0);
        //list_D.add(20.0);
        //list_D.add(50.0);
        //list_D.add(75.0);
        list_D.add(50.0);
    }

    public static void addT(List<Long> list_T) {
        //list_T.add(60000L);//1min
        //list_T.add(180000L);//3min
        //list_T.add(300000L);//5min
        list_T.add(600000L);//10min
        //list_T.add(900000L);//15min
        //list_T.add(1200000L);//20min
    }

}
