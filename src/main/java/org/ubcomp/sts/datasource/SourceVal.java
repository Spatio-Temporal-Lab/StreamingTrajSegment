package org.ubcomp.sts.datasource;

import com.alibaba.fastjson.JSON;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.ubcomp.sts.object.GpsPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * Create data source
 *
 * @author syy
 **/

public class SourceVal implements SourceFunction<GpsPoint> {


    @Override
    public void run(SourceContext<GpsPoint> ctx) throws Exception {
        try (
                InputStream in = SourceVal.class.getClassLoader()
                        .getResourceAsStream("data.txt");
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(Objects.requireNonNull(in))
                )
        ) {
            String trajStr = br.readLine();
            String correctStr = trajStr.replaceFirst("\\[", "[\"").replaceFirst(",", "\",");
            List<String> result = JSON.parseArray(correctStr, String.class);
            String tid = result.get(0);
            List<String> pointsStrList = JSON.parseArray(result.get(1), String.class);
            System.out.println(pointsStrList.size());
            for (int i = 0; i < 1; i++) {
                for (String p : pointsStrList) {
                    List<String> pList = JSON.parseArray(p, String.class);
                    GpsPoint point = new GpsPoint(Double.parseDouble(pList.get(1)),
                            Double.parseDouble(pList.get(2)),
                            tid,
                            pList.get(0),
                            0);
                    ctx.collect(point);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Generate trajectory error: " + e.getMessage());
        }
    }

    @Override
    public void cancel() {

    }

}
