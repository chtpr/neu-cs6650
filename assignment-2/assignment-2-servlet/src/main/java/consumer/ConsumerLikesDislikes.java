package consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Swipe;

public class ConsumerLikesDislikes {

  private static final String AWS_PUBLIC = "35.92.116.67";
  private static final String AWS_PRIVATE = "172.31.25.91";
  private final static int NUM_THREADS = 100;

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(AWS_PUBLIC);
    factory.setUsername("test");
    factory.setPassword("test");
    Connection connection = factory.newConnection();
    ConcurrentHashMap<String, int[]> likesDislikesMap = new ConcurrentHashMap<>();
    ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
    for (int j = 0; j < NUM_THREADS; j++) {
      threadPool.execute(new LikesDislikesThread(connection, likesDislikesMap));
    }
  }

  public static class LikesDislikesThread implements Runnable {

    private static final String QUEUE_NAME = "LikesDislikes";
    private static final String EXCHANGE_NAME = "SwipeExchange";

    private Connection connection;
    private ConcurrentHashMap<String, int[]> map;

    public LikesDislikesThread(Connection connection, ConcurrentHashMap<String, int[]> map) {
      this.connection = connection;
      this.map = map;
    }

    @Override
    public void run() {
      try {
        consume();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void consume() throws IOException {
      Channel channel = connection.createChannel();

      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
      channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        addToLikesDislikes(message);
      };
      channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
      });
    }

    private void addToLikesDislikes(String message) {
      Gson gson = new Gson();
      Swipe swipe = gson.fromJson(message, Swipe.class);
      int[] likesDislikes;
      // index 0 is dislikes, index 1 is likes
      if (!map.containsKey(swipe.getSwiper())) {
        likesDislikes = new int[2];
      } else {
        likesDislikes = map.get(swipe.getSwiper());
      }
      if (swipe.getSwipeDirection().equals("right")) {
        likesDislikes[1]++;
      } else {
        likesDislikes[0]++;
      }
      map.put(swipe.getSwiper(), likesDislikes);
//    System.out.println(map.get(swipe.getSwiper())[1]);
    }
  }
}
