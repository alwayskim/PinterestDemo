package wyz.whaley.pinterest.http.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.AsyncTask;
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
 * Created by alwayking on 16/4/4.
 */
public class AKGifDownLoader extends AsyncTask<String, Integer, Movie> {

    private static final String TAG = "AKGifDownLoader";

    private String mUrl;
    private ProgressListener mProgressListener;
    private ArrayList<ProgressListener> mListenerList = new ArrayList<>();
    private Context mContext;

    public AKGifDownLoader(Context context, String url, ProgressListener progressListener) {
        super();
        this.mUrl = url;
        if (progressListener != null) {
            mListenerList.add(progressListener);
        }
//        this.mProgressListener = progressListener;
        this.mContext = context;
    }

    @Override
    protected Movie doInBackground(String... strings) {
        InputStream input = null;
        InputStream in = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        Movie movie;

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
                    output = new BufferedOutputStream(new FileOutputStream(dir));
                    input = connection.getInputStream();
//                    output = new BufferedOutputStream(imageDiskCache.getOutPutStream(mUrl, mContext), 8 * 1024);
                    byte data[] = new byte[10240];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            output.close();
//                            imageDiskCache.abort();
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
                    byte[] bytes = DataUtils.convertToByteArray(in);
                    movie = Movie.decodeByteArray(bytes, 0, bytes.length);
                    in.close();
//                    bitmap = AKImageDiskCache.readImage(mContext, mUrl);

                    return movie;
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
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }


//        InputStream input = null;
//        OutputStream output = null;
//        HttpURLConnection connection = null;
//
//        Movie movie;
//
//        AKImageDiskCache imageDiskCache = new AKImageDiskCache(mContext);
//        try {
//            URL url = new URL(mUrl);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
//            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                return null;
//            }
//
//            int fileLength = connection.getContentLength();
//
//            input = connection.getInputStream();
//            output = new BufferedOutputStream(imageDiskCache.getOutPutStream(mUrl, mContext), 8 * 1024);
//            byte data[] = new byte[1024];
//            long total = 0;
//            int count;
//            while ((count = input.read(data)) != -1) {
//                // allow canceling with back button
//                if (isCancelled()) {
//                    input.close();
//                    imageDiskCache.abort();
//                    return null;
//                }
//                total += count;
//                if (fileLength > 0)
//                    publishProgress((int) (total * 100 / fileLength));
//                output.write(data, 0, count);
//            }
//            imageDiskCache.commitSave();
//            input.close();
//            output.close();
//            return AKImageDiskCache.readGif(mContext, mUrl);
//        } catch (Exception e) {
//            publishProgress(-1);
//            return null;
//        } finally {
//            try {
//                if (output != null)
//                    output.close();
//                if (input != null)
//                    input.close();
//            } catch (IOException ignored) {
//            }
//            if (connection != null)
//                connection.disconnect();
//        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        for (ProgressListener listener : mListenerList) {
            if (listener != null) {
                if (values[0] < 0) {
                    listener.complete(null);
                } else {
                    listener.updateProgress(values[0]);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Movie movie) {
        for (ProgressListener listener : mListenerList) {
            if (listener != null) {
                listener.complete(movie);
            }
        }
    }

    public interface ProgressListener {
        void updateProgress(int progress);

        void complete(Movie movie);

        void setIsIndeterminate(boolean isIndeterminate);
    }
}
