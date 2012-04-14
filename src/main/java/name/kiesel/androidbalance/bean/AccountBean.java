/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.bean;

import android.content.ContentValues;
import name.kiesel.androidbalance.repo.AccountRepository;

/**
 *
 * @author alex
 */
public class AccountBean extends AbstractBean {
    private static final String BEAN_NAME   = "account";
    
    /* DB fields */
    private long id = -1;
    private String number= null;
    private String bankCode= null;
    private String userId= null;
    private String hbciUrl= null;
    private String hbciVersion= null;
    private String pin= null;
    private String title= null;

    @Override
    public ContentValues getValues() {
        ContentValues values= new ContentValues();
        
        values.put(AccountRepository.FIELD_ACCOUNT_NAME, this.getTitle());
        values.put(AccountRepository.FIELD_ACCOUNT_NUMBER, this.getNumber());
        values.put(AccountRepository.FIELD_ACCOUNT_BANKCODE, this.getBankCode());
        values.put(AccountRepository.FIELD_ACCOUNT_HBCIURL, this.getHbciUrl());
        values.put(AccountRepository.FIELD_ACCOUNT_HBCIVER, this.getHbciVersion());
        values.put(AccountRepository.FIELD_ACCOUNT_USERID, this.getUserId());
        
        return values;
    }

    @Override
    public String getBeanName() {
        return AccountBean.BEAN_NAME;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getHbciUrl() {
        return hbciUrl;
    }

    public void setHbciUrl(String hbciUrl) {
        this.hbciUrl = hbciUrl;
    }

    public String getHbciVersion() {
        return hbciVersion;
    }

    public void setHbciVersion(String hbciVersion) {
        this.hbciVersion = hbciVersion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccountBean other = (AccountBean) obj;
        if ((this.number == null) ? (other.number != null) : !this.number.equals(other.number)) {
            return false;
        }
        if ((this.bankCode == null) ? (other.bankCode != null) : !this.bankCode.equals(other.bankCode)) {
            return false;
        }
        if ((this.userId == null) ? (other.userId != null) : !this.userId.equals(other.userId)) {
            return false;
        }
        if ((this.hbciUrl == null) ? (other.hbciUrl != null) : !this.hbciUrl.equals(other.hbciUrl)) {
            return false;
        }
        if ((this.hbciVersion == null) ? (other.hbciVersion != null) : !this.hbciVersion.equals(other.hbciVersion)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.number != null ? this.number.hashCode() : 0);
        hash = 83 * hash + (this.bankCode != null ? this.bankCode.hashCode() : 0);
        hash = 83 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        hash = 83 * hash + (this.hbciUrl != null ? this.hbciUrl.hashCode() : 0);
        hash = 83 * hash + (this.hbciVersion != null ? this.hbciVersion.hashCode() : 0);
        return hash;
    }
}
