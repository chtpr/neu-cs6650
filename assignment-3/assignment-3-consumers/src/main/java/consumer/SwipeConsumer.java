package consumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dao.SwipeDao;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Consumer class for swipes
 */
public class SwipeConsumer {

  private static final String AWS_PUBLIC = "34.216.20.114";
  private static final String AWS_PRIVATE = "172.31.25.91";
  private final static int NUM_THREADS = 50;

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(AWS_PRIVATE);
    factory.setUsername("test");
    factory.setPassword("test");
    Connection connection = factory.newConnection();
    SwipeDao swipeDao = new SwipeDao();
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(new ConsumerThread(connection, swipeDao));
    }
  }
}
