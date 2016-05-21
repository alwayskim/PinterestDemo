package wyz.whaley.pinterest.db.table;

import java.util.List;

import wyz.whaley.pinterest.data.ShotInfo;

/**
 * Created by alwayking on 16/4/9.
 */
public abstract class AKBaseTable<T>{

    protected String mTableName;

    public static final String _ID = "_id";

    public abstract void insert(T t);
    public abstract void insertList(List<T> list);
    public abstract void clear();
    public abstract void delete(T t, DBCallBack dbCallBack);
    public abstract void deleteList(List<T> t, DBCallBack dbCallBack);
    public abstract List<T> query(String selection, DBCallBack dbCallBack);
    public abstract List<T> query(DBCallBack dbCallBack);
    public abstract String getCreateTableSQL();

    public String getTableName() {
        return mTableName;
    }

    public interface DBCallBack{
        void queryComplete(List<ShotInfo> list);
    }
}
