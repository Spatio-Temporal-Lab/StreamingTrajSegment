package org.ubcomp.sts.operator;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.util.Interpolator;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


/**
 * @author syy
 */
public abstract class AbstractProcessFunction extends KeyedProcessFunction<String, GpsPoint, Object> {

    private ValueState<PointList> pointListValueState;
    private ValueState<Container> containerValueState;
    private int countPoint;
    private long runtime;
    private long latency;

    public AbstractProcessFunction() {
    }

    @Override
    public void open(Configuration parameters) {
        pointListValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("pointList",
                        Types.POJO(PointList.class))
        );
        containerValueState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("container",
                        Types.POJO(Container.class))
        );
        countPoint = 0;
        runtime = 0;
        latency = 0;
    }


    @Override
    public void processElement(GpsPoint gpsPoint, KeyedProcessFunction<String, GpsPoint, Object>.Context context, Collector<Object> collector) throws Exception {
        long startTime = System.nanoTime();
        long lateTime = 0;
        PointList pointList = pointListValueState.value();
        if (pointList == null) {
            pointListValueState.update(new PointList(true));
            containerValueState.update(new Container());
        }
        pointList = pointListValueState.value();
        Container container = containerValueState.value();
        List<GpsPoint> latePoints = container.latePoints;
        List<GpsPoint> tempPoints = container.tempPoints;
        GpsPoint tempPoint = container.tempPoint;
        StreamAnomalyDetection streamLof = container.lof;
        /*long endTime = System.nanoTime();
        runtime += endTime - startTime;*/

        if (pointList.getSize() >= 3 && gpsPoint.ingestionTime -
                pointList.pointList.get(pointList.getSize() - 1).ingestionTime >= 40000) {
            List<GpsPoint> pp = Interpolator.interpolatePoints(pointList.pointList.subList(
                    pointList.getSize() - 3, pointList.getSize()), gpsPoint);
            for (GpsPoint p : pp) {
                pointList.add(p);
                double score = streamLof.update(p);
            }
        } else {
            //startTime = System.nanoTime();
            lateTime = process(pointList, gpsPoint, streamLof, container, runtime, countPoint);
            //endTime = System.nanoTime();
            //runtime += endTime - startTime;
        }

        /*startTime = System.nanoTime();*/
        countPoint++;
        container.latePoints = latePoints;
        container.tempPoints = tempPoints;
        container.tempPoint = tempPoint;
        container.lof = streamLof;
        pointListValueState.update(pointList);
        containerValueState.update(container);
        long endTime = System.nanoTime();
        //endTime = System.nanoTime();
        if (countPoint > 10000){
            runtime += endTime - startTime;
            latency += endTime - startTime + lateTime;
        }
    }

    @Override
    public void close() throws IOException {
        runtime = runtime / 1000000;
        latency = latency / 1000000;
        double throughput = (double) countPoint / runtime * 1000;
        String name = printResult();
        System.out.println(name + "-Latency: " + (double) latency / countPoint + " ms");
        System.out.println(name + "-Throughput: " + throughput + " records/s");

    }


    public abstract long process(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, Container container, long runtime, int countPoint) throws ParseException;

    public abstract String printResult();

}
