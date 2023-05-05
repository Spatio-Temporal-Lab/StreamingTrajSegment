package org.ubcomp.sts.operators;


import org.apache.commons.math3.stat.regression.SimpleRegression;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description sws对比方法
 * @Author syy
 * @Date 2020/3/18 10:00
 * @Version 1.0
 **/

public class ProcessFunctionBaselineSws extends KeyedProcessFunction<String, GpsPoint, Object> {

    ValueState<TempPointList> tempPointListValueState;
    ValueState<TempFlag> tempFlagValueState;
    int w = 14;
    /**
     * i1: interpolate object
     */
    Interpolator i1 = new Interpolator();
    int c1;
    long t;

    public ProcessFunctionBaselineSws() {
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
        //step1 初始化临时列表
        TempPointList tempPointList = tempPointListValueState.value();
        if (tempPointList == null) {
            tempPointListValueState.update(new TempPointList(true));
            tempFlagValueState.update(new TempFlag());
        }
        tempPointList = tempPointListValueState.value();
        TempFlag tempFlag1 = tempFlagValueState.value();
        StreamLOF lof = tempFlag1.lof;

        if (tempPointList.getSize() >= 3 &&  point.ingestionTime -
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

            if (tempPointList.getSize()>w){
                double error = calError(tempPointList.getPointList().subList(tempPointList.getSize()-w,tempPointList.getSize()));
                //System.out.println(error);
                if (error > 5000){
                    tempPointList.pointList = new ArrayList<>();
                }
            }
        }
        c1++;
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
        System.out.println("MystsSws");
        System.out.println("--------------------------");
    }

    public double calError(List<GpsPoint> pointList) throws ParseException {
        int mid = pointList.size()/2 + 1;
        List<GpsPoint> pointList_f = pointList.subList(0,mid);
        List<GpsPoint> pointList_b = pointList.subList(mid,pointList.size());
        long tf = pointList_f.get(0).ingestionTime;
        long tb = pointList_b.get(pointList_b.size()-1).ingestionTime;

        SimpleRegression regression1_y = new SimpleRegression();
        for (GpsPoint point : pointList_f) {
            regression1_y.addData(point.ingestionTime - tf, point.lng);
        }
        double p_f_y = regression1_y.predict(pointList.get(mid).ingestionTime-tf);
        SimpleRegression regression1_x = new SimpleRegression();
        for (GpsPoint point : pointList_f) {
            regression1_x.addData(point.ingestionTime -tf , point.lat);
        }
        double p_f_x = regression1_x.predict(pointList.get(mid).ingestionTime-tf);
        GpsPoint p1 = new GpsPoint(p_f_y,p_f_x,pointList.get(mid).tid, pointList.get(mid).ingestionTime,0);

        SimpleRegression regression2_y = new SimpleRegression();
        for (GpsPoint point : pointList_b) {
            regression2_y.addData(point.ingestionTime-tb, point.lng);
        }
        double p_b_y = regression2_y.predict(pointList.get(mid).ingestionTime-tb);
        SimpleRegression regression2_x = new SimpleRegression();
        for (GpsPoint point : pointList_b) {
            regression2_x.addData(point.ingestionTime-tb, point.lat);
        }
        double p_b_x = regression2_x.predict(pointList.get(mid).ingestionTime-tb);
        GpsPoint p2 = new GpsPoint(p_b_y,p_b_x,pointList.get(mid).tid, pointList.get(mid).ingestionTime,0);

        GpsPoint p0 = new GpsPoint((p1.lng+ p2.lng)/2,(p1.lat+ p2.lat)/2,p1.tid, p1.ingestionTime,0);
        double error = CalculateDistance.calculateDistance(p0,pointList.get(mid));
        //System.out.println("p0:"+p0+" p1:"+p1+" p2:"+p2+"pmid"+pointList.get(mid));
        return error;
    }

}