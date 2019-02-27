package model.bank;

import java.io.Serializable;

/**
 *
 * This class stores all information about an request from a bank to offer
 * a loan to a specific client.
 */
public class BankInterestRequest implements Serializable {

    private int amount; // the requested loan amount
    private int time; // the requested loan period
    private String messageID;

    public BankInterestRequest() {
        super();
        this.amount = 0;
        this.time = 0;
    }

    public BankInterestRequest(int amount, int time, String messageID) {
        super();
        this.amount = amount;
        this.time = time;
        this.messageID = messageID;
    }

    public void setMessageID(String messageid) {this.messageID = messageid;}

    public String getMessageID() {return this.messageID; }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return " amount=" + amount + " time=" + time;
    }
}
