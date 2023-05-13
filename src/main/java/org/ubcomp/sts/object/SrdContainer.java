package org.ubcomp.sts.object;

import java.util.ArrayList;
import java.util.List;

/**
 * @author syy
 */
public class SrdContainer extends Container {

    public SrdContainer() { }

    public int i = 1;
    public double radius = 0;
    public List<GpsPoint> centroids = new ArrayList<>();
    public List<Integer> cutofs = new ArrayList<>();
    public int nPoints = 1;
    public GpsPoint currentCentroid;
}
