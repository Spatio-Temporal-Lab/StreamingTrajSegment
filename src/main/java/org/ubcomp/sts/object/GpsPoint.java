package org.ubcomp.sts.object;

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
    public String tid;
    //时间戳
    public long ingestionTime;
    public int lonCol;
    public int latCol;
    public Boolean isStayPoint = false;

    //构造方法
    public GpsPoint() {
    }

    public GpsPoint(double lng, double lat, String tid, String ingestionTime, long a) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.ingestionTime = formatter.parse(ingestionTime).getTime() + a;
        this.tid = tid;
        this.lng = lng;
        this.lat = lat;
    }

    public GpsPoint(double lng, double lat, String tid, long ingestionTime, long a) throws ParseException {
        this.ingestionTime = ingestionTime + a;
        this.tid = tid;
        this.lng = lng;
        this.lat = lat;
    }

    public GpsPoint(double lng, double lat, String tid, long ingestionTime, long a, Boolean isStayPoint) throws ParseException {
        this.ingestionTime = ingestionTime + a;
        this.tid = tid;
        this.lng = lng;
        this.lat = lat;
        this.isStayPoint = isStayPoint;

    }

    @Override
    public String toString() {
        return "编号:" + tid + ",时间戳:" + new Timestamp(ingestionTime) + ",经度:" + lng + ",纬度:" + lat;
    }

}
