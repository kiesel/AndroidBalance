/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.bean;

import android.content.ContentValues;
import java.util.ArrayList;
import java.util.Date;
import name.kiesel.androidbalance.repo.AccountRepository;

/**
 *
 * @author alex
 */
public class TransactionBean extends AbstractBean {
    private long id= -1;
    private long accountId= -1;
    private double balance;
    private String currency;
    private Date bookingdate;
    private Date valuta;
    private String text;
    private String otherAccountNumber;
    private String otherBankCode;
    private ArrayList<String> usage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
    
    public void setAccount(AccountBean account) {
        this.setAccountId(account.getId());
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(Date bookingdate) {
        this.bookingdate = bookingdate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOtherAccountNumber() {
        return otherAccountNumber;
    }

    public void setOtherAccountNumber(String otherAccountNumber) {
        this.otherAccountNumber = otherAccountNumber;
    }

    public String getOtherBankCode() {
        return otherBankCode;
    }

    public void setOtherBankCode(String otherBankCode) {
        this.otherBankCode = otherBankCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getUsage() {
        return usage;
    }

    public void setUsage(ArrayList<String> usage) {
        this.usage = usage;
    }

    public Date getValuta() {
        return valuta;
    }

    public void setValuta(Date valuta) {
        this.valuta = valuta;
    }

    @Override
    public ContentValues getValues() {
        ContentValues values= new ContentValues();
        
        values.put(AccountRepository.FIELD_TRANSACTION_ACCOUNT, this.getAccountId());
        values.put(AccountRepository.FIELD_TRANSACTION_BALANCE, this.getBalance());
        values.put(AccountRepository.FIELD_TRANSACTION_CURRENCY, this.getCurrency());
        values.put(AccountRepository.FIELD_TRANSACTION_BOOKINGDATE, this.getBookingdate().getTime());
        values.put(AccountRepository.FIELD_TRANSACTION_VALUTA, this.getValuta().getTime());
        values.put(AccountRepository.FIELD_TRANSACTION_TEXT, this.getText());
        values.put(AccountRepository.FIELD_TRANSACTION_OTHERACCOUNT, this.getOtherAccountNumber());
        values.put(AccountRepository.FIELD_TRANSACTION_OTHERBANK, this.getOtherBankCode());
        
        return values;
    }

    @Override
    public String getBeanName() {
        return "Transaction";
    }
    
}
