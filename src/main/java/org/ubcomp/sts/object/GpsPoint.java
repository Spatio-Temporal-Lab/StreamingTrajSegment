package org.ubcomp.sts.object;

import org.locationtech.jts.geom.Point;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author syy
 **/

public class GpsPoint {

    //经度坐标(x)
    public double lng;
    //纬度坐标(y)
    public double lat;
    //gps点所属移动对象编号
    public String tid;
    //时间戳
    public long ingestionTime;
    public Point point;
    public int lonCol;
    public int latCol;
    public boolean gridFlag = false;
    public long processTime;

    //构造方法
    public GpsPoint() {
    }

    public GpsPoint(double lng, double lat, String tid, String ingestionTime, long a) throws ParseException {
        //GeometryFactory geofact = new GeometryFactory();
        //CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        //CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
        //MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        //Coordinate coorDst =new Coordinate();
        //JTS.transform(new Coordinate(lat, lng),coorDst, transform);
        //point = geofact.createPoint(new Coordinate(coorDst.y, coorDst.x));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.ingestionTime = formatter.parse(ingestionTime).getTime() + a;
        this.tid = tid;
        this.lng = lng;
        this.lat = lat;
        //List<Double> aa = uGird.calUGird(lat,lng);
        //this.LATCOL = aa.get(0);
        //this.LONCOL = aa.get(1);
    }

    public GpsPoint(double lng, double lat, String tid, long ingestionTime, long a) throws ParseException {
        //GeometryFactory geofact = new GeometryFactory();
        //CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        //CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
        //MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        //Coordinate coorDst =new Coordinate();
        //JTS.transform(new Coordinate(lat, lng),coorDst, transform);
        //point = geofact.createPoint(new Coordinate(coorDst.y, coorDst.x));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.ingestionTime = ingestionTime + a;
        this.tid = tid;
        this.lng = lng;
        this.lat = lat;
        //List<Double> aa = uGird.calUGird(lat,lng);
        //this.LATCOL = aa.get(0);
        //this.LONCOL = aa.get(1);
    }

    @Override
    public String toString() {
        return "编号:" + tid + ",时间戳:" + new Timestamp(ingestionTime) + ",经度:" + lng + ",纬度:" + lat;
        //new Timestamp(ingestionTime)
    }


}
