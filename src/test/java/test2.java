import java.io.IOException;

public class test2 {
    public static void main(String[] args) throws IOException {
          double lng1 = 76;
          double lng2 = 123;
          double lat1 = 21;
          double lat2 = 45;

         double D = 45*Math.sqrt(2)/4;
         System.out.println(D);
        //static double D = 450;

         double deltaLon = D * 360 / (2 * Math.PI * 6371004 * Math.cos((lat1 + lat2) * Math.PI / 360));
         double deltaLat = D * 360 / (2 * Math.PI * 6371004);
         System.out.println(deltaLon);
            System.out.println(deltaLat);
    }
}
