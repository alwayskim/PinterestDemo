package wyz.whaley.pinterest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import wyz.whaley.pinterest.MyApplication;
import wyz.whaley.pinterest.db.table.DownloadedTable;
import wyz.whaley.pinterest.db.table.FollowingTable;
import wyz.whaley.pinterest.db.table.LikesTable;
import wyz.whaley.pinterest.db.table.PopularTable;

public class AKSQLiteHelp extends SQLiteOpenHelper {

//    public static final String TABLE_NAME = "pinterest";

    public AKSQLiteHelp(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ItemInfo.ID + " integer primary key," + ItemInfo.IMAGE_NAME + " varchar,"
//                + ItemInfo.IMAGE_URL + " varchar," + ItemInfo.IMAGE_WIDTH + " integer," + ItemInfo.IMAGE_HEIGHT + " integer" + ")");

//        db.execSQL("CREATE TABLE IF NOT EXISTS " + UserInfo.TABLE_NAME);
        db.execSQL(FollowingTable.getInstance(MyApplication.getContext()).getCreateTableSQL());
        db.execSQL(LikesTable.getInstance(MyApplication.getContext()).getCreateTableSQL());
        db.execSQL(PopularTable.getInstance(MyApplication.getContext()).getCreateTableSQL());
        db.execSQL(DownloadedTable.getInstance(MyApplication.getContext()).getCreateTableSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


//    public static String generateCreateTableSQL(Object object, String tableName) {
//
//        StringBuilder sqlString = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(");
//        sqlString.append();
//
//
//        // 获取f对象对应类中的所有属性域
//        Field[] fields = object.getClass().getDeclaredFields();
//        for (int i = 0, len = fields.length; i < len; i++) {
//            // 对于每个属性，获取属性名
//            String varName = fields[i].getName();
//
//
//            try {
//                // 获取原来的访问控制权限
//                boolean accessFlag = fields[i].isAccessible();
//                // 修改访问控制权限
//                fields[i].setAccessible(true);
//                // 获取在对象f中属性fields[i]对应的对象中的变量
//                Object o = fields[i].get(object);
//                System.out.println("传入的对象中包含一个如下的变量：" + varName + " = " + object);
//                // 恢复访问控制权限
//                fields[i].setAccessible(accessFlag);
//            } catch (IllegalArgumentException ex) {
//                ex.printStackTrace();
//            } catch (IllegalAccessException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

    public static final String INT_PRI_SQL = " integer primary key,";
    public static final String STRING_SQL = " varchar,";
    public static final String INT = " integer key,";
    public static final String DOT = ",";



}
