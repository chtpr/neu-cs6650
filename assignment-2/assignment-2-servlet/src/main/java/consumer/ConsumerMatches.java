package consumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConsumerMatches {
  private final static String QUEUE_NAME = "SwipeQueue";
  private static final String LOCAL_HOST = "localhost";
  private static final String AWS_HOST = "34.217.206.210";
  private final static int NUM_THREADS = 100;

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(AWS_HOST);
    factory.setUsername("test");
    factory.setPassword("test");
    Connection connection = factory.newConnection();
    ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(new MatchListThread(connection, map));
    }
  }
}
