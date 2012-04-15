/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.repo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import name.kiesel.androidbalance.bean.AccountBean;
import name.kiesel.androidbalance.bean.TransactionBean;

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
    private static final String[] FIELDS_ACCOUNT= new String[] {
        BaseColumns._ID,
        FIELD_ACCOUNT_NAME,
        FIELD_ACCOUNT_NUMBER,
        FIELD_ACCOUNT_BANKCODE,
        FIELD_ACCOUNT_USERID,
        FIELD_ACCOUNT_HBCIURL,
        FIELD_ACCOUNT_HBCIVER
    };
    
    public static final String TABLE_TRANSACTION=  "account_transaction";
    public static final String FIELD_TRANSACTION_ACCOUNT=  "account";
    public static final String FIELD_TRANSACTION_BALANCE=  "balance";
    public static final String FIELD_TRANSACTION_CURRENCY= "curr";
    public static final String FIELD_TRANSACTION_BOOKINGDATE=  "bookingdate";
    public static final String FIELD_TRANSACTION_VALUTA=   "valuta";
    public static final String FIELD_TRANSACTION_TEXT= "description";
    public static final String FIELD_TRANSACTION_OTHERACCOUNT= "other_account";
    public static final String FIELD_TRANSACTION_OTHERBANK= "other_bankcode";
    private static final String[] FIELDS_TRANSACTION= new String[] {
        BaseColumns._ID,
        FIELD_TRANSACTION_ACCOUNT,
        FIELD_TRANSACTION_BALANCE,
        FIELD_TRANSACTION_CURRENCY,
        FIELD_TRANSACTION_BOOKINGDATE,
        FIELD_TRANSACTION_VALUTA,
        FIELD_TRANSACTION_TEXT,
        FIELD_TRANSACTION_OTHERACCOUNT,
        FIELD_TRANSACTION_OTHERBANK
    };

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
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_ACCOUNT_NAME + " TEXT NOT NULL, "
                    + FIELD_ACCOUNT_NUMBER + " TEXT NOT NULL, "
                    + FIELD_ACCOUNT_BANKCODE + " TEXT NOT NULL, "
                    + FIELD_ACCOUNT_HBCIURL + " TEXT, "
                    + FIELD_ACCOUNT_HBCIVER + " TEXT, "
                    + FIELD_ACCOUNT_USERID + " TEXT)"
                    );
            
            sqld.execSQL("CREATE TABLE " + TABLE_TRANSACTION + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_TRANSACTION_ACCOUNT + " INTEGER NOT NULL, "
                    + FIELD_TRANSACTION_BALANCE + " REAL NOT NULL, "
                    + FIELD_TRANSACTION_CURRENCY + " TEXT NOT NULL, "
                    + FIELD_TRANSACTION_BOOKINGDATE + " TIMESTAMP NOT NULL, "
                    + FIELD_TRANSACTION_VALUTA + " TIMESTAMP NOT NULL, "
                    + FIELD_TRANSACTION_TEXT + " TEXT, "
                    + FIELD_TRANSACTION_OTHERACCOUNT + " TEXT, "
                    + FIELD_TRANSACTION_OTHERBANK + " TEXT, "
                    + "FOREIGN KEY(" + FIELD_TRANSACTION_ACCOUNT + ") REFERENCES "
                        + TABLE_ACCOUNT + "(" + BaseColumns._ID + "))"
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
    
    public AccountBean insertAccount(AccountBean bean) {
        long id;
        synchronized(this.lock) {
            id= this.db.insert(TABLE_ACCOUNT, null, bean.getValues());
        }
        
        bean.setId(id);
        return bean;
    }
    
    private AccountBean updateAccount(AccountBean bean) {
        synchronized(this.lock) {
            this.db.update(TABLE_ACCOUNT, bean.getValues(), "_id = ?", new String[] { String.valueOf(bean.getId()) });
        }
        return bean;
    }

    public AccountBean saveAccount(AccountBean bean) {
        if (-1 == bean.getId()) {
            return this.insertAccount(bean);
        } else {
            return this.updateAccount(bean);
        }
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
            Cursor c= this.db.query(TABLE_ACCOUNT, FIELDS_ACCOUNT, null, null, null, null, null);
            
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
    
    public List<TransactionBean> findTransactionsByAccount(AccountBean a) {
        List<TransactionBean> list;
        synchronized(this.lock) {
            Cursor c= this.db.query(TABLE_TRANSACTION, FIELDS_TRANSACTION, null, null, null, null, null);
            
            list= this.transactionsFromCursor(c);
        }
        
        return list;
    }
    
    private List<TransactionBean> transactionsFromCursor(Cursor c) {
        List<TransactionBean> t= new LinkedList<TransactionBean>();
        
        final int COL_ID= c.getColumnIndexOrThrow("_id"),
                COL_ACCOUNTID= c.getColumnIndexOrThrow(FIELD_TRANSACTION_ACCOUNT),
                COL_BALANCE= c.getColumnIndexOrThrow(FIELD_TRANSACTION_BALANCE),
                COL_CURRENCY= c.getColumnIndexOrThrow(FIELD_TRANSACTION_CURRENCY),
                COL_BOOKINGDATE= c.getColumnIndexOrThrow(FIELD_TRANSACTION_BOOKINGDATE),
                COL_VALUTA= c.getColumnIndexOrThrow(FIELD_TRANSACTION_VALUTA),
                COL_TEXT= c.getColumnIndexOrThrow(FIELD_TRANSACTION_TEXT),
                COL_OTHERACCOUNT= c.getColumnIndexOrThrow(FIELD_TRANSACTION_OTHERACCOUNT),
                COL_OTHERBANK= c.getColumnIndexOrThrow(FIELD_TRANSACTION_OTHERBANK);
        
        while (c.moveToNext()) {
            TransactionBean b= new TransactionBean();
            b.setId(c.getLong(COL_ID));
            b.setAccountId(c.getLong(COL_ACCOUNTID));
            b.setBalance(c.getDouble(COL_BALANCE));
            b.setCurrency(c.getString(COL_CURRENCY));
            b.setBookingdate(new Date(c.getLong(COL_BOOKINGDATE)));
            b.setValuta(new Date(c.getLong(COL_VALUTA)));
            b.setText(c.getString(COL_TEXT));
            b.setOtherAccountNumber(c.getString(COL_OTHERACCOUNT));
            b.setOtherBankCode(c.getString(COL_OTHERBANK));
            
            t.add(b);
        }
        
        return t;
    }
    
    public TransactionBean insertTransaction(TransactionBean bean) {
        long id;
        synchronized(this.lock) {
            id= this.db.insertOrThrow(TABLE_TRANSACTION, null, bean.getValues());
        }
        
        bean.setId(id);
        return bean;
    }
    
    public TransactionBean updateTransaction(TransactionBean bean) {
        synchronized(this.lock) {
            this.db.update(TABLE_TRANSACTION, bean.getValues(), "_id = ?", new String[] { String.valueOf(bean.getId()) });
        }
        
        return bean;
    }
    
    public TransactionBean saveTransaction(TransactionBean bean) {
        if (-1 == bean.getId()) {
            return this.insertTransaction(bean);
        } else {
            return this.updateTransaction(bean);
        }
    }
}
