package consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import model.Swipe;

public class LikesDislikesThread implements Runnable {

  private static final String QUEUE_NAME = "SwipeQueue";
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
//    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
    channel.basicQos(1);
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
    addToLikesDislikes(message);
//      System.out.println(" [x] Received '" + message + "'");
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
