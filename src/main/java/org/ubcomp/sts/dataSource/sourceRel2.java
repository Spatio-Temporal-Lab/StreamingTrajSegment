package org.ubcomp.sts.dataSource;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.ubcomp.sts.objects.gpsPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Create data source
 *
 * @author syy
 **/

public class sourceRel2 implements SourceFunction<gpsPoint> {
    // 声明一个布尔变量，作为控制数据生成的标识位
    private Boolean running = true;

    @Override
    public void run(SourceContext<gpsPoint> ctx) throws Exception {

        try (InputStream in = sourceRel2.class.getClassLoader().getResourceAsStream("cd-3000-7day");
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))) {
            String trajStr;
            while ((trajStr = br.readLine()) != null) {
                /*String[] result = trajStr.replace("\'","")
                        .replace("[","").replace("]","").
                        replace(" ","").split(",");*/
                String[] result = trajStr.split(" ");
                //String t1 = result[0];
                //String t2 = result[1];
                String lng = result[1];
                String lat = result[2];
                String tid = result[0];
                String time = result[3];
                //String time = t1.substring(0, 4) + "-" + t1.substring(4, 6) + "-" + t1.substring(6, 8) + " " + t2.substring(0, 2) + ":" + t2.substring(2, 4) + ":" + t2.substring(4, 6);
                gpsPoint point = new gpsPoint(Double.parseDouble(lng),
                        Double.parseDouble(lat),
                        tid,
                        Long.parseLong(time),
                        0);
                ctx.collect(point);
                //Thread.sleep(100);
            }
        } catch (IOException e) {
            throw new RuntimeException("Generate trajectory error: " + e.getMessage());
        }

    }

    @Override
    public void cancel() {
        running = false;
    }

}

