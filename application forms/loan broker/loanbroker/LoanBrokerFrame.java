package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.*;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import messaging.requestreply.RequestReply;
import model.MessageReceiverGateway;
import model.MessageSenderGateway;
import model.MyMessageListener;
import model.bank.*;
import model.broker.BrokerObject;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import org.apache.activemq.broker.Broker;
import org.springframework.messaging.simp.user.DestinationUserNameProvider;
import sun.security.krb5.internal.crypto.Des;


public class LoanBrokerFrame extends JFrame implements Observer {
//	private static Connection connection; // to connect to the JMS
//	private static Session session; // session for creating consumers

//	private static Destination receiveDestination; //reference to a queue/topic destination
//	private static Destination abnamroDestination;
//	private static Destination clientDestination;
//
//	private static MessageProducer loanproducer;
//	private static MessageConsumer consumer; // for receiving messages
//	private static MessageProducer producer;

	private static MessageSenderGateway messageSenderGatewayBank;
	private static MessageSenderGateway messageSenderGatewayING;
	private static MessageSenderGateway messageSenderGatewayRabo;

	private static MessageSenderGateway messageSenderGatewayClient;
	private static MessageReceiverGateway messageReceiverGateway;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;
	private List<MessageSenderGateway> bankList = new ArrayList<MessageSenderGateway>();
	private List<BrokerObject> brokerList;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private LoanRequest getloanRequest(String messageID){
		for (int i = 0; i < listModel.getSize(); i++){
			JListLine rr =listModel.get(i);
			if (rr.getLoanRequest().getID().equals( messageID)){
				return rr.getLoanRequest();
			}
		}

		return null;
	}
//	private void setMessageListener() {
//		try {
//			messageReceiverGateway.setListener(new MessageListener() {
//				@Override
//				public void onMessage(Message msg) {
//					ObjectMessage omsg = null;
//					try {
//						omsg = ((ObjectMessage)msg);
//						System.out.println("received message: " + omsg.getObject());
//					} catch (JMSException e) {
//						e.printStackTrace();
//					}
//					try {
//						if(omsg.getObject() instanceof RequestReply){
//							RequestReply<BankInterestRequest,BankInterestReply> rr = (RequestReply<BankInterestRequest,BankInterestReply>) omsg.getObject();
//							LoanRequest lr = getloanRequest(rr.getRequest().getMessageID());
//							System.out.println("Loabrequestget: " + lr.toString());
//							add(lr, rr.getRequest());
//							add(lr,rr.getReply());
//							messageSenderGatewayClient.send(messageSenderGatewayClient.createObjectMessageLoanReply(new RequestReply<LoanRequest, LoanReply>(lr,new LoanReply(rr.getReply().getInterest(),rr.getReply().getQuoteId()))));
//						}
//						else {
//							try {
//								LoanRequest lr = (LoanRequest) omsg.getObject();
//								System.out.println(omsg.getJMSMessageID());
//								lr.setID(omsg.getJMSMessageID());
//								add(lr);
//								messageSenderGatewayBank.send(messageSenderGatewayBank.createObjectMessageBank(new BankInterestRequest(lr.getAmount(),lr.getTime(), lr.getID())));
//
//							} catch (JMSException e) {
//								e.printStackTrace();
//							}
//						}
//					} catch (JMSException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
//	}
	private void setConnection() throws JMSException {
		brokerList = new ArrayList<>();
		messageSenderGatewayBank = new MessageSenderGateway("abnamro");
		messageSenderGatewayING = new MessageSenderGateway("ing");
		messageSenderGatewayRabo = new MessageSenderGateway("rabobank");
		messageSenderGatewayClient = new MessageSenderGateway("client");
		messageReceiverGateway = new MessageReceiverGateway("LoanBroker");
		messageReceiverGateway.addObserver(this);
	}

	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() throws JMSException {
		setConnection();
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1400, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);

		// setMessageListener();
	}
	
	 private JListLine getRequestReply(LoanRequest request){    
	     
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	   }
	
	public void add(LoanRequest loanRequest){		
		listModel.addElement(new JListLine(loanRequest));		
	}
	

	public void add(LoanRequest loanRequest,BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}


	@Override
	public void update(Observable o, Object arg) {
		ObjectMessage omsg = null;
		try {
			omsg = ((ObjectMessage)arg);
			System.out.println("update: " + omsg.getObject());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		try {
			if(omsg.getObject() instanceof RequestReply){
				RequestReply<BankInterestRequest,BankInterestReply> rr = (RequestReply<BankInterestRequest,BankInterestReply>) omsg.getObject();
				String messageID = rr.getRequest().getMessageID();
				LoanRequest lr = getloanRequest(messageID);
				System.out.println("Loabrequestget: " + lr.toString());
				add(lr, rr.getRequest());
				add(lr,rr.getReply());
				for (BrokerObject bo : brokerList){
					if(bo.getId().equals(messageID)){
						if(bo.add(rr.getReply()) == true){
							messageSenderGatewayClient.send(messageSenderGatewayClient.createObjectMessageLoanReply(
									new RequestReply<LoanRequest, LoanReply>(lr, new LoanReply(bo.getReply().getInterest(),bo.getReply().getQuoteId()))));
//	new RequestReply<LoanRequest, LoanReply>(lr,new LoanReply(rr.getReply().getInterest(),rr.getReply().getQuoteId()))
							break;
						}
						else {
							break;
						}
					}
				}

			}
			else {
				try {
					LoanRequest lr = (LoanRequest) omsg.getObject();
					System.out.println(omsg.getJMSMessageID());
					lr.setID(omsg.getJMSMessageID());
					add(lr);
					checkRules(lr);
//					messageSenderGatewayBank.send(messageSenderGatewayBank.createObjectMessageBank(new BankInterestRequest(lr.getAmount(),lr.getTime(), lr.getID())));

				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void checkRules(LoanRequest lr) throws JMSException {
		int replyamount = 0;
		int amount = lr.getAmount();
		int time = lr.getTime();
		BankInterestRequest request = new BankInterestRequest(amount,time,lr.getID());
		if((amount <= 100000) && (time <= 10)) {
			System.out.println("send to ing");
			messageSenderGatewayING.send(messageSenderGatewayING.createObjectMessageBank(request));
			replyamount ++;
		}
		if((amount <= 3000000) && (amount >= 200000) && (time <= 20)) {
			System.out.println("send to abn amro");
			replyamount++;
			messageSenderGatewayBank.send(messageSenderGatewayBank.createObjectMessageBank(request));
		}
		if((amount <=  250000) && (time <= 15)) {
			System.out.println("send top rabo");
			replyamount++;
			messageSenderGatewayRabo.send(messageSenderGatewayRabo.createObjectMessageBank(request));
		}
		BrokerObject brokerObject = new BrokerObject(replyamount, lr.getID());
		brokerList.add(brokerObject);
	}
}
