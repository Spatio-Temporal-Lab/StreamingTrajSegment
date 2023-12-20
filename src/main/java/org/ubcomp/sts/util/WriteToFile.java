package org.ubcomp.sts.util;

import org.ubcomp.sts.object.GpsPoint;
import org.ubcomp.sts.object.PointList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToFile {
    public static void save(PointList list, int i, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // add to file
            GpsPoint point = list.getPointList().get(i - 1);
            writer.write(point.lng + "," + point.lat + "," + point.tid + "," + point.ingestionTime + "," + point.isStayPoint);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }
}
