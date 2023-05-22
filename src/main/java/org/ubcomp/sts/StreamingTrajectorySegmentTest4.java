package org.ubcomp.sts;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.datasource.SourceRel;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.operator.ProcessFunction;
import org.ubcomp.sts.operator.ProcessFunctionBaselineSws;
import org.ubcomp.sts.operator.ProcessFunctionMergeDistance;
import org.ubcomp.sts.operator.ProcessFunctionMergeDistanceGird;
import org.ubcomp.sts.partition.CustomPartitioner;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class StreamingTrajectorySegmentTest4 {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<GpsPoint> GPSStream = env.addSource(new SourceRel());
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(new CustomPartitioner<>())
                .process(new ProcessFunctionBaselineSws());
        env.execute("Sws");


    }
}
