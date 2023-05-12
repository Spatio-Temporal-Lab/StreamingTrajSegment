package org.ubcomp.sts.kafka;

import java.io.*;
import java.util.*;

import org.apache.kafka.clients.producer.*;

/**
 * @author syy
 */
public class KafkaGpsProducer {

    private final static int COUNT_NUM = 100;

    public static void main(String[] args) throws Exception {

        String topicName = "gps_data_topic";
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        // 遍历读取所有文本文件
        for (int i = 1; i <= COUNT_NUM; i++) {
            String fileName = "D:\\stdata\\projects\\sts4\\src\\main\\resources\\taxi.txt";
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            while (line != null) {
                String[] fields = line.split(",");
                String lat = fields[0];
                String lon = fields[1];
                String msg = lat + "," + lon;
                producer.send(new ProducerRecord<>(topicName, msg));
                line = reader.readLine();
            }
            reader.close();
        }

        producer.close();
    }
}
