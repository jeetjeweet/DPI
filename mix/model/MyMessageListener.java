package model;

import javax.jms.Message;
import javax.jms.MessageListener;

public class MyMessageListener implements MessageListener {
    @Override
    public void onMessage(Message msg) {

        System.out.println("received: " + msg);
    }
}
