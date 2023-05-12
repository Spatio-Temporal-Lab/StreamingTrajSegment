package org.ubcomp.sts.object;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author syy
 **/
public class PointList implements Serializable {

    //temporary gps point list
    public ArrayList<GpsPoint> pointList;
    //indicates the presence or absence of  stay point
    public boolean hasStayPoint = false;
    //the location of stay point(total)
    public int stayPointStart = 0;
    public int stayPointEnd = 0;
    public int getStayPointFlag = -1;

    public PointList() {
    }

    public PointList(boolean a) {
        pointList = new ArrayList<>();
    }

    public void add(GpsPoint p) {
        pointList.add(p);
    }

    public ArrayList<GpsPoint> getPointList() {
        return pointList;
    }

    public int getSize() {
        return pointList.size();
    }
}
