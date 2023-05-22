package org.ubcomp.sts.operator;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.ubcomp.sts.method.srd.Srd;
import org.ubcomp.sts.method.streamlof.StreamAnomalyDetection;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.Container;
import org.ubcomp.sts.object.PointList;
import org.ubcomp.sts.object.SrdContainer;
import org.ubcomp.sts.util.Interpolator;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author paopaotang
 */
public class ProcessFunctionBaselineSrd extends AbstractProcessFunction {

    private ValueState<SrdContainer> srdContainerValueState;
    private static final double MIN_R = 10000;
    private static final double MIN_DENSITY = 5e-6;

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
    public long process(PointList pointList, GpsPoint point, StreamAnomalyDetection lof, Container container, long runtime, int countPoints) throws ParseException, IOException {

        SrdContainer srdContainer = srdContainerValueState.value();
        if (pointList == null) {
            srdContainer = new SrdContainer();
        }
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