package org.ubcomp.sts.objects;

import org.ubcomp.sts.tlof.streamLOF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class tempFlag implements Serializable {

    public tempFlag(){}

    public int count = 0;
    public List<gpsPoint> latePoints = new ArrayList<>();
    public List<gpsPoint> tempPoints = new ArrayList<>();
    public gpsPoint tempPoint ;

    public int i = 1;
    public double radius = 0;
    public List<gpsPoint> centroids = new ArrayList<>();
    public List<Integer> cutofs = new ArrayList<>();
    public int nPoints = 1;
    public gpsPoint currentCentroid;

    public streamLOF lof = new streamLOF(10, 50);
}
