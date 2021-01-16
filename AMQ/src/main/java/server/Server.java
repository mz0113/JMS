package server;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Server {
    public static final String BROKER_URL  = "tcp://localhost:61616";
    public static final String QUEUE = "mq.queue";
    public static final String TOPIC = "mq.topic";
    public static Connection connection=null;

    static {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String agrs[]) throws JMSException {
        for (int i = 0; i < 20 ; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Session session  = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                        MessageProducer messageProducer = session.createProducer(session.createTopic(TOPIC));
                        TextMessage textMessage = session.createTextMessage();
                        textMessage.setText("Hello This World");
                        messageProducer.send(textMessage);
                        messageProducer.close();
                        session.close();
                        //connection.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(2500);
            connection.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void send(String msg) throws JMSException {
        connection.start();
        Session session  = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        MessageProducer messageProducer = session.createProducer(session.createQueue(QUEUE));
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(msg);
        messageProducer.send(textMessage);
        messageProducer.close();
        session.close();
        connection.close();
    }
}
