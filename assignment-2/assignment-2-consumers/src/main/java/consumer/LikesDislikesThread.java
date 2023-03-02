package consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import model.Swipe;

/**
 * Thread class for the likes dislikes consumer
 */
public class LikesDislikesThread implements Runnable {

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

  /**
   * Adds to a swiper's likes and dislikes in a hash map. Uses an array to hold
   * the count, index 0 is dislikes (left), index 1 is likes (right)
   * @param message the message with the swipe info that we get from RabbitMQ
   */
  private void addToLikesDislikes(String message) {
    Gson gson = new Gson();
    Swipe swipe = gson.fromJson(message, Swipe.class);
    int[] likesDislikes;
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
  }
}
