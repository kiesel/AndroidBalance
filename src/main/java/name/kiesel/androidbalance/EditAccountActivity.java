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
import android.widget.*;
import java.util.Hashtable;
import java.util.Map;
import name.kiesel.androidbalance.bean.AccountBean;
import name.kiesel.androidbalance.repo.AccountRepository;
import name.kiesel.hbci.impl.HbciAccount;
import name.kiesel.hbci.impl.HbciSession;

/**
 *
 * @author alex
 */
public class EditAccountActivity extends Activity {
    public static final String ACCOUNTID    = "_id";
    public static final String TAG = EditAccountActivity.class.getSimpleName();
    
    private Long accountId = null;
    private AccountRepository repository = null;
    private AccountBean account = null;
    
    private String versionSpinner= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.repository = new AccountRepository(this);
        this.repository.open();

        this.setContentView(R.layout.edit_account);

        accountId = null;
        if (null != savedInstanceState) {
            accountId = (Long) savedInstanceState.getSerializable(ACCOUNTID);
        } else {
            Bundle extras = this.getIntent().getExtras();
            if (null != extras) {
                this.accountId = (Long) extras.getLong(ACCOUNTID);
            }
        }

        Log.i(TAG, "EditAccountActivity startup w/ account [" + this.accountId + "]");
        if (null != this.accountId) {
            this.account = this.repository.findAccountById(accountId);
            this.fillData();
        }

        // Fill spinner values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.edit_account_hbciversions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner= (Spinner) this.findViewById(R.id.edit_account_hbciver);
        spinner.setAdapter(adapter);
        
        // Callback when spinner item was selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map= new Hashtable<String,String>();
                map.put("2.0.1", "V201");
                map.put("2.1.0", "V210");
                map.put("2.2.0", "V220");
                map.put("Fin-TS (3.0.0)", "V300");
                map.put("Plus", "plus");
                
                final String key= parent.getItemAtPosition(position).toString();
                if (map.containsKey(key)) {
                    versionSpinner= map.get(key);
                } else {
                    versionSpinner= null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                versionSpinner= null;
            }
        });

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
                AccountBean bean;
                if (null != account) {
                    bean= account;
                } else {
                    bean= new AccountBean();
                }
                
                bean.setTitle(extractString(R.id.edit_account_title));
                bean.setNumber(extractString(R.id.edit_account_aid));
                bean.setBankCode(extractString(R.id.edit_account_bankcode));
                bean.setUserId(extractString(R.id.edit_account_userid));
                bean.setHbciUrl(extractString(R.id.edit_account_hbciurl));
                
                // Use value set by different listener
                bean.setHbciVersion(versionSpinner);
                repository.saveAccount(bean);
                
                repository.close();

                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void fillData() {
        ((EditText) this.findViewById(R.id.edit_account_title)).setText(this.account.getTitle());
        ((EditText) this.findViewById(R.id.edit_account_aid)).setText(this.account.getNumber());
        ((EditText) this.findViewById(R.id.edit_account_bankcode)).setText(this.account.getBankCode());
        ((EditText) this.findViewById(R.id.edit_account_userid)).setText(this.account.getUserId());
        ((EditText) this.findViewById(R.id.edit_account_hbciurl)).setText(this.account.getHbciUrl());
    }
}
