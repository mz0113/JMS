import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Provider1 {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();){
            channel.queueDeclare("queue",false,false,false,null);
            String message = "Hello RabbitMQ";
            channel.basicPublish("","queue",null,message.getBytes());
            System.out.println("sent ok");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
