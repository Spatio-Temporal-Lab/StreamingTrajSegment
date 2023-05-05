package org.ubcomp.sts.operators;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.ubcomp.sts.objects.GpsPoint;
import org.ubcomp.sts.objects.TempFlag;
import org.ubcomp.sts.objects.TempPointList;
import org.ubcomp.sts.tlof.StreamLOF;
import org.ubcomp.sts.utils.Interpolator;
import org.ubcomp.sts.utils.CalculateDistance;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 自定义处理函数的base版本
 * @Author syy
 * @Date 2020/3/18 10:00
 * @Version 1.0
 **/

public class ProcessFunctionBaselineSrd extends KeyedProcessFunction<String, GpsPoint, Object> {

    double min_r = 10000;
    double min_density = 0.5;

    TempPointList tempPointList = new TempPointList(true);
    ValueState<TempPointList> tempPointListValueState;
    ValueState<TempFlag> tempFlagValueState;
    Interpolator i1 = new Interpolator();
    int c1;
    long t;
    public ProcessFunctionBaselineSrd() {
    }

    @Override
    public void open(Configuration parameters) {
        tempPointListValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("tempPointList",
                        Types.POJO(TempPointList.class))
        );
        tempFlagValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("tempFlag",
                        Types.POJO(TempFlag.class))
        );
        c1 = 0;
        t=0;
    }

    @Override
    public void processElement(GpsPoint point, KeyedProcessFunction<String, GpsPoint, Object>.Context context, Collector<Object> collector) throws Exception {

        long startTime = System.currentTimeMillis();
        TempPointList tempPointList = tempPointListValueState.value();
        if (tempPointList == null) {
            tempPointListValueState.update(new TempPointList(true));
            tempFlagValueState.update(new TempFlag());
        }
        tempPointList = tempPointListValueState.value();
        TempFlag tempFlag1 = tempFlagValueState.value();
        int i = tempFlag1.i;
        double radius = tempFlag1.radius;
        List<GpsPoint> centroids = tempFlag1.centroids;
        List<Integer> cutofs = tempFlag1.cutofs;
        int nPoints = tempFlag1.nPoints;
        GpsPoint currentCentroid = tempFlag1.currentCentroid;
        StreamLOF lof = tempFlag1.lof;


        if (currentCentroid == null) {
            currentCentroid = point;
        }
        if (tempPointList.getSize() >= 3 && point.ingestionTime -
                tempPointList.pointList.get(tempPointList.getSize() - 1).ingestionTime >= 30000) {
            //直接插值
            //System.out.println("直接插值 " + (point.ingestionTime -temp_point_list.pointList.get(temp_point_list.getSize() - 1).ingestionTime) + " "+ point);
            List<GpsPoint> pp = i1.interpolatePoints(tempPointList.pointList.subList(
                    tempPointList.getSize() - 3, tempPointList.getSize()), point);
            for (GpsPoint p : pp) {
                tempPointList.add(p);
                lof.update(p);
            }
        } else {
            //将点加入临时列表中
            tempPointList.add(point);
            double score = lof.update(point);
            //System.out.println(score + "  " + point);
            if (score > 30 && score < 10000) {
                //System.out.println("lof:" + score + "替换异常点" + " " + point);
                if (tempPointList.getSize() > 3) {
                    GpsPoint p = i1.interpolatePosition(tempPointList.pointList.subList(
                            tempPointList.getSize() - 4, tempPointList.getSize() - 1), point.ingestionTime);
                    tempPointList.pointList.remove(tempPointList.getSize() - 1);
                    tempPointList.add(p);
                    lof.deletePoint();
                    lof.update(p);
                }
            }
            nPoints++;
            double distance = CalculateDistance.calculateDistance(point, currentCentroid);
            radius = Math.max(radius, distance);

            if (radius > min_r) {
                double density = nPoints / (Math.PI * radius * radius);
                if (density < min_density) {
                    cutofs.add(i);
                    centroids.add(currentCentroid);
                    //System.out.println(point);
                    tempPointList.pointList = new ArrayList<>(tempPointList.pointList.subList(0, tempPointList.getSize()-1));
                    currentCentroid = point;
                    nPoints = 1;
                    radius = 0;
                }
            }

            currentCentroid.lng = ((nPoints - 1) * currentCentroid.lng + point.lng) / nPoints;
            currentCentroid.lat = ((nPoints - 1) * currentCentroid.lat + point.lat) / nPoints;
            i++;
        }
        c1++;
        tempFlag1.i= i;
        tempFlag1.radius = radius;
        tempFlag1.centroids = centroids;
        tempFlag1.cutofs = cutofs;
        tempFlag1.nPoints = nPoints;
        tempFlag1.currentCentroid= currentCentroid;
        tempFlag1.lof = lof;
        tempPointListValueState.update(tempPointList);
        tempFlagValueState.update(tempFlag1);
        long endTime = System.currentTimeMillis();
        t += endTime - startTime;
    }


    @Override
    public void close() {
        double t1 = t/1.0;
        double processingTime = (double) t / 4;
        double throughput = (double) c1 / t1 * 1000;
        System.out.println("Average processing time: " +(double) t1 / c1 + " ms/record");
        System.out.println("Processing time: " + processingTime + " ms");
        System.out.println("Throughput: " + throughput + " records/s");
        System.out.println("Records；" + c1);
        System.out.println("MystsDRS");
        System.out.println("--------------------------");
    }
}