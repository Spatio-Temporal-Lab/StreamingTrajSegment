package org.ubcomp.sts.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Container implements Serializable {
    public Container() { }

    // 迟到点
    public List<GpsPoint> latePoints = new ArrayList<>();
    public List<GpsPoint> unprocessedPoints = new ArrayList<>();
    public GpsPoint currentPoint;
}
