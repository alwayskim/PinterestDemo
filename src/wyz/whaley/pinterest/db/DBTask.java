package wyz.whaley.pinterest.db;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBTask {
    private static DBTask instace;

    private Context context;

    private static ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    private DBTask(Context context) {
        this.context = context;
    }

    public static DBTask getInstance(Context context) {
        if (instace == null) {
            synchronized (DBTask.class) {
                if (instace == null) {
                    instace = new DBTask(context);
                }
            }
        }
        return instace;
    }

    public void insert(final List<ItemInfo> list, final DBChangeListener listener) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DBHelp.getInstance(context).insertItemList(list);
                notificateChange(listener);
            }
        });
    }

    public void deleteAll(final DBChangeListener listener) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DBHelp.getInstance(context).deleteAll();
                notificateChange(listener);
            }
        });
    }

    public void queryAll(final DBChangeListener listener) {
        dbExecutor.execute(new Runnable() {
            @Override
            public void run() {
                notificateChange(listener);
            }
        });
    }

    public void notificateChange(DBChangeListener listener) {
        if (listener != null) {
            List<ItemInfo> list = DBHelp.getInstance(context).queryAll();
            if (list.size() > 0) {
                listener.listAfterDBChange(list);
            }
        }
    }

    public interface DBChangeListener {
        void listAfterDBChange(List<?> list);
    }
}
