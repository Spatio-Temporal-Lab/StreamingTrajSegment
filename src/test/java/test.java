import org.ubcomp.sts.dataSource.sourceRel3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class test {
    public static void main(String[] args) throws IOException {
        double minlng = 999;
        double maxlng = 0;
        double minlat = 999;
        double maxlat = 0;
        try (InputStream in = sourceRel3.class.getClassLoader().getResourceAsStream("wh1000");
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)))) {
            String trajStr;
            int count =0;
            while ((trajStr = br.readLine()) != null) {
                count++;
                String[] result = trajStr.replace("\'","")
                        .replace("[","").replace("]","").
                        replace(" ","").split(",");
                String t1 = result[0];
                String t2 = result[1];
                String lng = result[4];
                String lat = result[5];
                String tid = result[3];
                double lng1 = Double.parseDouble(lng);
                double lat1 = Double.parseDouble(lat);
                if (lng1 < minlng) {
                    minlng = lng1;
                }
                if (lng1 > maxlng) {
                    maxlng = lng1;
                }
                if (lat1 < minlat) {
                    minlat = lat1;
                }
                if (lat1 > maxlat) {
                    maxlat = lat1;
                }
            }
            System.out.println("count: "+count);
            System.out.println("minlng: "+minlng);
            System.out.println("maxlng: "+maxlng);
            System.out.println("minlat: "+minlat);
            System.out.println("maxlat: "+maxlat);
        } catch (IOException e) {
            throw new RuntimeException("Generate trajectory error: " + e.getMessage());
        }
    }
}
