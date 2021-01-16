package client;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Client2 {
    public static final String BROKER_URL  = "tcp://localhost:61616";
    public static final String QUEUE = "mq.queue";
    public static final String TOPIC = "mq.topic";
    public static Connection connection=null;
    public static int i=0;

    static{
        try {
            connection =   new ActiveMQConnectionFactory(BROKER_URL).createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) {
       // receive();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    MessageConsumer messageConsumer = session.createConsumer(session.createTopic(TOPIC));
                    messageConsumer.setMessageListener(new MessageListener() {
                        public void onMessage(Message message) {
                            try {
                                System.out.println(((TextMessage) message).getText());
                                i++;
                            } catch (JMSException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void receive() throws JMSException {
        connection.start();
        Session session =  connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        MessageConsumer messageConsumer = session.createConsumer(session.createQueue(QUEUE));
        messageConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                try {
                    System.out.println(((TextMessage)message).getText());
                    i++;
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
