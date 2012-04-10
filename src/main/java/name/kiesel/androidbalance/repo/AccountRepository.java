/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.kiesel.androidbalance.repo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import name.kiesel.hcbi.impl.HbciAccount;

/**
 *
 * @author alex
 */
public class AccountRepository {
    private static final String DATABASE_NAME= "accounts";
    private static final int DATABASE_VERSION= 1;
    public static final String TITLE_COLUMN = "name";
    
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
                    "name text not null)"
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
        return this.db.query("accounts", new String[] {
            "_id",
            "number",
            "bankcode",
            "name"
        }, null, null, null, null, null);
    }
    
    public HbciAccount byAccountId(Long accountId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
