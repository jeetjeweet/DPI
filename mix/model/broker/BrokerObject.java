package model.broker;

import model.bank.BankInterestReply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrokerObject {
    private int replyAmount;
    private List<BankInterestReply> replyList;
    private String id;

    public BrokerObject(int amount, String id) {
        replyList = new ArrayList<>();
        this.replyAmount = amount;
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public boolean add(BankInterestReply reply){
        replyList.add(reply);
        System.out.println("Expected returns: " + replyAmount);
        System.out.println("Current Returns: " + replyList.size());
        if(replyList.size() == replyAmount){
            System.out.println("Returning true, sending to client after");
            return true;
        }
        else{
            System.out.println("Returning False, try again");
            return false;
        }
    }
    public BankInterestReply getReply() {
        return Collections.max(replyList, new BankInterestComperator());
    }
}
