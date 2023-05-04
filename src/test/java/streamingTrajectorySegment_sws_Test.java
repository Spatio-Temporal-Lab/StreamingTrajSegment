import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.dataSource.sourceRel3;
import org.ubcomp.sts.operators.myStsSws;
import org.ubcomp.sts.objects.gpsPoint;

public class streamingTrajectorySegment_sws_Test {
    public static void main(String[] args) throws Exception {
        long t = 0;
        //创建流环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //添加数据源
        //source类数据源
        DataStreamSource<gpsPoint> GPSStream = env.addSource(new sourceRel3());
        //keyby 并 进行分段
        long start = System.currentTimeMillis();
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(data -> data.tid)
                .process(new myStsSws());
        //flink执行
        env.execute();
    }
}
