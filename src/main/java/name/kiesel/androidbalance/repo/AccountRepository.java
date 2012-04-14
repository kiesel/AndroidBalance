/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.repo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.LinkedList;
import java.util.List;
import name.kiesel.androidbalance.bean.AccountBean;

/**
 *
 * @author alex
 */
public class AccountRepository {
    private static final String DATABASE_NAME= "accounts";
    private static final int DATABASE_VERSION= 1;
    
    public static final String TABLE_ACCOUNT=   "account";
    public static final String FIELD_ACCOUNT_NUMBER = "number";
    public static final String FIELD_ACCOUNT_BANKCODE = "bankcode";
    public static final String FIELD_ACCOUNT_HBCIURL = "hbciurl";
    public static final String FIELD_ACCOUNT_HBCIVER= "hbicversion";
    public static final String FIELD_ACCOUNT_USERID= "userid";
    public static final String FIELD_ACCOUNT_NAME= "name";

    private final Context ctx;
    private DatabaseHelper dbh;
    private SQLiteDatabase db;
    private final Object lock= new Object();

    private static class DatabaseHelper extends SQLiteOpenHelper {
        
        DatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqld) {
            sqld.execSQL("CREATE TABLE " + TABLE_ACCOUNT + "("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_ACCOUNT_NAME + " TEXT NOT NULL, "
                    + FIELD_ACCOUNT_NUMBER + " TEXT NOT NULL, "
                    + FIELD_ACCOUNT_BANKCODE + " TEXT NOT NULL, "
                    + FIELD_ACCOUNT_HBCIURL + " TEXT, "
                    + FIELD_ACCOUNT_HBCIVER + " TEXT, "
                    + FIELD_ACCOUNT_USERID + " TEXT)"
                    );
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqld, int oldVersion, int newVersion) {
//            sqld.execSQL("drop table if exists accounts");
//            onCreate(sqld);
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
    
    public AccountBean saveAccount(AccountBean bean) {
        long id;
        synchronized(this.lock) {
            id= this.db.insert(TABLE_ACCOUNT, null, bean.getValues());
        }
        
        bean.setId(id);
        return bean;
    }
    
    public void deleteAccount(AccountBean bean) {
        if (-1 == bean.getId()) return;
        
        synchronized(this.lock) {
            this.db.delete(TABLE_ACCOUNT, "_id = ?", new String[] { String.valueOf(bean.getId()) });
        }
    }
    
    public AccountBean findAccountById(long id) {
        List<AccountBean> list;
        
        synchronized(this.lock) {
            Cursor c= this.db.query(TABLE_ACCOUNT, null, "_id = ?", new String[] { String.valueOf(id) }, null, null, null);
            list= this.accountsFromCursor(c);
            c.close();
            
        }
        
        if (list.size() > 0) {
            return list.get(0);
        }
        
        return null;
    }
    
    public List<AccountBean> findAllAccounts() {
        List<AccountBean> list;
        synchronized(this.lock) {
            Cursor c= this.db.query(TABLE_ACCOUNT, new String[] {
                "_id",
                FIELD_ACCOUNT_NAME,
                FIELD_ACCOUNT_NUMBER,
                FIELD_ACCOUNT_BANKCODE,
                FIELD_ACCOUNT_USERID,
                FIELD_ACCOUNT_HBCIURL,
                FIELD_ACCOUNT_HBCIVER
            }, null, null, null, null, null);
            
            list= this.accountsFromCursor(c);
        }
        
        return list;
    }

    private List<AccountBean> accountsFromCursor(Cursor c) {
        List<AccountBean> accounts= new LinkedList<AccountBean>();
        
        final int COL_ID= c.getColumnIndexOrThrow("_id"),
                COL_NUMBER= c.getColumnIndexOrThrow(FIELD_ACCOUNT_NUMBER),
                COL_BANKCODE= c.getColumnIndexOrThrow(FIELD_ACCOUNT_BANKCODE),
                COL_NAME= c.getColumnIndexOrThrow(FIELD_ACCOUNT_NAME),
                COL_HBCIURL= c.getColumnIndexOrThrow(FIELD_ACCOUNT_HBCIURL),
                COL_HBCIVER= c.getColumnIndexOrThrow(FIELD_ACCOUNT_HBCIVER),
                COL_USERID= c.getColumnIndexOrThrow(FIELD_ACCOUNT_USERID);
        
        while (c.moveToNext()) {
            AccountBean b= new AccountBean();
            b.setId(c.getLong(COL_ID));
            b.setBankCode(c.getString(COL_BANKCODE));
            b.setNumber(c.getString(COL_NUMBER));
            b.setTitle(c.getString(COL_NAME));
            b.setHbciUrl(c.getString(COL_HBCIURL));
            b.setHbciVersion(c.getString(COL_HBCIVER));
            b.setUserId(c.getString(COL_USERID));
            
            accounts.add(b);
        }
        
        return accounts;
    }
}
