package org.ubcomp.sts.objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Create temporary gps point list data model
 *
 * @author syy
 **/
public class tempPointList implements Serializable {

    //temporary gps point list
    public ArrayList<gpsPoint> pointList;
    //indicates the presence or absence of  stay point
    public boolean hasStayPoint = false;
    //the location of stay point(total)
    public int stayPointStart = 0;
    public int stayPointEnd = 0;
    public int getStayPointFlag = -1;

    public tempPointList() {
    }
    public tempPointList(boolean a) {
        pointList = new ArrayList<gpsPoint>();
    }

    /**
     * add new point to temporary gps point list
     *
     * @p p new gps point
     * @author syy
     **/
    public void add(gpsPoint p) {
        pointList.add(p);
    }

    public ArrayList<gpsPoint> getPointList() {
        return pointList;
    }


    public int getSize() { return pointList.size(); }
}
