package wyz.whaley.pinterest.http.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.utils.DataUtils;
import wyz.whaley.pinterest.utils.FileUtils;

/**
 * Created by alwayking on 16/3/31.
 */
public class AKImageDownLoader extends AsyncTask<Void, Integer, Bitmap> {

    private static final String TAG = "AKImageDownLoader";

    private String mUrl;
    private ProgressListener mProgressListener;
    private int mExpectWidth;
    private ArrayList<ProgressListener> mListenerList = new ArrayList<>();
    private Context mContext;

    public AKImageDownLoader(Context context, String url, int expectWidth, ProgressListener progressListener) {
        super();
        this.mUrl = url;
        this.mExpectWidth = expectWidth;
        if (progressListener != null) {
            mListenerList.add(progressListener);
        }
//        this.mProgressListener = progressListener;
        this.mContext = context;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        InputStream input = null;
        InputStream in = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        Bitmap bitmap;
//        Bitmap bitmap = AKImageDiskCache.readImage(mContext, mUrl);
//        if (bitmap != null) {
//            if (mExpectWidth > 0) {
//                bitmap = BitmapUtils.resizeBitmap(bitmap, mExpectWidth);
//            }
//            return bitmap;
//        }


//        AKImageDiskCache imageDiskCache = new AKImageDiskCache(mContext);
        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int fileLength = connection.getContentLength();

            final File destFile = FileUtils.getDiskCacheDir(mContext, "TMP");
            destFile.mkdir();
            final String destFilePath = destFile.getAbsolutePath();
            if (FileUtils.makeDirs(destFilePath)) {
                String destFileStr = null;
                String name = "detail.tmp";
//                destFileStr = destFilePath + "/" + name + ".tmp";
                File dir = new File(destFilePath, name);
//                if (!dir.exists()) {
//                    try {
//                        //在指定的文件夹中创建文件
                dir.createNewFile();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }

                if (dir.exists()) {
                    output = new BufferedOutputStream(new FileOutputStream(dir), 8 * 1024);
                    input = connection.getInputStream();
//                    output = new BufferedOutputStream(imageDiskCache.getOutPutStream(mUrl, mContext), 8 * 1024);
                    byte data[] = new byte[10240];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
//                            imageDiskCache.abort();
                            output.close();
                            return null;
                        }
                        total += count;
                        if (fileLength > 0)
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
//                    imageDiskCache.commitSave();
                    output.close();
                    input.close();

                    in = new BufferedInputStream(new FileInputStream(dir));
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
//                    bitmap = AKImageDiskCache.readImage(mContext, mUrl);
                    if (mExpectWidth > 0) {
                        bitmap = BitmapUtils.resizeBitmap(bitmap, mExpectWidth);
                    }
                    return bitmap;
                }
            }
            return null;

        } catch (Exception e) {
            Log.e(TAG, e.toString());
//            publishProgress(-1);
            return null;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
                if (in != null){
                    in.close();
                }
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        for (ProgressListener listener : mListenerList) {
            if (listener != null) {
                if (values[0] < 0) {
                    listener.complete(null);
                    Log.e(TAG, "Down load image exception!");
                } else {
                    listener.updateProgress(values[0]);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        for (ProgressListener listener : mListenerList) {
            if (listener != null) {
                listener.complete(bitmap);
            }
        }
    }

    public interface ProgressListener {
        void updateProgress(int progress);

        void complete(Bitmap bitmap);

        void setIsIndeterminate(boolean isIndeterminate);
    }
}
