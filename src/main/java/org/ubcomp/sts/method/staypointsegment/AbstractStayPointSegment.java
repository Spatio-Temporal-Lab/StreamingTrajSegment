package org.ubcomp.sts.method.staypointsegment;

import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStayPointSegment implements Serializable {
    protected PointList pointList;
    protected double maxD;
    protected long minT;

    public AbstractStayPointSegment(PointList pointList, double maxD, long minT) {
        this.pointList = pointList;
        this.maxD = maxD;
        this.minT = minT;
    }

    public abstract void processWithStayPoints();

    public abstract void processWithoutStayPoints();

    protected void breakStayPoint(PointList pointList) {
        List<GpsPoint> list = pointList.getPointList().subList(0,pointList.stayPointEndLocalIndex);
        pointList.pointList = new ArrayList<>(pointList.getPointList()
            .subList(pointList.stayPointEndLocalIndex, pointList.getSize()));
        pointList.hasStayPoint = false;
        pointList.stayPointEndLocalIndex = -1;
        String filePath = "output.txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            for (GpsPoint gpsPoint : list) {
                String line = gpsPoint.toString();
                writer.write(line);
                writer.write(",");
            }
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("写入文件时发生错误：" + e.getMessage());
        }
    }

    protected void mergeStayPoint(PointList pointList) {
        int mergeListSize = pointList.getSize() - pointList.stayPointEndLocalIndex;
        pointList.stayPointEndGlobalIndex = pointList.stayPointEndGlobalIndex + mergeListSize;
        pointList.stayPointEndLocalIndex = pointList.getSize();
    }

    protected void exactStayPoint(PointList pointList, int currentIndex) {
        pointList.stayPointStartGlobalIndex += currentIndex + 2;
        pointList.hasStayPoint = true;
        pointList.pointList = new ArrayList<>(
            pointList.getPointList().subList(currentIndex + 1, pointList.getSize()));
        pointList.stayPointEndGlobalIndex = pointList.stayPointStartGlobalIndex + pointList.getSize() - 1;
        pointList.stayPointEndLocalIndex = pointList.getSize();
    }
}
