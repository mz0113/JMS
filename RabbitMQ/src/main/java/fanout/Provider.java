package fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Provider {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();){
            channel.exchangeDeclare("logExchange", BuiltinExchangeType.DIRECT);
            String message = "Hello RabbitMQ";
            channel.basicPublish("logExchange","level3",null,message.getBytes());
            System.out.println("sent ok");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
