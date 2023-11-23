package org.ubcomp.sts.lcaol;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class LocalTest2 {
    public static void main(String[] args) throws InterruptedException, IOException, ParseException {

        double run;
        String path3 = "D:\\project\\StreamingTrajSegment\\src\\main\\resources\\batch-taxi-1000\\taxi-1000-";
        List<String> pathList = new ArrayList<>();
        pathList.add(path3);
        List<Double> list_D = new ArrayList<>();
        List<Long> list_T = new ArrayList<>();

        addD(list_D);
        addT(list_T);
        for (String path : pathList){
            System.out.println("############################################");
            System.out.println("Now: " + path);

            for (double maxD : list_D){

                LocalProcessFunctionBase spds = new LocalProcessFunctionBase(path, maxD, 15000);
                long t0 = spds.processElement();
                run = (t0 / 1000000.0);
                System.out.println(run / spds.countPoint);

                System.out.println("############################################");


                LocalProcessFunctionGrid spds_d_g = new LocalProcessFunctionGrid(path, maxD, 15000);
                t0 = spds_d_g.processElement();
                run = (t0 / 1000000.0);
                System.out.println(run / spds_d_g.countPoint);

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
        //list_T.add(600000L);//10min
        //list_T.add(900000L);//15min
        //list_T.add(1200000L);//20min
    }

}
