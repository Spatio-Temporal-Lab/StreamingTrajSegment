import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.dataSource.sourceRel3;
import org.ubcomp.sts.objects.gpsPoint;
import org.ubcomp.sts.operators.mySts;
import org.ubcomp.sts.partition.customPartitioner;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class streamingTrajectorySegment_Test {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //添加数据源
        //source类数据源
        DataStreamSource<gpsPoint> GPSStream = env.addSource(new sourceRel3());

        //keyby 并 进行分段
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(new customPartitioner<>())
                .process(new mySts(45, 1200000));
        //flink执行
        env.execute();

    }
}
