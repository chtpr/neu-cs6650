package consumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Consumer class for match lists
 */
public class ConsumerMatches {

  private static final String AWS_PUBLIC = "35.88.142.170";
  private static final String AWS_PRIVATE = "172.31.25.91";
  private final static int NUM_THREADS = 100;

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(AWS_PRIVATE);
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
