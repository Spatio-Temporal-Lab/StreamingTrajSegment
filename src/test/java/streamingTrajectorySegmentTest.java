import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.datasource.SourceRel;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.operator.*;


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
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(data -> data.tid)
                .process(new ProcessFunction(45, 1200000));
        SingleOutputStreamOperator<Object> keyedStream2 = GPSStream.keyBy(data -> data.tid)
                .process(new ProcessFunctionMergeDistance(45, 1200000));
        SingleOutputStreamOperator<Object> keyedStream3 = GPSStream.keyBy(data -> data.tid)
                .process(new ProcessFunctionMergeDistanceGird(45, 1200000));
        SingleOutputStreamOperator<Object> keyedStream4 = GPSStream.keyBy(data -> data.tid)
                .process(new ProcessFunctionBaselineSws());
        SingleOutputStreamOperator<Object> keyedStream5 = GPSStream.keyBy(data -> data.tid)
                .process(new ProcessFunctionBaselineSrd());
        //flink执行
        env.execute();

    }
}
