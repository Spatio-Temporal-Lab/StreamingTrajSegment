import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.dataSource.SourceRel;
import org.ubcomp.sts.objects.GpsPoint;
import org.ubcomp.sts.operators.ProcessFunctionBase;
import org.ubcomp.sts.partition.CustomPartitioner;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class streamingTrajectorySegmentTest {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //添加数据源
        //source类数据源
        DataStreamSource<GpsPoint> GPSStream = env.addSource(new SourceRel());

        //keyby 并 进行分段
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(new CustomPartitioner<>())
                .process(new ProcessFunctionBase(45, 1200000));
        //flink执行
        env.execute();

    }
}
