package org.ubcomp.sts.datasource;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.ubcomp.sts.object.GpsPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * @author syy
 **/

public class SourceInfinity implements SourceFunction<GpsPoint> {

    private static final int NUM_COUNT = 10000000;

    @Override
    public void run(SourceContext<GpsPoint> ctx) throws Exception {

        try (InputStream in = SourceInfinity.class.getClassLoader().getResourceAsStream("whsmall.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))) {
            String trajStr;
            int count = 0;
            int num = 0;

            while ((trajStr = br.readLine()) != null && num <= NUM_COUNT ) {
                num++;
                count++;
                String[] result = trajStr.replace("'", "")
                        .replace("[", "").replace("]", "")
                        .replace(" ", "").split(",");
                String t1 = result[0];
                String t2 = result[1];
                String lng = result[4];
                String lat = result[5];
                String tid = result[3];

                String time = t1.substring(0, 4) + "-" + t1.substring(4, 6) + "-" + t1.substring(6, 8) + " " + t2.substring(0, 2) + ":" + t2.substring(2, 4) + ":" + t2.substring(4, 6);
                GpsPoint point = new GpsPoint(Double.parseDouble(lng),
                        Double.parseDouble(lat),
                        tid,
                        time,
                        0);

                ctx.collect(point);
                Thread.sleep(100000);
            }
            System.out.println("count: " + count);
        } catch (IOException e) {
            throw new RuntimeException("Generate trajectory error: " + e.getMessage());
        }


    }

    @Override
    public void cancel() {
    }

}

