import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.ubcomp.sts.datasource.SourceRel;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.operator.ProcessFunctionBaselineSrd;
import org.ubcomp.sts.partition.CustomPartitioner;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class StreamingTrajectorySegmentTest51 {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<GpsPoint> GPSStream = env.addSource(new SourceRel());
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(new CustomPartitioner<>())
                .process(new ProcessFunctionBaselineSrd());
        env.execute("Srd");

    }
}
