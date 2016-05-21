package wyz.whaley.pinterest.http;

import android.os.Process;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by alwayking on 16/3/18.
 */
public class AKRequestThread extends Thread {

    private final PriorityBlockingQueue<AKRequest> mQueue; //优先级阻塞队列
    private volatile boolean mQuit = false;

    public AKRequestThread(PriorityBlockingQueue<AKRequest> queue) {
        this.mQueue = queue;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            AKRequest request;
            try {
                // 从阻塞队列中获取请求实例，若无则阻塞本线程
                request = mQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }
            request.run();
        }
    }

    /**
     * 退出线程
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }
}
