import org.apache.hadoop.security.authentication.util.ZKSignerSecretProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.*;

public class SimpleConsumer {

    public static void main(String[] args) throws InterruptedException {
        SparkConf conf= new SparkConf();
        conf.setMaster("local");
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        conf.setAppName("message listing");

        JavaStreamingContext streamingContext= new JavaStreamingContext(conf, Durations.seconds(10));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "group_test2");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("mytopic");

        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));
        JavaDStream<String> data = messages.map(v -> { return v.value(); });

        data.print();

        streamingContext.start();
        streamingContext.awaitTermination();

    }

}
