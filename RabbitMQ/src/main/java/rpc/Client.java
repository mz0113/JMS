package rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class Client {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();){
            channel.queueDeclare("queue",false,false,false,null);
            String replyTo = channel.queueDeclare().getQueue();
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().correlationId("hahaha").replyTo(replyTo).build();
            String message = "4";
            channel.basicPublish("","queue",basicProperties,message.getBytes());
            System.out.println("sent ok");
            BlockingQueue<String> blockingQueue = new ArrayBlockingQueue(1);

            //basicConsume(queueName,isAck,DeliverCallback,CancelCallBack)
            //basicPublish(exchange,rountingKey,basicProperties,message)

            //The empty string denotes the default or nameless exchange:
            //          messages are routed to the queue with the <name> specified by routingKey, if it exists.

                String ctag = channel.basicConsume(replyTo, true, new DeliverCallback() {
                @Override
                public void handle(String s, Delivery delivery) throws IOException {
                    //发送RPC请求后，阻塞到队列中
                    if (delivery.getProperties().getCorrelationId().equals("hahaha")) {
                        blockingQueue.add(new String(delivery.getBody(),"UTF-8"));
                    }
                }
            }, new CancelCallback() {
                @Override
                public void handle(String s) throws IOException {
                    System.out.println("我被停止了？");
                }
            });
            String result = blockingQueue.take();
            System.out.println(result);
            channel.basicCancel(ctag);
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
