package org.ubcomp.sts.objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Create temporary gps point list data model
 *
 * @author syy
 **/
public class TempPointList implements Serializable {

    //temporary gps point list
    public ArrayList<GpsPoint> pointList;
    //indicates the presence or absence of  stay point
    public boolean hasStayPoint = false;
    //the location of stay point(total)
    public int stayPointStart = 0;
    public int stayPointEnd = 0;
    public int getStayPointFlag = -1;

    public TempPointList() {
    }
    public TempPointList(boolean a) {
        pointList = new ArrayList<GpsPoint>();
    }

    /**
     * add new point to temporary gps point list
     *
     * @p p new gps point
     * @author syy
     **/
    public void add(GpsPoint p) {
        pointList.add(p);
    }

    public ArrayList<GpsPoint> getPointList() {
        return pointList;
    }


    public int getSize() { return pointList.size(); }
}
