package model;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Observable;
import java.util.Properties;

public class MessageReceiverGateway extends Observable {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer;

     public MessageReceiverGateway(String channel) throws JMSException {
         try {
             Properties props = new Properties();
             props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
             props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

             // connect to the Destination called “myFirstChannel”
             // queue or topic: “queue.myFirstDestination” or “topic.myFirstDestination”
             props.put(("queue."+ channel), channel);
             // props.put(("queue.client"), "client");

             Context jndiContext = new InitialContext(props);
             ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                     .lookup("ConnectionFactory");
             connection = connectionFactory.createConnection();
             session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

             // connect to the sender destination
             destination = (Destination) jndiContext.lookup(channel);
             // clientDestination = (Destination) jndiContext.lookup("client");
             // producer = session.createProducer(destination);
             consumer = session.createConsumer(destination);
             connection.start();
         } catch (NamingException | JMSException e) {
             e.printStackTrace();
         }
         consumer.setMessageListener(msg -> {
             setChanged();
             notifyObservers(msg);
             System.out.println("received a message " + msg);
         });
     }
}
