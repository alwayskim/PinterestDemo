package wyz.whaley.pinterest.db.table;

import android.content.Context;

/**
 * Created by alwayking on 16/4/9.
 */
public class DownloadedTable extends BaseShotTable {

    private static DownloadedTable instance;
    public static final String TABEL_NAME = "downloaded_images";

    private DownloadedTable(Context context) {
        super(context, TABEL_NAME);
    }

    public static DownloadedTable getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadedTable.class) {
                if (instance == null) {
                    instance = new DownloadedTable(context);
                }
            }
        }
        return instance;
    }
}
