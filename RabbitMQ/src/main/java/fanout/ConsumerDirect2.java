package fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ConsumerDirect2 {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();){
            String queuqName = channel.queueDeclare().getQueue();
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
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),true);
                }
            };
            channel.exchangeDeclare("logExchange",BuiltinExchangeType.TOPIC);
            channel.queueBind(queuqName,"logExchange","*.level2.*");
            channel.basicConsume(queuqName, false, deliverCallback, consumerTag -> { });
            System.out.println("received ok");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
