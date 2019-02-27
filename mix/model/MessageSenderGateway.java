package model;

import messaging.requestreply.RequestReply;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanReply;
import model.loan.LoanRequest;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageSenderGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;

    public MessageSenderGateway(String channel) {
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
            producer = session.createProducer(destination);
           // consumer = session.createConsumer(clientDestination);
            connection.start();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }
    public ObjectMessage createObjectMessageLoanReply(RequestReply<LoanRequest,LoanReply> requestReply) throws JMSException {
        ObjectMessage msg = session.createObjectMessage(requestReply);
        return msg;
    }
    public ObjectMessage createObjectMessageLoanrequest(RequestReply<LoanRequest, LoanReply> requestReply) throws JMSException {
        ObjectMessage msg = session.createObjectMessage(requestReply.getRequest());
        return msg;
    }
    public ObjectMessage createObjectMessageBank(BankInterestRequest request) throws JMSException {
        ObjectMessage msg = session.createObjectMessage(request);
        return msg;
    }
    public ObjectMessage createObjectMessageBankReply(RequestReply<BankInterestRequest,BankInterestReply> requestReply) throws JMSException {
        ObjectMessage msg = session.createObjectMessage(requestReply);
        return msg;
    }
    public void send(ObjectMessage omsg) throws JMSException {
        producer.send(omsg);
    }
}
