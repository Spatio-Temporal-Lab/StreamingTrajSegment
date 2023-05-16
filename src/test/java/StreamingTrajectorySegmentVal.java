import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.ubcomp.sts.datasource.SourceVal;
import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.operator.ProcessFunction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


/**
 * Execute the flink program
 *
 * @author syy
 **/

public class StreamingTrajectorySegmentVal {
    public static void main(String[] args) throws Exception {

        String filePath1 = "val.txt";
        String filePath2 = "output.txt";
        new BufferedWriter(new FileWriter(filePath2));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<GpsPoint> GPSStream = env.addSource(new SourceVal());
        SingleOutputStreamOperator<Object> keyedStream = GPSStream.keyBy(data -> data.tid)
                .process(new ProcessFunction(45, 10000));
        env.execute("0");

        byte[] file1Content = Files.readAllBytes(Paths.get(filePath1));
        byte[] file2Content = Files.readAllBytes(Paths.get(filePath2));
        boolean isSame = Arrays.equals(file1Content, file2Content);
        assertTrue(isSame);
    }

}
