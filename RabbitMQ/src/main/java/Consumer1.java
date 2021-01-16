import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Consumer1 {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();){
            channel.queueDeclare("queue",false,false,false,null);
            DeliverCallback deliverCallback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery delivery) throws IOException {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("sleep 6s completed");
                    //channel.basicReject(delivery.getEnvelope().getDeliveryTag(),true);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),true);
                }
            };
            channel.basicConsume("queue", false, deliverCallback, consumerTag -> { });
            System.out.println("received ok");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
