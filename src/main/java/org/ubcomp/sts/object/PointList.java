package org.ubcomp.sts.object;

import java.util.ArrayList;

/**
 * @author syy
 **/
public class PointList{

    //temporary gps point list
    public ArrayList<GpsPoint> pointList = new ArrayList<>();;
    //indicates the presence or absence of  stay point
    public boolean hasStayPoint = false;
    //the location of stay point(total), inclusive
    public int stayPointStartGlobalIndex = -1;
    public int stayPointEndGlobalIndex = -1;
    public int stayPointEndLocalIndex = -1;

    public PointList() { }

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
