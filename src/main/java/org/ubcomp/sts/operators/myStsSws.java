package org.ubcomp.sts.operators;


import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.ubcomp.sts.objects.gpsPoint;
import org.ubcomp.sts.objects.tempFlag;
import org.ubcomp.sts.objects.tempPointList;
import org.ubcomp.sts.tlof.streamLOF;
import org.ubcomp.sts.utils.Interpolator;
import org.ubcomp.sts.utils.LinearRegression;
import org.ubcomp.sts.utils.calculateDistance;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 自定义处理函数的base版本
 * @Author syy
 * @Date 2020/3/18 10:00
 * @Version 1.0
 **/

public class myStsSws extends KeyedProcessFunction<String, gpsPoint, Object> {

    ValueState<tempPointList> tempPointListValueState;
    ValueState<tempFlag> tempFlagValueState;
    int w = 14;
    /**
     * i1: interpolate object
     */
    Interpolator i1 = new Interpolator();
    int c1;
    long t;

    public myStsSws() {
    }

    @Override
    public void open(Configuration parameters) {
        tempPointListValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("tempPointList",
                        Types.POJO(tempPointList.class))
        );
        tempFlagValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("tempFlag",
                        Types.POJO(tempFlag.class))
        );
        c1 = 0;
        t=0;
    }

    @Override
    public void processElement(gpsPoint point, KeyedProcessFunction<String, gpsPoint, Object>.Context context, Collector<Object> collector) throws Exception {
        long startTime = System.currentTimeMillis();
        //step1 初始化临时列表
        tempPointList tempPointList = tempPointListValueState.value();
        if (tempPointList == null) {
            tempPointListValueState.update(new tempPointList(true));
            tempFlagValueState.update(new tempFlag());
        }
        tempPointList = tempPointListValueState.value();
        tempFlag tempFlag1 = tempFlagValueState.value();
        streamLOF lof = tempFlag1.lof;

        if (tempPointList.getSize() >= 3 &&  point.ingestionTime -
                tempPointList.pointList.get(tempPointList.getSize() - 1).ingestionTime >= 30000) {
            //直接插值
            //System.out.println("直接插值 " + (point.ingestionTime -temp_point_list.pointList.get(temp_point_list.getSize() - 1).ingestionTime) + " "+ point);
            List<gpsPoint> pp = i1.interpolatePoints(tempPointList.pointList.subList(
                    tempPointList.getSize() - 3, tempPointList.getSize()), point);
            for (gpsPoint p : pp) {
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
                    gpsPoint p = i1.interpolatePosition(tempPointList.pointList.subList(
                            tempPointList.getSize() - 4, tempPointList.getSize() - 1), point.ingestionTime);
                    tempPointList.pointList.remove(tempPointList.getSize() - 1);
                    tempPointList.add(p);
                    lof.deletePoint();
                    lof.update(p);
                }
            }

            if (tempPointList.getSize()>w){
                double error = calError2(tempPointList.getPointList().subList(tempPointList.getSize()-w,tempPointList.getSize()));
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

    public double calError(List<gpsPoint> pointList) throws ParseException {
        int mid = pointList.size()/2 + 1;
        List<gpsPoint> pointList_f = pointList.subList(0,mid);
        List<gpsPoint> pointList_b = pointList.subList(mid,pointList.size());
        LinearRegression lr = new LinearRegression();
        lr.fit_x(pointList_f);
        lr.fit_y(pointList_f);
        gpsPoint p1 = lr.predict(pointList.get(mid));
        LinearRegression lr2 = new LinearRegression();
        lr2.fit_x(pointList_b);
        lr2.fit_y(pointList_b);
        gpsPoint p2 = lr2.predict(pointList.get(mid));
        gpsPoint p0 = new gpsPoint((p1.lng+ p2.lng)/2,(p1.lat+ p2.lat)/2,p1.tid, p1.ingestionTime,0);
        double error = calculateDistance.calculateDistance(p0,pointList.get(mid));
        return error;
    }

    public double calError2(List<gpsPoint> pointList) throws ParseException {
        int mid = pointList.size()/2 + 1;
        List<gpsPoint> pointList_f = pointList.subList(0,mid);
        List<gpsPoint> pointList_b = pointList.subList(mid,pointList.size());
        long tf = pointList_f.get(0).ingestionTime;
        long tb = pointList_b.get(pointList_b.size()-1).ingestionTime;

        SimpleRegression regression1_y = new SimpleRegression();
        for (gpsPoint point : pointList_f) {
            regression1_y.addData(point.ingestionTime - tf, point.lng);
        }
        double p_f_y = regression1_y.predict(pointList.get(mid).ingestionTime-tf);
        SimpleRegression regression1_x = new SimpleRegression();
        for (gpsPoint point : pointList_f) {
            regression1_x.addData(point.ingestionTime -tf , point.lat);
        }
        double p_f_x = regression1_x.predict(pointList.get(mid).ingestionTime-tf);
        gpsPoint p1 = new gpsPoint(p_f_y,p_f_x,pointList.get(mid).tid, pointList.get(mid).ingestionTime,0);

        SimpleRegression regression2_y = new SimpleRegression();
        for (gpsPoint point : pointList_b) {
            regression2_y.addData(point.ingestionTime-tb, point.lng);
        }
        double p_b_y = regression2_y.predict(pointList.get(mid).ingestionTime-tb);
        SimpleRegression regression2_x = new SimpleRegression();
        for (gpsPoint point : pointList_b) {
            regression2_x.addData(point.ingestionTime-tb, point.lat);
        }
        double p_b_x = regression2_x.predict(pointList.get(mid).ingestionTime-tb);
        gpsPoint p2 = new gpsPoint(p_b_y,p_b_x,pointList.get(mid).tid, pointList.get(mid).ingestionTime,0);

        gpsPoint p0 = new gpsPoint((p1.lng+ p2.lng)/2,(p1.lat+ p2.lat)/2,p1.tid, p1.ingestionTime,0);
        double error = calculateDistance.calculateDistance(p0,pointList.get(mid));
        //System.out.println("p0:"+p0+" p1:"+p1+" p2:"+p2+"pmid"+pointList.get(mid));
        return error;
    }

}