package org.ubcomp.sts;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.dataSource.sourceRel3;
import org.ubcomp.sts.objects.gpsPoint;
import org.ubcomp.sts.operators.myStsOnline;
import org.ubcomp.sts.partition.customPartitioner;

public class streamingTrajectorySegment_Online {
    public static void main(String[] args) throws Exception {
        //创建流环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //source类数据源
        DataStreamSource<gpsPoint> GPSStream = env.addSource(new sourceRel3());
        //keyby 并 进行分段
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(new customPartitioner<>())
                .process(new myStsOnline());
        //flink执行
        env.execute();
    }
}
