package org.ubcomp.sts.operator;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.ubcomp.sts.method.srd.Srd;
import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.object.SrdContainer;
import org.ubcomp.sts.util.Interpolator;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author paopaotang
 */
public class ProcessFunctionBaselineSrd extends AbstractProcessFunction {

    private ValueState<SrdContainer> srdContainerValueState;
    private static final double MIN_R = 10000;
    private static final double MIN_DENSITY = 1e-8;

    @Override
    public void open(Configuration parameters) {
        pointListValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("pointList",
                        Types.POJO(PointList.class))
        );
        srdContainerValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("container",
                        Types.POJO(SrdContainer.class))
        );
        countPoint = 0;
        runtime = 0;
        latency = 0;
    }

    public ProcessFunctionBaselineSrd() {
        super();
    }

    @Override
    public void processElement(GpsPoint gpsPoint, KeyedProcessFunction<String, GpsPoint, Object>.Context context,
                               Collector<Object> collector) throws Exception {
        long startTime = System.nanoTime();
        long lateTime = 0;
        PointList pointList = pointListValueState.value();
        SrdContainer srdContainer = srdContainerValueState.value();
        if (pointList == null) {
            pointList = new PointList();
            srdContainer = new SrdContainer();
        }
        List<GpsPoint> latePoints = srdContainer.latePoints;
        List<GpsPoint> tempPoints = srdContainer.unprocessedPoints;
        GpsPoint tempPoint = srdContainer.currentPoint;
        StreamAnomalyDetection streamLof = srdContainer.lof;

        if (pointList.getSize() >= 3 && gpsPoint.ingestionTime -
                pointList.pointList.get(pointList.getSize() - 1).ingestionTime >= 40000) {
            List<GpsPoint> pp = Interpolator.interpolatePoints(pointList.pointList.subList(
                    pointList.getSize() - 3, pointList.getSize()), gpsPoint);
            for (GpsPoint p : pp) {
                pointList.add(p);
                streamLof.update(p);
            }
        } else {
            lateTime = process1(pointList, gpsPoint, streamLof, srdContainer, runtime, countPoint);
        }

        /*startTime = System.nanoTime();*/
        countPoint++;
        srdContainer.latePoints = latePoints;
        srdContainer.unprocessedPoints = tempPoints;
        srdContainer.currentPoint = tempPoint;
        srdContainer.lof = streamLof;
        pointListValueState.update(pointList);
        srdContainerValueState.update(srdContainer);
        long endTime = System.nanoTime();
        //endTime = System.nanoTime();
        if (countPoint > 10000) {
            runtime += endTime - startTime;
            latency += endTime - startTime + lateTime;
        }
    }

    @Override
    public long process(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, Container container, long runtime, int countPoint) throws ParseException, IOException {
        return 0;
    }

    public long process1(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, SrdContainer srdContainer, long runtime, int countPoints) throws ParseException, IOException {

        pointList.add(point);
        double score = lof.update(point);
        if (score > 10 && score < 10000) {
            if (pointList.getSize() >= 4) {
                GpsPoint p = Interpolator.interpolatePosition(pointList.pointList.subList(
                        pointList.getSize() - 4, pointList.getSize() - 1), point.ingestionTime);
                pointList.pointList.remove(pointList.getSize() - 1);
                pointList.add(p);
                lof.deletePoint();
                lof.update(p);
            }
        }
        Srd.processSrd(pointList, point, srdContainer, MIN_R, MIN_DENSITY);
        return 0;
    }

    @Override
    public String printResult() {
        return "Srd";
    }
}