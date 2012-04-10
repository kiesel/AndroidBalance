/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance;

import android.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import name.kiesel.androidbalance.repo.AccountRepository;
import name.kiesel.hcbi.impl.HbciAccount;

/**
 *
 * @author alex
 */
public class TransactionsListActivity extends Activity {
    private Long accountId= null;
    private AccountRepository repository= null;
    private HbciAccount account= null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.repository= new AccountRepository(this);
        this.repository.open();
        
//        this.setContentView(R.layout.transaction_list);
        
        accountId= null;
        if (null != savedInstanceState) {
            accountId= (Long)savedInstanceState.getSerializable("accountId");
        } else {
            Bundle extras= this.getIntent().getExtras();
            if (null != extras) {
                this.accountId= (Long)extras.getLong("accountId");
            }
        }
        
        this.account= this.repository.byAccountId(this.accountId);
        this.fillData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void fillData() {
//        Cursor c= this.repository.fetchAllTransactionsFor(this.account);
//        this.startManagingCursor(c);
        
        // TODO
    }
    
}
