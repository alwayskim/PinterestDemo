package wyz.whaley.pinterest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelp extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "pinterest";

    public SQLiteHelp(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ItemInfo.ID + " integer primary key," + ItemInfo.IMAGE_NAME + " varchar,"
                + ItemInfo.IMAGE_URL + " varchar," + ItemInfo.IMAGE_WIDTH + " integer," + ItemInfo.IMAGE_HEIGHT + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
