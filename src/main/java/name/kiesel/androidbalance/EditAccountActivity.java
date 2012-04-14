/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import name.kiesel.androidbalance.bean.AccountBean;
import name.kiesel.androidbalance.repo.AccountRepository;
import name.kiesel.hbci.impl.HbciAccount;
import name.kiesel.hbci.impl.HbciSession;

/**
 *
 * @author alex
 */
public class EditAccountActivity extends Activity {

    private Long accountId = null;
    private AccountRepository repository = null;
    private AccountBean account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.repository = new AccountRepository(this);
        this.repository.open();

        this.setContentView(R.layout.edit_account);

        accountId = null;
        if (null != savedInstanceState) {
            accountId = (Long) savedInstanceState.getSerializable("accountId");
        } else {
            Bundle extras = this.getIntent().getExtras();
            if (null != extras) {
                this.accountId = (Long) extras.getLong("accountId");
            }
        }

        if (null != this.account) {
            this.account = this.repository.findAccountById(accountId);
            this.fillData();
        }

        // Fill spinner values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.edit_account_hbciversions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) this.findViewById(R.id.edit_account_hbciver)).setAdapter(adapter);

        // Callback action when bankcode has changed
        ((EditText) this.findViewById(R.id.edit_account_bankcode)).setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, final boolean hasFocus) {
                if (true == hasFocus) {
                    return;
                }

                Editable bc = ((EditText) view).getText();
                if (null != bc && bc.length() > 0) {
                    Log.d("Looking up host for bankcode: ", bc.toString());
                    String host = new HbciSession(new HbciAccount("fake account", bc.toString())).hbciHost();

                    Log.d("Received host: ", host);
                    ((EditText) findViewById(R.id.edit_account_hbciurl)).setText(bc);
                }
            }
        });

        // Callback for cancel-button
        ((Button) this.findViewById(R.id.edit_account_cancel)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // Callback for save-button
        ((Button) this.findViewById(R.id.edit_account_save)).setOnClickListener(new OnClickListener() {

            private String extractString(int id) {
                Editable e = ((EditText) findViewById(id)).getText();
                if (null == e) {
                    return null;
                }

                return e.toString();
            }

            @Override
            public void onClick(View view) {
                AccountBean bean= new AccountBean();
                
                bean.setTitle(extractString(R.id.edit_account_title));
                bean.setNumber(extractString(R.id.edit_account_aid));
                bean.setBankCode(extractString(R.id.edit_account_bankcode));
                bean.setUserId(extractString(R.id.edit_account_userid));
                bean.setHbciUrl(extractString(R.id.edit_account_hbciurl));
                bean.setHbciVersion("300"); // FIXME
                repository.saveAccount(bean);

                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void fillData() {
        ((EditText) this.findViewById(R.id.edit_account_aid)).setText(this.account.getNumber());
        ((EditText) this.findViewById(R.id.edit_account_bankcode)).setText(this.account.getBankCode());
    }
}
