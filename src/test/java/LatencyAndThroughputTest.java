import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.executor.LocalProcessFunctionBase;
import org.ubcomp.sts.executor.LocalProcessFunctionBaselineSws;
import org.ubcomp.sts.executor.LocalProcessFunctionGrid;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class LatencyAndThroughputTest {
    public static void main(String[] args) throws IOException, ParseException, FactoryException, TransformException {

        //set the number of experiment
        int expNum = 10;
        //set default parameter T
        long defaultT = 300000;
        //set default parameter D
        double defaultD = 50;
        //set default parameter N
        int defaultN = 20;
        //set the datasets catalog
        String path1 = "/home/datasets/Cq-taxi/taxi-10000-";
        String path2 = "/home/datasets/Cq-taxi/taxi-30000-";

        double latency;
        double processTime;
        double[] delay;
        ArrayList<Double> latencyResults = new ArrayList<>();
        ArrayList<Double> throughputResult = new ArrayList<>();
        List<String> pathList = new ArrayList<>();
        List<Double> list_D = new ArrayList<>();
        List<Long> list_T = new ArrayList<>();
        List<Integer> list_GridResolution = new ArrayList<>();

        pathList.add(path1);
        pathList.add(path2);
        addD(list_D);
        addT(list_T);
        addN(list_GridResolution);

        // start experiments
        for (String path : pathList) {
            System.out.println("############################################");
            System.out.println("Now dataset: " + path);
            System.out.println("##################### parameter d #####################");
            //parameter D
            for (double maxD : list_D) {
                //start SPD method
                for (int i = 1; i <= expNum; i++) {
                    LocalProcessFunctionBase STEP = new LocalProcessFunctionBase(path, maxD, defaultT);
                    delay = STEP.processElement();
                    latency = (delay[0]);
                    processTime = (delay[1]);
                    latencyResults.add(latency / STEP.countPoint);
                    throughputResult.add(STEP.countPoint * 1000 / processTime);
                }
                System.out.println("parameter D=" + maxD + " SPD_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("parameter D=" + maxD + " SPD_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
                //start STEP method
                for (int i = 1; i <= expNum; i++) {
                    LocalProcessFunctionGrid STEP_GRID = new LocalProcessFunctionGrid(path, maxD, defaultT, defaultN);
                    delay = STEP_GRID.processElement();
                    latency = (delay[0]);
                    processTime = (delay[1]);
                    latencyResults.add(latency / STEP_GRID.countPoint);
                    throughputResult.add(STEP_GRID.countPoint * 1000 / processTime);
                }
                System.out.println("parameter D=" + maxD + " STEP_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("parameter D=" + maxD + " STEP_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
            }
            System.out.println("##################### parameter t #####################");
            //parameter T
            for (long minT : list_T) {
                //start SPD method
                for (int i = 1; i <= expNum; i++) {
                    LocalProcessFunctionBase STEP = new LocalProcessFunctionBase(path, defaultD, minT);
                    delay = STEP.processElement();
                    latency = (delay[0]);
                    processTime = (delay[1]);
                    latencyResults.add(latency / STEP.countPoint);
                    throughputResult.add(STEP.countPoint * 1000 / processTime);
                }
                System.out.println("parameter T=" + minT + " SPD_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("parameter T=" + minT + " SPD_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
                //start Grid method
                for (int i = 1; i <= expNum; i++) {
                    LocalProcessFunctionGrid STEP_GRID = new LocalProcessFunctionGrid(path, defaultD, minT, defaultN);
                    delay = STEP_GRID.processElement();
                    latency = (delay[0]);
                    processTime = (delay[1]);
                    latencyResults.add(latency / STEP_GRID.countPoint);
                    throughputResult.add(STEP_GRID.countPoint * 1000 / processTime);
                }
                System.out.println("parameter T=" + minT + " STEP_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("parameter T=" + minT + " STEP_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
            }
            System.out.println("##################### parameter n #####################");
            //parameter n
            for (int n : list_GridResolution) {
                //start Grid method
                for (int i = 1; i <= expNum; i++) {
                    LocalProcessFunctionGrid STEP_GRID = new LocalProcessFunctionGrid(path, defaultD, defaultT, n);
                    delay = STEP_GRID.processElement();
                    latency = (delay[0]);
                    processTime = (delay[1]);
                    latencyResults.add(latency / STEP_GRID.countPoint);
                    throughputResult.add(STEP_GRID.countPoint * 1000 / processTime);
                }
                System.out.println("parameter n=" + n + " STEP_AvgLatency: " + avg(latencyResults) + " ms");
                System.out.println("parameter n=" + n + " STEP_Throughput: " + avg(throughputResult) + " records/s");
                System.out.println();
                latencyResults.clear();
                throughputResult.clear();
            }
            System.out.println("##################### SWS #####################");
            //start sws
            for (int i = 1; i <= expNum; i++) {
                LocalProcessFunctionBaselineSws sws = new LocalProcessFunctionBaselineSws(path);
                delay = sws.processElement();
                latency = (delay[0]);
                processTime = (delay[1]);
                latencyResults.add(latency / sws.countPoint);
                throughputResult.add(sws.countPoint * 1000 / processTime);
            }
            System.out.println("SWS_AvgLatency: " + avg(latencyResults) + " ms");
            System.out.println("SWS_Throughput:" + avg(throughputResult) + " records/s");
            System.out.println();
            latencyResults.clear();
            throughputResult.clear();
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
