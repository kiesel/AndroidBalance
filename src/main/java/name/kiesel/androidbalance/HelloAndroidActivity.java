package name.kiesel.androidbalance;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import name.kiesel.androidbalance.repo.AccountRepository;

public class HelloAndroidActivity extends ListActivity {
    private static final int OPT_INSERT_ID  = Menu.FIRST;
    private static final int OPT_DELETE_ID  = Menu.FIRST + 1;
    private static final int ACTIVITY_ACCOUNT_CREATE    = 0;

    private AccountRepository repository= null;
    
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

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });

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
        Cursor c= this.repository.findAllAccounts();
        this.startManagingCursor(c);
        
        String[] from= new String[] { AccountRepository.TITLE_COLUMN };
        int[] to= new int[] { R.id.account_item };
        
        SimpleCursorAdapter adapter= new SimpleCursorAdapter(this, R.layout.account_list_item, c, from, to);
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
        menu.add(0, OPT_DELETE_ID, 0, R.string.main_menu_delete);
    }

    private void createAccount() {
        Intent i= new Intent(this, EditAccountActivity.class);
        this.startActivityForResult(i, ACTIVITY_ACCOUNT_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
}
