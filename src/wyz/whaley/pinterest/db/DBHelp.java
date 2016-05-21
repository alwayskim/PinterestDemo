package wyz.whaley.pinterest.db;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelp {
    private static final String DB_NAME = "pinterest.db";
    private static final int DB_VERSION = 1;

    private SQLiteHelp dbHelp;
    private SQLiteDatabase db;

    private static DBHelp instance;
    private static final String TAG = "DBHelp";

    private DBHelp(Context context) {
        dbHelp = new SQLiteHelp(context, DB_NAME, null, DB_VERSION);
        db = dbHelp.getWritableDatabase();
    }

    public static DBHelp getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelp.class) {
                if (instance == null) {
                    instance = new DBHelp(context);
                }
            }
        }
        return instance;
    }

    public void insertItemInfo(ItemInfo info) {
        // 添加users表的记录
        ContentValues values = new ContentValues();
        values.put(ItemInfo.ID, info.getId());
        values.put(ItemInfo.IMAGE_NAME, info.getImageName());
        values.put(ItemInfo.IMAGE_URL, info.getImageURL());
        values.put(ItemInfo.IMAGE_WIDTH, info.getWidth());
        values.put(ItemInfo.IMAGE_HEIGHT, info.getHeight());
        db.replace(SQLiteHelp.TABLE_NAME, null, values);
    }

    public void insertItemList(List<ItemInfo> list) {
        for (ItemInfo imageInfo : list) {
            insertItemInfo(imageInfo);
        }
    }

    public void deleteAll() {
        db.delete(SQLiteHelp.TABLE_NAME, null, null);
    }

    public List<ItemInfo> queryAll() {
        List<ItemInfo> infoList = new LinkedList<ItemInfo>();
        Cursor cursor = db.query(SQLiteHelp.TABLE_NAME, null, null, null, null, null, ItemInfo.ID + " ASC");
        // ImageInfo.ID + " DESC"
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            ItemInfo info = new ItemInfo();
            info.setId(cursor.getLong(0));
            info.setImageName(cursor.getString(1));
            info.setImageURL(cursor.getString(2));
            info.setWidth(cursor.getInt(3));
            info.setHeight(cursor.getInt(4));
            infoList.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i(TAG, "query all image list size : " + infoList.size());
        return infoList;
    }

}
