package wyz.whaley.pinterest.db.table;

import android.content.Context;

/**
 * Created by alwayking on 16/4/9.
 */
public class PopularTable extends BaseShotTable {
    private static PopularTable instance;
    public static final String TABLE_NAME = "popular";

    private PopularTable(Context context) {
        super(context, TABLE_NAME);
    }

    public static PopularTable getInstance(Context context) {
        if (instance == null) {
            synchronized (FollowingTable.class) {
                if (instance == null) {
                    instance = new PopularTable(context);
                }
            }
        }
        return instance;
    }
}
