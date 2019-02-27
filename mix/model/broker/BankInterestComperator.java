package model.broker;

import model.bank.BankInterestReply;

import java.util.Comparator;

public class BankInterestComperator implements Comparator<BankInterestReply> {
    @Override
    public int compare(BankInterestReply o1, BankInterestReply o2) {
        if (o1.getInterest() < o2.getInterest())
            return 1; // lowest value first
        if (o1.getInterest() == o2.getInterest())
            return 0;
        return -1;
    }
}
