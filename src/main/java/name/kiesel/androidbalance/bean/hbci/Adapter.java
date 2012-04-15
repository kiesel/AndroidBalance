/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.bean.hbci;

import name.kiesel.androidbalance.bean.AccountBean;
import name.kiesel.androidbalance.bean.TransactionBean;
import name.kiesel.hbci.impl.HbciAccount;
import name.kiesel.hbci.impl.HbciTransaction;
import name.kiesel.hbci.impl.HbciVersion;

/**
 *
 * @author alex
 */
public class Adapter {

    public static HbciAccount accountFromBean(AccountBean bean) {
        HbciAccount a = new HbciAccount(bean.getNumber(), bean.getBankCode());
        a.setUrl(bean.getHbciUrl());
        a.setVersion(HbciVersion.V300); // FIXME
        a.getCredentials().setUserId(bean.getUserId());
        a.getCredentials().setPin(bean.getPin());

        return a;
    }

    public static TransactionBean transactionFromHbci(HbciTransaction t) {
        TransactionBean bean = new TransactionBean();

        bean.setBalance(t.getBalance().getAmount());
        bean.setCurrency(t.getBalance().getCurrency());
        bean.setBookingdate(t.getBookingDate());
        bean.setValuta(t.getValuta());

        if (null != t.getCounterAccount()) {
            bean.setOtherAccountNumber(t.getCounterAccount().getAccountNumber());
            bean.setOtherBankCode(t.getCounterAccount().getBankCode());
        }
        return bean;
    }
}
