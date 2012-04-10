/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import name.kiesel.androidbalance.R;
import name.kiesel.hcbi.impl.HbciAccount;
import name.kiesel.hcbi.impl.HbciCredentials;

/**
 *
 * @author alex
 */
public class AccountRepository {
    private static final String DATABASE_NAME= "accounts";
    private static final int DATABASE_VERSION= 1;
    public static final String TITLE_COLUMN = "name";
    private static final String[] ACCOUNT_COLUMNS = new String[] {
            "_id",
            "number",
            "bankcode",
            "hbciurl",
            "hbciversion",
            "userid",
            "name"
        };

    private final Context ctx;
    private DatabaseHelper dbh;
    private SQLiteDatabase db;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        
        DatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqld) {
            sqld.execSQL("create table accounts (" +
                    "_id integer primary key autoincrement, " +
                    "number text not null, " +
                    "bankcode text not null, " +
                    "hbciurl text null, " +
                    "hbciversion text null, " +
                    "name text not null, " +
                    "userid text null)"
            );
            sqld.execSQL("insert into accounts (number, bankcode, name) values (\"8542130\", \"66090800\", \"Main account - BBBank\")");
            sqld.execSQL("insert into accounts (number, bankcode, name) values (\"123456\", \"66090900\", \"Second account\")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqld, int i, int i1) {
            sqld.execSQL("drop table if exists accounts");
            onCreate(sqld);
        }
        
    }

    public AccountRepository(Context ctx) {
        this.ctx = ctx;
    }
    
    public void open() {
        this.dbh= new DatabaseHelper(this.ctx);
        this.db= this.dbh.getWritableDatabase();
    }
    
    public void close() {
        this.dbh.close();
    }

    public Cursor findAllAccounts() {
        return this.db.query("accounts", ACCOUNT_COLUMNS, null, null, null, null, null);
    }
    
    public HbciAccount byAccountId(Long accountId) {
        Cursor c= this.db.query("accounts", ACCOUNT_COLUMNS, "_id = ?", new String[] { String.valueOf(accountId) }, null, null, null);
        
        if (c.getCount() != 1) {
            throw new IllegalArgumentException("Could not find proper account for id " + accountId);
        }
        
        // FIXME
        HbciAccount a= new HbciAccount(c.getString(1), c.getString(2));
        a.getCredentials().setUserId(c.getString(5));
        
        return a;
    }
    
    public void updateAccount(HbciAccount a) {
    }

    public void createOrUpdateAccount(HbciAccount a) {
        ContentValues values= new ContentValues();
        values.put("number", a.getAccountNumber());
        values.put("bankcode", a.getBankCode());
        values.putNull("hbciurl");
        values.put("hbciversion", "300");
        values.put("name", "New account");
        values.put("userid", a.getCredentials().getUserId());
        
        Long rowId= this.db.insert("accounts", null, values);
        Log.i("Created new account w/ id ", String.valueOf(rowId));
    }

    public static HbciAccount fromValues(String accountNumber, String bankCode, String userId, String hbciUrl, String hbciVersion) {
        HbciAccount a= new HbciAccount(accountNumber, bankCode);
        a.setCredentials(new HbciCredentials());
        a.getCredentials().setUserId(userId);

        return a;
    }
    

}
