package org.ubcomp.sts.util;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.ubcomp.sts.object.GpsPoint;

public class CalculateDistance {
    public static double calDistance(GpsPoint p1, GpsPoint p2) {

        /*double radLat1 = calRad(p1.lat);
        double radLon1 = calRad(p1.lng);
        double radLat2 = calRad(p2.lat);
        double radLon2 = calRad(p2.lng);

        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        double addAll = Math.pow(deltaLat, 2) + Math.pow(deltaLon, 2);

        //System.out.println(Math.sqrt(addAll) * 6378.137 * 1000);
        return Math.sqrt(addAll) * 6378.137 * 1000 ;*/

        double lat1 = p1.lat;
        double lat2 = p2.lat;
        double lng1 = p1.lng;
        double lng2 = p2.lng;
        double radLat11 = calRad(lat1);
        double radLat21 = calRad(lat2);
        double a = radLat11 - radLat21;
        double b = calRad(lng1) - calRad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat11) * Math.cos(radLat21)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        //System.out.println(s);
        return s;

        /* CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        Coordinate coorDstP1 = new Coordinate();
        Coordinate coorDstP2 = new Coordinate();
        JTS.transform(new Coordinate(p1.lat, p1.lng), coorDstP1, transform);
        JTS.transform(new Coordinate(p2.lat, p2.lng), coorDstP2, transform);
        System.out.println(Math.sqrt(Math.pow(p1.lat - p2.lat, 2) + Math.pow(p1.lng - p2.lng, 2)) * 100000);
        System.out.println();*/
        //return Math.sqrt(Math.pow((p1.lat - p2.lat), 2) + Math.pow((p1.lng - p2.lng), 2)) * 100000;

    }

    public static double calRad(double a) {
        return a * Math.PI / 180.0;
    }
}
