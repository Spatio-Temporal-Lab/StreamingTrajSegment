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

public class sourceRel4 implements SourceFunction<gpsPoint> {
    // 声明一个布尔变量，作为控制数据生成的标识位
    private Boolean running = true;

    @Override
    public void run(SourceContext<gpsPoint> ctx) throws Exception {
        int num = 0;

        try (InputStream in = sourceRel4.class.getClassLoader().getResourceAsStream("sort-1000-sh.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))) {
            String trajStr;
            int count = 0;
            while ((trajStr = br.readLine()) != null && num <=200000) {
                num++;
                count++;
                String[] result = trajStr.replace("/", "-").split(",");
                if (result.length != 4) {
                    continue;
                }

                String lng = result[3];
                String lat = result[2];
                String tid = result[0];
                String time = result[1];
                gpsPoint point = new gpsPoint(Double.parseDouble(lng),
                        Double.parseDouble(lat),
                        tid,
                        time,
                        0);
                ctx.collect(point);
                //Thread.sleep(100);
            }
            System.out.println("count: " + count);
        } catch (IOException e) {
            throw new RuntimeException("Generate trajectory error: " + e.getMessage());
        }

    }

    @Override
    public void cancel() {
        running = false;
    }

}

