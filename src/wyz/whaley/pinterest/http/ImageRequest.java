package wyz.whaley.pinterest.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class ImageRequest extends Request {

    public ImageRequest(final String url, final ResponseListener listener, int requestMode) {
        super(url, listener, requestMode);
    }

    @Override
    public void dispatcherResult(InputStream in) {
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        // byte[] b = DataUtility.convertToByteArray(in);
        if (this.listener != null) {
            this.listener.onSuccess(bitmap);
        }
        if (this.listenersList != null) {
            for (ResponseListener l : this.listenersList) {
                l.onSuccess(bitmap);
            }
        }
    }

}
