package org.ubcomp.sts.operators;

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
import org.ubcomp.sts.utils.calculateDistance;
import org.ubcomp.sts.utils.findT;

import java.util.ArrayList;
import java.util.List;

public class myStsBoth extends KeyedProcessFunction<String, gpsPoint, Object> {

    //构造方法
    public myStsBoth(double D, long T) {
        this.maxD = D;
        this.minT = T;
    }

    //最大距离间隔
    double maxD;
    //最短时间间隔
    long minT;
    Interpolator i1 = new Interpolator();
    //状态：轨迹点列表（存储到来得gps点）
    ValueState<tempPointList> tempPointListValueState;
    ValueState<tempFlag> tempFlagValueState;

    int c1;
    long t;

    int temcount1 = 0;
    int temcount2 = 0;


    @Override
    public void open(Configuration parameters) {
        tempPointListValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<tempPointList>("tempPointList",
                        Types.POJO(tempPointList.class))
        );
        tempFlagValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("tempFlag",
                        Types.POJO(tempFlag.class))
        );
        c1 = 0;
        t = 0;
    }

    @Override
    public void processElement(gpsPoint point, KeyedProcessFunction<String, gpsPoint, Object>.Context context, Collector<Object> collector) throws Exception {

        long startTime = System.currentTimeMillis();
        /**
         * step1 初始化状态
         **/
        tempPointList tempPointList = tempPointListValueState.value();
        if (tempPointList == null) {
            tempPointListValueState.update(new tempPointList(true));
            tempFlagValueState.update(new tempFlag());
        }
        tempPointList = tempPointListValueState.value();
        tempFlag tempFlag1 = tempFlagValueState.value();
        List<gpsPoint> latePoints = tempFlag1.latePoints;
        List<gpsPoint> tempPoints = tempFlag1.tempPoints;
        gpsPoint tempPoint = tempFlag1.tempPoint;
        streamLOF lof = tempFlag1.lof;


        /**
         * step2 根据距离索引判断是否存在驻留点
         *  step2.1 如果输入点前不存在驻留点，寻找驻留点
         **/
        if (!tempPointList.hasStayPoint) {
                //将点加入临时列表中
                tempPointList.add(point);
                double score = lof.update(point);
                //System.out.println(score + "  " + point);
                if (score > 30 && score < 10000) {
                }
                hasNotStayPoint(tempPointList, lof.Distances);
        }
        /**
         * step2.2 如果输入前存在驻留点，判断是否可以合并驻留点或者断开驻留点
         **/
        else {
                //将点加入临时列表中
                tempPointList.add(point);
                double score = lof.update(point);
                //System.out.println(score + "  " + point);
                if (score > 30 && score < 10000) {
                    //System.out.println("lof:" + score + "替换异常点" + " " + point);
                }
                hasStayPoint(tempPointList,lof.Distances);
        }
        c1++;
        tempFlag1.latePoints = latePoints;
        tempFlag1.tempPoints = tempPoints;
        tempFlag1.tempPoint = tempPoint;
        tempFlag1.lof = lof;
        tempPointListValueState.update(tempPointList);
        tempFlagValueState.update(tempFlag1);
        long endTime = System.currentTimeMillis();
        t += endTime - startTime;
    }

    @Override
    public void close() {
        double t1 = t/1.0;
        double processingTime = t/4.0;
        double throughput = (double) c1 / t1 * 1000;
        System.out.println("Average processing time: " +(double) t1 / c1 + " ms/record");
        System.out.println("Processing time: " + processingTime + " ms");
        System.out.println("Throughput: " + throughput + " records/s");
        System.out.println("Records；" + c1);
        System.out.println("MystsBoth");
        System.out.println("--------------------------");
        System.out.println("temcount1: " + temcount1);
        System.out.println("temcount2: " + temcount2);
    }


    public void hasNotStayPoint(tempPointList temp_point_list, List<Double> distances) {
        int ii = distances.size()-1;
        for (int i = temp_point_list.getSize() - 2; i >= 0; i--) {
            double distance;
            if (ii>=0){
                temcount1++;
                distance = distances.get(ii);
                ii--;
            }else {
                temcount2++;
                distance = calculateDistance.calculateDistance(temp_point_list.getPointList().get(temp_point_list.getSize() - 1),
                        temp_point_list.getPointList().get(i));
            }
            if (distance > maxD) {
                long t = temp_point_list.getPointList().get(temp_point_list.getSize()-1).ingestionTime -
                        temp_point_list.getPointList().get(i + 1).ingestionTime;
                if (t > minT) {
                    //更新标志并断开驻留点之前得点
                    temp_point_list.stayPointStart += i + 1 + 1;
                    temp_point_list.hasStayPoint = true;
                    temp_point_list.pointList = new ArrayList<>(
                            temp_point_list.getPointList().subList(i + 1, temp_point_list.getSize()));
                    temp_point_list.stayPointEnd = temp_point_list.stayPointStart + temp_point_list.getSize() - 1;
                    temp_point_list.getStayPointFlag = temp_point_list.getSize();
                    //更新索引
                }
                break;
            }
        }
    }

    public void hasStayPoint(tempPointList temp_point_list,List<Double> distances) {
        int t = findT.findT(temp_point_list.pointList, minT);
        if (t <= temp_point_list.getStayPointFlag) {
            int flag = 0;
            int ii = distances.size()-1;
            for (int i = temp_point_list.getSize() - 2; i >= t; i--) {
                double distance;
                if (ii>=0){
                    temcount1++;
                    distance = distances.get(ii);
                    ii--;
                }else {
                    temcount2++;
                    distance = calculateDistance.calculateDistance(temp_point_list.getPointList().get(temp_point_list.getSize() - 1),
                            temp_point_list.getPointList().get(i));
                }
                if (distance >= maxD) {
                    //确认构成不了
                    //System.out.println("驻留点1："+ temp_point_list.getPointList().subList(0,temp_point_list.getStayPointFlag));
                    temp_point_list.pointList = new ArrayList<>(temp_point_list.getPointList()
                            .subList(temp_point_list.getStayPointFlag, temp_point_list.getSize()));
                    //disIndex.deleteIndex(temp_point_list.getStayPointFlag);
                    temp_point_list.hasStayPoint = false;
                    temp_point_list.getStayPointFlag = -1;
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                //合并驻留点
                int subSize = temp_point_list.getSize() - temp_point_list.getStayPointFlag;
                temp_point_list.stayPointEnd = temp_point_list.stayPointEnd + subSize;
                temp_point_list.getStayPointFlag = temp_point_list.getSize();
            }

        } else {
            //System.out.println("驻留点2："+ temp_point_list.getPointList().subList(0,temp_point_list.getStayPointFlag));
            temp_point_list.pointList = new ArrayList<>(temp_point_list.getPointList()
                    .subList(temp_point_list.getStayPointFlag, temp_point_list.getSize()));
            //disIndex.deleteIndex(temp_point_list.getStayPointFlag);
            temp_point_list.hasStayPoint = false;
            temp_point_list.getStayPointFlag = -1;
        }
    }
}
