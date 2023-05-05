package org.ubcomp.sts.operators;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.ubcomp.sts.index.Gird;
import org.ubcomp.sts.objects.GpsPoint;
import org.ubcomp.sts.objects.TempFlag;
import org.ubcomp.sts.objects.TempPointList;
import org.ubcomp.sts.tlof.StreamLOF;
import org.ubcomp.sts.utils.Interpolator;
import org.ubcomp.sts.utils.CalculateDistance;
import org.ubcomp.sts.utils.FindT;

import java.util.ArrayList;
import java.util.List;

/**
 * baseline 融合异常检测中的距离
 *
 * @author syy
 * @p D maximum distance interval
 * @p T minimum time interval
 **/

public class ProcessFunctionMergeDistance extends KeyedProcessFunction<String, GpsPoint, Object> {

    //构造方法
    public ProcessFunctionMergeDistance(double D, long T) {
        this.maxD = D;
        this.minT = T;
    }

    //最大距离间隔
    double maxD;
    //最短时间间隔
    long minT;
    //


    Interpolator i1 = new Interpolator();
    Gird u = new Gird();
    //状态：轨迹点列表（存储到来得gps点）
    ValueState<TempPointList> tempPointListValueState;
    ValueState<TempFlag> tempFlagValueState;

    int c1;
    long t;
    int cc1 = 0;
    int cc2 = 0;
    int tc1 = 0;
    int tc2 = 0;
    int tc3 = 0;

    @Override
    public void open(Configuration parameters) {
        tempPointListValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<TempPointList>("tempPointList",
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
        /**
         * step1 初始化状态
         **/
        TempPointList tempPointList = tempPointListValueState.value();
        if (tempPointList == null) {
            tempPointListValueState.update(new TempPointList(true));
            tempFlagValueState.update(new TempFlag());
        }
        tempPointList = tempPointListValueState.value();
        TempFlag tempFlag1 = tempFlagValueState.value();
        List<GpsPoint> latePoints = tempFlag1.latePoints;
        List<GpsPoint> tempPoints = tempFlag1.tempPoints;
        GpsPoint tempPoint = tempFlag1.tempPoint;
        StreamLOF lof = tempFlag1.lof;


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
                    //System.out.println("lof:" + score + "替换异常点" + " " + point);
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
        t = t + (endTime - startTime);
    }

    @Override
    public void close() {
        double t1 = t/1.0;
        double processingTime = t/4.0;
        double throughput = (double) c1 / t1 * 1000;
        System.out.println("Average processing time: " + (double) t1 / c1 + " records/ms");
        System.out.println("Processing time: " + processingTime + " ms");
        System.out.println("Throughput: " + throughput + " records/s");
        System.out.println("Records；" + c1);
        System.out.println("MystsBothGird");
        System.out.println("--------------------------");
        System.out.println("tc1:" + tc1);
        System.out.println("tc2:" + tc2);
        System.out.println("tc3:" + tc3);
        System.out.println("cc1:" + cc1);
        System.out.println("cc2:" + cc2);
    }

    public void hasNotStayPoint(TempPointList temp_point_list, List<Double> distances) {
        int ii = distances.size()-1;
        for (int i = temp_point_list.getSize() - 2; i >= 0; i--) {
            double distance;
            cc1++;
            if (ii>=0){
                cc2++;
                distance = distances.get(ii);
                ii--;
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
            }else {
                tc1++;
                int aa = u.inArea(temp_point_list.getPointList().get(i),
                        temp_point_list.getPointList().get(temp_point_list.getSize() - 1));
                if (aa == 2 || aa == 3) {
                    tc2++;
                    double td = CalculateDistance.calculateDistance(temp_point_list.getPointList().get(temp_point_list.getSize() - 1),
                            temp_point_list.getPointList().get(i));
                    if (td > maxD) {
                        long t = temp_point_list.getPointList().get(temp_point_list.getSize()-1).ingestionTime - temp_point_list.getPointList().get(i + 1).ingestionTime;
                        if (t > minT) {
                            //更新标志并断开驻留点之前得点
                            temp_point_list.stayPointStart += i + 1 + 1;
                            temp_point_list.hasStayPoint = true;
                            temp_point_list.pointList = new ArrayList<>(
                                    temp_point_list.getPointList().subList(i + 1, temp_point_list.getSize()));
                            temp_point_list.stayPointEnd = temp_point_list.stayPointStart + temp_point_list.getSize() - 1;
                            temp_point_list.getStayPointFlag = temp_point_list.getSize();
                            //更新索引
                            //disIndex.deleteIndex(i + 1);
                        }
                        break;
                    }
                } else if (aa >= 4) {
                    tc3++;
                    long t = temp_point_list.getPointList().get(temp_point_list.getSize()-1).ingestionTime - temp_point_list.getPointList().get(i + 1).ingestionTime;
                    if (t > minT) {
                        //更新标志并断开驻留点之前得点
                        temp_point_list.stayPointStart += i + 1 + 1;
                        temp_point_list.hasStayPoint = true;
                        temp_point_list.pointList = new ArrayList<>(
                                temp_point_list.getPointList().subList(i + 1, temp_point_list.getSize()));
                        temp_point_list.stayPointEnd = temp_point_list.stayPointStart + temp_point_list.getSize() - 1;
                        temp_point_list.getStayPointFlag = temp_point_list.getSize();
                        //更新索引
                        //disIndex.deleteIndex(i + 1);
                    }
                    break;
                }
            }
        }
    }

    public void hasStayPoint(TempPointList temp_point_list, List<Double> distances) {
        int t = FindT.findT(temp_point_list.pointList, minT);
        if (t <= temp_point_list.getStayPointFlag) {
            int flag = 0;
            int ii = distances.size()-1;
            for (int i = temp_point_list.getSize() - 2; i >= t; i--) {
                cc1++;
                double distance;
                if (ii>=0){
                    cc2++;
                    distance = distances.get(ii);
                    ii--;
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
                }else {
                    tc1++;
                    int aa = u.inArea(temp_point_list.getPointList().get(i),
                            temp_point_list.getPointList().get(temp_point_list.getSize() - 1));
                    if (aa >= 4) {
                        tc3++;
                        //确认构成不了
                        //System.out.println("驻留点："+ temp_point_list.getPointList().subList(0,temp_point_list.getStayPointFlag));
                        temp_point_list.pointList = new ArrayList<>(temp_point_list.getPointList()
                                .subList(temp_point_list.getStayPointFlag, temp_point_list.getSize()));
                        //disIndex.deleteIndex(temp_point_list.getStayPointFlag);
                        temp_point_list.hasStayPoint = false;
                        temp_point_list.getStayPointFlag = -1;
                        flag = 1;
                        break;
                    } else if (aa == 2 || aa == 3) {
                        tc2++;
                        //计算距离
                        double td = CalculateDistance.calculateDistance(temp_point_list.getPointList().get(temp_point_list.getSize() - 1),
                                temp_point_list.getPointList().get(i));
                        if (td > maxD) {
                            //System.out.println("驻留点：" + temp_point_list.getPointList().subList(0, temp_point_list.getStayPointFlag));
                            temp_point_list.pointList = new ArrayList<>(temp_point_list.getPointList()
                                    .subList(temp_point_list.getStayPointFlag, temp_point_list.getSize()));
                            //disIndex.deleteIndex(temp_point_list.getStayPointFlag);
                            temp_point_list.hasStayPoint = false;
                            temp_point_list.getStayPointFlag = -1;
                            flag = 1;
                            break;
                        }
                    }
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

