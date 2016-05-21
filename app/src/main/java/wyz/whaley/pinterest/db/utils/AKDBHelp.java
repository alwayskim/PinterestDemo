package wyz.whaley.pinterest.db.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import wyz.whaley.pinterest.db.ItemInfo;
import wyz.whaley.pinterest.db.AKSQLiteHelp;
import wyz.whaley.pinterest.db.table.AKBaseTable;

/**
 * Created by alwayking on 16/4/9.
 */
public class AKDBHelp {
    private static final String DB_NAME = "akbubble.db";
    private static final int DB_VERSION = 1;

    private AKSQLiteHelp dbHelp;
    private SQLiteDatabase db;

    private static AKDBHelp instance;
    private static final String TAG = "DBHelp";

    private AKDBHelp(Context context) {
        dbHelp = new AKSQLiteHelp(context, DB_NAME, null, DB_VERSION);
        db = dbHelp.getWritableDatabase();
    }

    public static AKDBHelp getInstance(Context context) {
        if (instance == null) {
            synchronized (AKDBHelp.class) {
                if (instance == null) {
                    instance = new AKDBHelp(context);
                }
            }
        }
        return instance;
    }

    public void insert(String tableName, ContentValues values) {
        db.insert(tableName, null, values);
    }

    public void insertItemList(String tableName, List<ContentValues> valuesList) {
        for (ContentValues values : valuesList) {
            insert(tableName, values);
        }
    }

    public void deleteAll(String tableName) {
        db.delete(tableName, null, null);
    }

    public void delete(String tableName, String selection, String[] arg){
        db.delete(tableName, selection, arg);
    }

    public Cursor query(String tableName) {
        List<ItemInfo> infoList = new LinkedList<ItemInfo>();
        Cursor cursor = db.query(tableName, null, null, null, null, null, AKBaseTable._ID + " ASC");
        return cursor;
    }

    public Cursor query(String tableName, String selection) {
        List<ItemInfo> infoList = new LinkedList<ItemInfo>();
        Cursor cursor = db.query(tableName, null, selection, null, null, null, AKBaseTable._ID + " ASC");
        return cursor;
    }



}
