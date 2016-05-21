package wyz.whaley.pinterest.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

import wyz.whaley.pinterest.data.ShotInfo;
import wyz.whaley.pinterest.db.utils.AKDBHelp;

/**
 * Created by alwayking on 16/4/9.
 */
abstract class BaseShotTable extends AKBaseTable<ShotInfo> {

    protected String mTableName;
    protected Context mContext;

    protected BaseShotTable(Context context, String tableName) {
        mContext = context;
        mTableName = tableName;
    }


    public enum Columns {
        ID("id"),
        JSON_DATA("json");
        public String key;

        private Columns(String key) {
            this.key = key;
        }
    }

    @Override
    public void insert(ShotInfo shotInfo) {
        Gson gson = new GsonBuilder().create();
        String jsonObject = gson.toJson(shotInfo);
        ContentValues values = new ContentValues();
        values.put(Columns.ID.key, shotInfo.getId());
        values.put(Columns.JSON_DATA.key, jsonObject);
        AKDBHelp.getInstance(mContext).insert(mTableName, values);
    }

    @Override
    public void insertList(List<ShotInfo> shotInfoList) {
        for (ShotInfo info : shotInfoList) {
            insert(info);
        }
    }

    @Override
    public void clear() {
        AKDBHelp.getInstance(mContext).deleteAll(mTableName);
    }

    @Override
    public void delete(ShotInfo shotInfo, DBCallBack callBack) {
        AKDBHelp.getInstance(mContext).delete(mTableName, Columns.ID.key + "=?", new String[]{String.valueOf(shotInfo.getId())});
    }

    @Override
    public void deleteList(List<ShotInfo> t, DBCallBack dbCallBack) {
        for (ShotInfo info : t) {
            AKDBHelp.getInstance(mContext).delete(mTableName, Columns.ID.key + "=?", new String[]{String.valueOf(info.getId())});
        }
        if (dbCallBack != null) {
            dbCallBack.queryComplete(null);
        }
    }

    @Override
    public List<ShotInfo> query(String selection, DBCallBack callBack) {
        List<ShotInfo> list = new LinkedList<>();
        Gson gson = new GsonBuilder().create();
        Cursor cursor = AKDBHelp.getInstance(mContext).query(mTableName);
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            String data = cursor.getString(2);
            ShotInfo info = gson.fromJson(data, ShotInfo.class);
            list.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        if (callBack != null) {
            callBack.queryComplete(list);
        }
        return list;
    }

    @Override
    public List<ShotInfo> query(DBCallBack dbCallBack) {
        return query(null, dbCallBack);
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS " + mTableName + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + Columns.ID.key + " VARCHAR(255), "
                + Columns.JSON_DATA.key + " VARCHAR, "
                + "UNIQUE (" + Columns.ID.key + ") ON CONFLICT REPLACE)";
    }
}
