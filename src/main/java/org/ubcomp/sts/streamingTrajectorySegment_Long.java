package org.ubcomp.sts;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.dataSource.source;
import org.ubcomp.sts.objects.gpsPoint;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class streamingTrajectorySegment_Long {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //添加数据源
        //source类数据源
        DataStreamSource<gpsPoint> GPSStream = env.addSource(new source());
        GPSStream.map(data -> 1);

        //flink执行
        env.execute();

    }
}
