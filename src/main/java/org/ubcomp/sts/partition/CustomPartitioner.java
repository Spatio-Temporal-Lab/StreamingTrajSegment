package org.ubcomp.sts.partition;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.functions.KeySelector;
import org.ubcomp.sts.object.GpsPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author syy
 */
public class CustomPartitioner<T> implements KeySelector<T, String>, Partitioner<String> {

    private final Map<Integer, Long> partitionKeyCount = new HashMap<>();

    @Override
    public int partition(String key, int numPartitions) {
        int partitionId = 0;
        for (int i = 0; i < numPartitions; i++) {
            Long count = partitionKeyCount.get(i);
            if (count == null) {
                count = 0L;
            }
            if (partitionKeyCount.get(partitionId) > count) {
                partitionId = i;
            }
        }
        Long count = partitionKeyCount.get(partitionId);
        if (count == null) {
            count = 0L;
        }
        partitionKeyCount.put(partitionId, count + 1);
        return partitionId;
    }

    @Override
    public String getKey(T value) throws Exception {
        if (value instanceof GpsPoint) {
            return ((GpsPoint) value).tid;
        }
        return UUID.randomUUID().toString();
    }
}
