package wyz.whaley.pinterest.db.table;

import android.content.Context;

/**
 * Created by alwayking on 16/4/9.
 */
public class FollowingTable extends BaseShotTable {

    private static FollowingTable instance;
    public static final String TABEL_NAME = "following";

    private FollowingTable(Context context) {
        super(context, TABEL_NAME);
    }

    public static FollowingTable getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadedTable.class) {
                if (instance == null) {
                    instance = new FollowingTable(context);
                }
            }
        }
        return instance;
    }
}
