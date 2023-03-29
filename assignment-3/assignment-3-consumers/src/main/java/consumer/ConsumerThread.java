package consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;
import dao.SwipeDao;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import model.Swipe;

/**
 * Thread class for the likes dislikes consumer
 */
public class ConsumerThread implements Runnable {

  private static final String QUEUE_NAME = "LikesDislikes";
  private static final String EXCHANGE_NAME = "SwipeExchange";

  private Connection connection;
  private SwipeDao swipeDao;

  private static final Gson gson = new Gson();

  public ConsumerThread(Connection connection, SwipeDao swipeDao) {
    this.connection = connection;
    this.swipeDao = swipeDao;
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
    final Channel channel = connection.createChannel();
    boolean autoAck = false;
    AtomicBoolean multipleAcks = new AtomicBoolean(false);
    List<Swipe> swipes = Collections.synchronizedList(new ArrayList<>());
    channel.basicQos(100);
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
//    for (int i = 0; i < 100; i++) {
//      int j = i;
//      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//        Swipe swipe = gson.fromJson(message, Swipe.class);
//        swipes.add(swipe);
//        if (j % 99 == 0) {
//          addListToSwipeData(swipes);
//          multipleAcks.set(true);
//          swipes.clear();
//        } else {
//          multipleAcks.set(false);
//        }
//        long deliveryTag = delivery.getEnvelope().getDeliveryTag();
//        channel.basicAck(deliveryTag, multipleAcks.get());
//      };
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      Swipe swipe = gson.fromJson(message, Swipe.class);
      swipes.add(swipe);
      if (swipes.size() >= 100) {
        addListToSwipeData(swipes);
        multipleAcks.set(true);
        swipes.clear();
      } else {
        multipleAcks.set(false);
      }
      long deliveryTag = delivery.getEnvelope().getDeliveryTag();
      channel.basicAck(deliveryTag, multipleAcks.get());
    };
    channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {});
//    channel.basicConsume(QUEUE_NAME, autoAck, "a-consumer-tag",
//        new DefaultConsumer(channel) {
//          @Override
//          public void handleDelivery(String consumerTag,
//              Envelope envelope,
//              AMQP.BasicProperties properties,
//              byte[] body)
//              throws IOException
//          {
//            String message = new String(body, StandardCharsets.UTF_8);
//            Swipe swipe = gson.fromJson(message, Swipe.class);
//            swipes.add(swipe);
//            if (swipes.size() >= 100) {
//              addListToSwipeData(swipes);
//              channel.basicAck(envelope.getDeliveryTag(), true);
//              swipes.clear();
//            } else {
//              channel.basicAck(envelope.getDeliveryTag(), false);
//            }
//          }
//        });
  }

  /**
   * Adds to a swiper's likes and dislikes in a hash map. Uses an array to hold
   * the count, index 0 is dislikes (left), index 1 is likes (right)
   * @param message the message with the swipe info that we get from RabbitMQ
   */
  private void addToSwipeData(String message) {
    Swipe swipe = gson.fromJson(message, Swipe.class);
    swipeDao.createSwipe(swipe);
//    System.out.println(swipe.getSwipeDirection() + " " + swipe.getSwiper() + " " + swipe.getSwipee());
  }

  private void addListToSwipeData(List<Swipe> swipes) {
    swipeDao.createSwipes(swipes);
  }
}
