package org.ubcomp.sts;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.datasource.SourceRel;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.operator.ProcessFunction;
import org.ubcomp.sts.operator.ProcessFunctionMergeDistance;
import org.ubcomp.sts.operator.ProcessFunctionMergeDistanceGird;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class StreamingTrajectorySegmentTest1 {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<GpsPoint> GPSStream2 = env.addSource(new SourceRel());
        SingleOutputStreamOperator<Object> keyedStream3 = GPSStream2.keyBy(data -> data.tid)
                .process(new ProcessFunctionMergeDistanceGird(45, 1200000));
        JobExecutionResult execute2 = env.execute("2");

        DataStreamSource<GpsPoint> GPSStream = env.addSource(new SourceRel());
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(data -> data.tid)
                .process(new ProcessFunction(45, 1200000));
        JobExecutionResult execute = env.execute("0");

        DataStreamSource<GpsPoint> GPSStream1 = env.addSource(new SourceRel());
        SingleOutputStreamOperator<Object> keyedStream2 = GPSStream1.keyBy(data -> data.tid)
                .process(new ProcessFunctionMergeDistance(45, 1200000));
        JobExecutionResult execute1 = env.execute("1");

        /*DataStreamSource<GpsPoint> GPSStream3 = env.addSource(new SourceRel());
        SingleOutputStreamOperator<Object> keyedStream4 = GPSStream3.keyBy(data -> data.tid)
                .process(new ProcessFunctionBaselineSws());
        JobExecutionResult execute3 = env.execute("3");

        DataStreamSource<GpsPoint> GPSStream4 = env.addSource(new SourceRel());
        SingleOutputStreamOperator<Object> keyedStream5 = GPSStream4.keyBy(data -> data.tid)
                .process(new ProcessFunctionBaselineSrd());
        JobExecutionResult execute4 = env.execute("4");*/

    }
}
