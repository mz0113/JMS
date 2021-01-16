package rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Worker {
    public static void main(String[] args) {
        Object object = new Object();
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
                        Thread.sleep(5000);
                        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().correlationId(delivery.getProperties().getCorrelationId()).build();
                        Integer i = (Integer.valueOf(message).intValue());
                        channel.basicPublish("",delivery.getProperties().getReplyTo(),basicProperties,String.valueOf((++i).intValue()).getBytes());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        synchronized (object) {
                            object.notify();
                        }
                    }
                }
            };
            while (true){
                synchronized (object) {
                    channel.basicConsume("queue", true, deliverCallback, new CancelCallback() {
                        @Override
                        public void handle(String consumerTag) throws IOException {
                        }
                    });
                    object.wait();
                }
            }

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
