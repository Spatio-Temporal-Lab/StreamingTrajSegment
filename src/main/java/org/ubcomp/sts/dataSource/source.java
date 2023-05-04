package org.ubcomp.sts.dataSource;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.ubcomp.sts.objects.gpsPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Create data source
 *
 * @author syy
 **/

public class source implements SourceFunction<gpsPoint> {
    // 声明一个布尔变量，作为控制数据生成的标识位
    private Boolean running = true;

    @Override
    public void run(SourceContext<gpsPoint> ctx) throws Exception {
        String a1 = "2017-03-01 00:00:06";
        String a2 = "2017-03-31 23:59:47";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long t = formatter.parse(a2).getTime() - formatter.parse(a1).getTime();
        int count = 0;
        while (true){
            try (InputStream in = sourceRel.class.getClassLoader().getResourceAsStream("taxi.txt");
                 BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))) {
                String trajStr;
                while ((trajStr = br.readLine()) != null) {
                    String[] result = trajStr.split(",");
                    String lng = result[1];
                    String lat = result[0];
                    String time = result[3];
                    gpsPoint point = new gpsPoint(Double.parseDouble(lng),
                            Double.parseDouble(lat),
                            "100001",
                            time,
                            0);
                    ctx.collect(point);
                    Thread.sleep(1000000);
                }
            } catch (IOException e) {
                throw new RuntimeException("Generate trajectory error: " + e.getMessage());
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

}

