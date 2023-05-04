package org.ubcomp.sts.objects;

import java.util.List;

/**
 * Create trajectory data model
 *
 * @author syy
 **/

public class trajectory {

    public String tid;
    public List<gpsPoint> gpslist;

    public long starttime;
    public long endtime;

    public trajectory() {
    }

    public trajectory(String tid, List<gpsPoint> gpslist, long starttime, long endtime) {
        this.tid = tid;
        this.gpslist = gpslist;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    @Override
    public String toString() {
        return "tid:" + tid + ",gpslist:" + gpslist.toString() +
                ",开始时间:" + starttime + ",结束时间：" + endtime;
    }
}
