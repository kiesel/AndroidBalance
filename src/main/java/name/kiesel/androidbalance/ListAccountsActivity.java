package name.kiesel.androidbalance;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;
import name.kiesel.androidbalance.bean.AccountBean;
import name.kiesel.androidbalance.repo.AccountRepository;

public class ListAccountsActivity extends ListActivity {
    private static final String TAG= "AndroidBalance.Main";
    private static final int OPT_INSERT_ID  = Menu.FIRST;
    private static final int OPT_DELETE_ID  = Menu.FIRST + 1;
    private static final int ACTIVITY_ACCOUNT_CREATE    = 0;
    private static final int ACTIVITY_TRANSACTIONS_LIST = 1;

    private AccountRepository repository= null;
    
    private static class AccountListAdapter extends ArrayAdapter<AccountBean> {
        private final List<AccountBean> list;
        private final Activity context;

        public AccountListAdapter(Activity ctx, List<AccountBean> list) {
            super(ctx, R.layout.account_list_item, list);
            this.context= ctx;
            this.list= list;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            
            final AccountBean elem= list.get(position);
            if (null == convertView) {
                LayoutInflater inflater= context.getLayoutInflater();
                view= inflater.inflate(R.layout.account_list_item, null);
                TextView text= (TextView)view.findViewById(R.id.account_item);
                text.setTag(elem);
            } else {
                view= convertView;
                view.setTag(elem);
            }
            
            ((TextView)view).setText(elem.getTitle() + " - " + elem.getNumber());
            Log.d(TAG, "Added " + elem.getTitle() + " at pos " + position);
            
            return view;
        }
    }

    
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
     * is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.repository= new AccountRepository(this);
        this.repository.open();

        this.fillAccounts();

        ListView lv = this.getListView();
        lv.setTextFilterEnabled(true);

//        lv.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//                Intent i= new Intent(this, TransactionsListActivity.class);
//                this.startActivityForResult(i, ACTIVITY_TRANSACTIONS_LIST);
//                // When clicked, show a toast with the TextView text
//                Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

        this.registerForContextMenu(this.getListView());
        

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPT_INSERT_ID: {
                this.createAccount();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillAccounts() {
        List<AccountBean> list= this.repository.findAllAccounts();
        
//        String[] from= new String[] { AccountRepository.FIELD_ACCOUNT_NAME };
//        int[] to= new int[] { R.id.account_item };
//        
////        SimpleAdapter adapter= new SimpleAdapter(this, list, R.layout.account_list_item, from, to);
//        ArrayAdapter adapter= new ArrayAdapter(this, R.layout.account_list_item, list);
//        this.setListAdapter(adapter);
        
        ArrayAdapter<AccountBean> adapter= new AccountListAdapter(this, list);
        this.setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result= super.onCreateOptionsMenu(menu);
        menu.add(0, OPT_INSERT_ID, 0, R.string.main_menu_create);
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        
        AccountBean bean= (AccountBean)info.targetView.getTag();
        menu.setHeaderTitle(bean.getTitle());
        info.id= bean.getId();
        
        
        menu.add(0, OPT_INSERT_ID, 0, R.string.main_menu_modify);
        menu.add(0, OPT_DELETE_ID, 0, R.string.main_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case OPT_INSERT_ID: {
                this.editAccount(info.id);
                return true;
            }
                
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void createAccount() {
        Log.i(TAG, "Yield EditAccountActivity w/o id (create mode)");
        Intent i= new Intent(this, EditAccountActivity.class);
        this.startActivityForResult(i, ACTIVITY_ACCOUNT_CREATE);
    }
    
    private void editAccount(long id) {
        Log.i(TAG, "Yield EditAccountActivity w/ id [" + id + "] (edit mode)");
        Intent i= new Intent(this, EditAccountActivity.class);
        i.putExtra(EditAccountActivity.ACCOUNTID, id);
        this.startActivityForResult(i, ACTIVITY_ACCOUNT_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i= new Intent(this, TransactionsListActivity.class);
        i.putExtra(TransactionsListActivity.ACCOUNTID, ((AccountBean)v.getTag()).getId());
        
        this.startActivityForResult(i, ACTIVITY_TRANSACTIONS_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_ACCOUNT_CREATE: {
                if (RESULT_OK == resultCode) {
                    Toast.makeText(getApplicationContext(), R.string.main_confirm_save,
                        Toast.LENGTH_SHORT).show();
                    
                    this.fillAccounts();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
}
