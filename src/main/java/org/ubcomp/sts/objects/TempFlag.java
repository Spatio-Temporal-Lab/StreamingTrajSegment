package org.ubcomp.sts.objects;

import org.ubcomp.sts.tlof.StreamLOF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TempFlag implements Serializable {

    public TempFlag(){}

    public int count = 0;
    public List<GpsPoint> latePoints = new ArrayList<>();
    public List<GpsPoint> tempPoints = new ArrayList<>();
    public GpsPoint tempPoint ;

    public int i = 1;
    public double radius = 0;
    public List<GpsPoint> centroids = new ArrayList<>();
    public List<Integer> cutofs = new ArrayList<>();
    public int nPoints = 1;
    public GpsPoint currentCentroid;

    public StreamLOF lof = new StreamLOF(10, 50);
}
