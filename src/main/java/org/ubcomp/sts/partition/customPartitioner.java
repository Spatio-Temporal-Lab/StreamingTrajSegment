package org.ubcomp.sts.partition;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.functions.KeySelector;
import org.ubcomp.sts.objects.gpsPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class customPartitioner<T> implements KeySelector<T, String>, Partitioner<String> {

    private final Map<Integer, Long> partitionKeyCount = new HashMap<>();

    @Override
    public int partition(String key, int numPartitions) {
        int partitionId = 0;
        // 统计每个分区的key数量
        for (int i = 0; i < numPartitions; i++) {
            Long count = partitionKeyCount.get(i);
            if (count == null) {
                count = 0L;
            }
            if (partitionKeyCount.get(partitionId) > count) {
                partitionId = i;
            }
        }
        // 将元素分配到key数量最少的分区
        Long count = partitionKeyCount.get(partitionId);
        if (count == null) {
            count = 0L;
        }
        partitionKeyCount.put(partitionId, count + 1);
        return partitionId;
    }

    @Override
    public String getKey(T value) throws Exception {
        if (value instanceof gpsPoint) {
            // 获取key值
            return ((gpsPoint) value).tid;
        }
        // 如果元素不是gpsPoint类型，则返回随机字符串
        return UUID.randomUUID().toString();
    }
}
