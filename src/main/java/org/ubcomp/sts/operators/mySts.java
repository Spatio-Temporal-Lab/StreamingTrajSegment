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

/**
 * @Description 自定义处理函数的base版本
 * @Author syy
 * @Date 2020/3/18 10:00
 * @Version 1.0
 **/

public class mySts extends KeyedProcessFunction<String, gpsPoint, Object> {


    /**
     * @param d maximum distance interval
     * @param t minimum time interval
     **/
    public mySts(double d, long t) {
        this.maxD = d;
        this.minT = t;
    }

    /**
    maxD: maximum distance interval
     */
    double maxD;
    /**
    minT: minimum time interval
     */
    long minT;
    /**
     * lof: lof object
     */

    /**
     * i1: interpolate object
     */
    Interpolator i1 = new Interpolator();
    /**
     * tempPointListValueState: store the temporary point list
     */
    ValueState<tempPointList> tempPointListValueState;
    ValueState<tempFlag> tempFlagValueState;

    int c1;
    long t;


    /**
     * @description: open method
     * @param parameters Configuration
     */
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

    /**
     * @Description process function
     * @param point input
     * @param context context
     * @param collector collector
     */
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
        List<gpsPoint> latePoints = tempFlag1.latePoints;
        List<gpsPoint> tempPoints = tempFlag1.tempPoints;
        gpsPoint tempPoint = tempFlag1.tempPoint;
        streamLOF lof = tempFlag1.lof;


        //step2 根据距离索引判断是否存在驻留点
        //step2.1 如果输入点前不存在驻留点，寻找驻留点
        if (!tempPointList.hasStayPoint) {
                //将点加入临时列表中
                tempPointList.add(point);
                double score = lof.update(point);
                //System.out.println(score + "  " + point);
                if (score > 30 && score < 10000) {
                }
                hasNotStayPoint(tempPointList);
        }
        //step2.2 如果输入前存在驻留点，判断是否可以合并驻留点或者断开驻留点
        else {
                //将点加入临时列表中
                tempPointList.add(point);
                double score = lof.update(point);
                //System.out.println(score + "  " + point);
                if (score > 30 && score < 10000) {
                    //System.out.println("lof:" + score + "替换异常点" + " " + point);
                }
                hasStayPoint(tempPointList);
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

    /**
     * @Description  close
     */
    @Override
    public void close() {
        double t1 = t/1.0;
        double processingTime = t/4.0;
        double throughput = (double) c1 / t1 * 1000;
        System.out.println("Average processing time: " + t1 / c1 + " ms/record");
        System.out.println("Processing time: " + processingTime + " ms");
        System.out.println("Throughput: " + throughput + " records/s");
        System.out.println("Records；" + c1);
        System.out.println("Mysts");
        System.out.println("--------------------------");

    }

    /**
     * @Description 无驻留点处理过程
     * @param tempPointList 临时存储输入点列表
     */
    public void hasNotStayPoint(tempPointList tempPointList) {
        for (int i = tempPointList.getSize() - 2; i >= 0; i--) {
            double distance = calculateDistance.calculateDistance(tempPointList.getPointList().get(tempPointList.getSize() - 1),
                    tempPointList.getPointList().get(i));
            if (distance > maxD) {
                long t = tempPointList.getPointList().get(tempPointList.getSize()-1).ingestionTime -
                        tempPointList.getPointList().get(i + 1).ingestionTime;
                if (t > minT) {
                    //更新标志并断开驻留点之前得点
                    tempPointList.stayPointStart += i + 1 + 1;
                    tempPointList.hasStayPoint = true;
                    tempPointList.pointList = new ArrayList<>(
                            tempPointList.getPointList().subList(i + 1, tempPointList.getSize()));
                    tempPointList.stayPointEnd = tempPointList.stayPointStart + tempPointList.getSize() - 1;
                    tempPointList.getStayPointFlag = tempPointList.getSize();
                    //更新索引
                }
                break;
            }
        }
    }

    /**
     * @Description 有驻留点处理过程
     * @param tempPointList 临时存储输入点列表
     */
    public void hasStayPoint(tempPointList tempPointList) {
        int t = findT.findT(tempPointList.pointList, minT);
        if (t <= tempPointList.getStayPointFlag) {
            int flag = 0;
            for (int i = tempPointList.getSize() - 2; i >= t; i--) {
                double distance = calculateDistance.calculateDistance(tempPointList.getPointList().get(tempPointList.getSize() - 1),
                        tempPointList.getPointList().get(i));
                if (distance >= maxD) {
                    //确认构成不了
                   // System.out.println("驻留点1："+ tempPointList.getPointList().subList(0,tempPointList.getStayPointFlag));
                    tempPointList.pointList = new ArrayList<>(tempPointList.getPointList()
                            .subList(tempPointList.getStayPointFlag, tempPointList.getSize()));
                    tempPointList.hasStayPoint = false;
                    tempPointList.getStayPointFlag = -1;
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                //合并驻留点
                int subSize = tempPointList.getSize() - tempPointList.getStayPointFlag;
                tempPointList.stayPointEnd = tempPointList.stayPointEnd + subSize;
                tempPointList.getStayPointFlag = tempPointList.getSize();
            }

        } else {
            //System.out.println("驻留点2："+ tempPointList.getPointList().subList(0,tempPointList.getStayPointFlag));
            tempPointList.pointList = new ArrayList<>(tempPointList.getPointList()
                    .subList(tempPointList.getStayPointFlag, tempPointList.getSize()));
            //disIndex.deleteIndex(temp_point_list.getStayPointFlag);
            tempPointList.hasStayPoint = false;
            tempPointList.getStayPointFlag = -1;
        }
    }
}
