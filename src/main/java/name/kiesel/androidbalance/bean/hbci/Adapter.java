/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.bean.hbci;

import name.kiesel.androidbalance.bean.AccountBean;
import name.kiesel.hbci.impl.HbciAccount;
import name.kiesel.hbci.impl.HbciVersion;

/**
 *
 * @author alex
 */
public class Adapter {
    public static HbciAccount accountFromBean(AccountBean bean) {
        HbciAccount a= new HbciAccount(bean.getNumber(), bean.getBankCode());
        a.setUrl(bean.getHbciUrl());
        a.setVersion(HbciVersion.V300); // FIXME
        a.getCredentials().setUserId(bean.getUserId());
        a.getCredentials().setPin(bean.getPin());
        
        return a;
    }
}
