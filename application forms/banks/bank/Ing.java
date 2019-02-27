package bank;

import messaging.requestreply.RequestReply;
import model.MessageReceiverGateway;
import model.MessageSenderGateway;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class Ing extends JFrame implements Observer {
//	private static Connection connection; // to connect to the JMS
//	private static Session session; // session for creating consumers
//
//	private static Destination receiveDestination;
//	private static Destination produceDestination;
//	private static MessageConsumer consumer; // for receiving messages
//	private static MessageProducer producer;
	private static MessageReceiverGateway messageReceiverGateway;
	private static MessageSenderGateway messageSenderGateway;
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tfReply;
	private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");

					Ing frame = new Ing();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Ing() throws JMSException {
		setConnection();
		setTitle("JMS Bank - ING");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 700, 450, 300);
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
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		JList<RequestReply<BankInterestRequest, BankInterestReply>> list = new JList<RequestReply<BankInterestRequest, BankInterestReply>>(listModel);
		scrollPane.setViewportView(list);
		
		JLabel lblNewLabel = new JLabel("type reply");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		tfReply = new JTextField();
		GridBagConstraints gbc_tfReply = new GridBagConstraints();
		gbc_tfReply.gridwidth = 2;
		gbc_tfReply.insets = new Insets(0, 0, 0, 5);
		gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfReply.gridx = 1;
		gbc_tfReply.gridy = 1;
		contentPane.add(tfReply, gbc_tfReply);
		tfReply.setColumns(10);
		
		JButton btnSendReply = new JButton("send reply");
		btnSendReply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RequestReply<BankInterestRequest, BankInterestReply> rr = list.getSelectedValue();
				double interest = Double.parseDouble((tfReply.getText()));
				BankInterestReply reply = new BankInterestReply(interest,"ING");
				System.out.println("rr: " + rr );
				System.out.println("reply: "+ reply);
				if (rr!= null && reply != null){
					rr.setReply(reply);
	                list.repaint();
					// todo: sent JMS message with the reply to Loan Broker
					try {
						System.out.println("send message" + rr);
						messageSenderGateway.send(messageSenderGateway.createObjectMessageBankReply(rr));
					} catch (JMSException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
		gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSendReply.gridx = 4;
		gbc_btnSendReply.gridy = 1;
		contentPane.add(btnSendReply, gbc_btnSendReply);
	}

	private void setConnection() throws JMSException {
		messageReceiverGateway = new MessageReceiverGateway("ing");
		messageReceiverGateway.addObserver(this);
		messageSenderGateway = new MessageSenderGateway("LoanBroker");
	}


//	private void setMessageListener() {
//		try {
//			messageReceiverGateway.setListener(new MessageListener() {
//				@Override
//				public void onMessage(Message msg) {
//					System.out.println("received message: " + msg);
//					try {
//						BankInterestRequest lr = (BankInterestRequest) ((ObjectMessage)msg).getObject();
//						add(lr);
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
	public void add(BankInterestRequest bir){
		listModel.addElement(new RequestReply<>(bir,null));
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("update");
		BankInterestRequest lr = null;
		try {
			lr = (BankInterestRequest) ((ObjectMessage)arg).getObject();
			add(lr);
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
