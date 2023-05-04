package org.ubcomp.sts;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.dataSource.sourceRel3;
import org.ubcomp.sts.objects.gpsPoint;
import org.ubcomp.sts.operators.myStsBothGird;
import org.ubcomp.sts.partition.customPartitioner;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class streamingTrajectorySegment_Both_Gird {
    public static void main(String[] args) throws Exception {
        //创建流环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //添加数据源
        //source类数据源
        DataStreamSource<gpsPoint> GPSstream = env.addSource(new sourceRel3());
        //keyby 并 进行分段
        SingleOutputStreamOperator<Object> keyedstream = GPSstream.keyBy(new customPartitioner<>())
                .process(new myStsBothGird(45, 1200000));
        //flink执行
        env.execute();
    }
}
