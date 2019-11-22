package com.uri;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    public int id;
    private int balance;

    ReentrantLock re;

    public Account(int initialDeposit,int id) {
        balance = initialDeposit;
        this.id = id;
        this.re = new ReentrantLock();
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized void deposit(int amount) {
        balance += amount;
    }

    public boolean withdraw(int amount) {
        synchronized (this) {
            if (balance >= amount) {
                balance = balance-amount;
                return true;
            } else {
                return false;
            }
        }
    }

    // Attention, this code can produce a deadlock, if two (or more) threads
    // transfer money from/to a circle of accounts.
    public boolean transferWithDeadlock (Account dest, int amount) {
        if (withdraw(amount)) {
            dest.deposit(amount);
            return true;
        } else {
            return false;
        }
    }

    // Idead for a deadlock prevention. Compare the accounts and always lock
    // the `smaller` account first. This realtes to having one philosopher
    // taking its sticks in reverse order.
    public boolean transfer(Account dest, int amount) {
        if (dest.id>this.id) {  // This comparison does not work yet, correct it.
            synchronized(dest) {
                synchronized(this) {
                    if (withdraw(amount)) {
                        dest.deposit(amount);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            synchronized(this) {
                synchronized(dest) {
                    if (withdraw(amount)) {
                        dest.deposit(amount);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

    }

    public static boolean collectedTransfer(Account destination,Account a1,int amount1,Account a2,int amount2){
        int collectedMoney=0;
        Account[] accounts = new Account[3];
        if(destination.id<a1.id){
            synchronized (destination){
                synchronized (a1){
                    synchronized (a2){
                        if (a1.withdraw(amount1)){
                            collectedMoney = collectedMoney + amount1;
                        }else {
                            System.out.println("do not have enough balance to transfer");
                        }
                        if (a2.withdraw(amount2)){
                            collectedMoney = collectedMoney + amount2;
                        }else {
                            System.out.println("do not have enough balance to transfer");
                        }
                        destination.deposit(collectedMoney);
                    }
                }
            }
        }
        else {
            if(a1.id < a2.id){

                synchronized (a1){
                    synchronized (destination){
                        synchronized (a2){
                            if (a1.withdraw(amount1)){
                                collectedMoney = collectedMoney + amount1;
                            }else {
                                System.out.println("do not have enough balance to transfer");
                            }
                            if (a2.withdraw(amount2)){
                                collectedMoney = collectedMoney + amount2;
                            }else {
                                System.out.println("do not have enough balance to transfer");
                            }
                            destination.deposit(collectedMoney);
                        }
                    }
                }
            }else{
                synchronized (a2){
                    synchronized (destination){
                        synchronized (a1){
                            if (a1.withdraw(amount1)){
                                collectedMoney = collectedMoney + amount1;
                            }else {
                                System.out.println("do not have enough balance to transfer");
                            }
                            if (a2.withdraw(amount2)){
                                collectedMoney = collectedMoney + amount2;
                            }else {
                                System.out.println("do not have enough balance to transfer");
                            }
                            destination.deposit(collectedMoney);
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean transferWithReentrantLock(Account dest, int amount){
        try{
            if(!re.tryLock()){
                return false;
            }

            if(!dest.re.tryLock()){
                re.unlock();
                return false;
            }

            if (withdraw(amount)) {
                dest.deposit(amount);
                dest.re.unlock();
                re.unlock();
                return true;
            } else {
                dest.re.unlock();
                re.unlock();
                return false;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            re.unlock();
            return true;
        }
    }
}
