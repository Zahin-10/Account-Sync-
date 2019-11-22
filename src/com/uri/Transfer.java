package com.uri;

import java.util.concurrent.locks.ReentrantLock;

public class Transfer extends Thread {
    Account a1;
    Account a2;
    Account a3;
    int transferBal;

    Transfer(Account a1,Account a2, Account a3, int value){
        this.a2 = a1;
        this.a1 = a2;
        this.transferBal = value;
        this.a3 = a3;
    }

    public void run(){
        while (true){
            //myAccount.transferWithDeadlock(Destination,transferBal);
            //System.out.println("Account ID: "+myAccount.id+ " "+myAccount.getBalance());
            //Account.collectedTransfer(a3,a1,10,a2,15);

            a1.transferWithReentrantLock(a2, 500);
            System.out.println(a3.getBalance());
        }
    }
}
