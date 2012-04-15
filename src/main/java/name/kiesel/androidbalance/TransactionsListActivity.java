/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Iterator;
import name.kiesel.androidbalance.bean.AccountBean;
import name.kiesel.androidbalance.bean.TransactionBean;
import name.kiesel.androidbalance.bean.hbci.Adapter;
import name.kiesel.androidbalance.repo.AccountRepository;
import name.kiesel.hbci.Transaction;
import name.kiesel.hbci.Transactions;
import name.kiesel.hbci.impl.HbciAccount;
import name.kiesel.hbci.impl.HbciSession;
import name.kiesel.hbci.impl.HbciTransaction;

/**
 *
 * @author alex
 */
public class TransactionsListActivity extends Activity {

    private static final String TAG = "TransactionsListActivity";
    public static final String ACCOUNTID = "_id";
    private static final int OPT_FETCH_TRANSACTIONS = 0;
    private Long accountId = null;
    private AccountRepository repository = null;
    private AccountBean account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.repository = new AccountRepository(this);
        this.repository.open();

        this.setContentView(R.layout.transaction_list);

        accountId = null;
        if (null != savedInstanceState) {
            accountId = (Long) savedInstanceState.getSerializable(ACCOUNTID);
        } else {
            Bundle extras = this.getIntent().getExtras();
            if (null != extras) {
                this.accountId = (Long) extras.getLong(ACCOUNTID);
            }
        }

        this.account = this.repository.findAccountById(this.accountId);
        this.fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, OPT_FETCH_TRANSACTIONS, 0, R.string.transactions_menu_fetch);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPT_FETCH_TRANSACTIONS: {
                this.fetchTransactions();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillData() {
//        Cursor c= this.repository.fetchAllTransactionsFor(this.account);
//        this.startManagingCursor(c);
        // TODO
    }

    private void fetchTransactions() {
        if (null == this.account.getPin()) {
            Log.i(TAG, "No PIN available, requesting...");
            this.queryPin();
            return;
        }

        Log.i(TAG, "Have PIN, starting HBCI request.");

        Log.i(TAG, "Using following HBCI URL: " + this.account.getHbciUrl());
        HbciAccount acc = Adapter.accountFromBean(this.account);
        HbciSession session = new HbciSession(acc);
        session.acquireTransactions();

        Transactions ts = acc.getTransactions();
        for (Iterator<Transaction> i = ts.getIterator(); i.hasNext();) {
            HbciTransaction t = (HbciTransaction) i.next();
            Log.d(TAG, "Have transaction: " + t.toString());

            TransactionBean bean = Adapter.transactionFromHbci(t);
            bean.setAccount(this.account);
            this.repository.saveTransaction(bean);
            Log.d(TAG, "Saved transaction w/ id [" + bean.getId() + "]");
        }

        Toast.makeText(this, R.string.transactions_fetch_done, Toast.LENGTH_LONG).show();
    }

    private void queryPin() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.pin_dialog_title);
        alert.setMessage(R.string.pin_dialog_msg);

        // Set EditText view to get user pin
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface di, int i) {
                Log.i(TAG, "Receving PIN.");
                account.setPin(input.getText().toString());
                fetchTransactions();
            }
        });

        alert.setNegativeButton(R.string.cancel, null);
        alert.show();
    }
}
