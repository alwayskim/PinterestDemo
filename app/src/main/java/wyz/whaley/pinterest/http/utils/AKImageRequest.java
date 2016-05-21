package wyz.whaley.pinterest.http.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.utils.DataUtils;

public class AKImageRequest extends AKRequest {

    public AKImageRequest(final String url, final AKResponseListener listener, int requestMode, Context context) {
        super(url, listener, requestMode, context);
    }

    public AKImageRequest(final String url, final AKResponseListener listener, int requestMode, Priority priority, Context context) {
        super(url, listener, requestMode, priority, context);
    }

    @Override
    public void dispatcherResult(InputStream in) {
//        Bitmap bitmap = BitmapFactory.decodeStream(in);
        byte[] bytes = DataUtils.convertToByteArray(in);
        if (this.mListener != null) {
            this.mListener.onSuccess(bytes);
        }
        if (this.mListenersList != null) {
            for (AKResponseListener l : this.mListenersList) {
                l.onSuccess(bytes);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AKImageRequest) {
            AKRequest request = (AKImageRequest) o;
            return request.mUrl.equals(this.mUrl);
        } else {
            return o.equals(this.mUrl);
        }
    }

    @Override
    public String toString() {
        return mUrl;
    }
}
