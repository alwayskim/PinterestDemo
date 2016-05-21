package wyz.whaley.pinterest.db.table;

import android.content.Context;

/**
 * Created by alwayking on 16/4/9.
 */
public class LikesTable extends BaseShotTable {

    private static LikesTable instance;
    public static final String TABLE_NAME = "likes";

    private LikesTable(Context context) {
        super(context, TABLE_NAME);
    }

    public static LikesTable getInstance(Context context) {
        if (instance == null) {
            synchronized (FollowingTable.class) {
                if (instance == null) {
                    instance = new LikesTable(context);
                }
            }
        }
        return instance;
    }
}
