package wyz.whaley.pinterest.http.utils;


import android.content.Context;

import java.io.InputStream;

import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.utils.DataUtils;

/**
 * Created by alwayking on 16/3/14.
 */
public class AKGifRequest extends AKRequest {
    public AKGifRequest(final String url, final AKResponseListener listener, int requestMode, Context context) {
        super(url, listener, requestMode, context);
    }

    @Override
    public void dispatcherResult(InputStream in) {
//        Bitmap bitmap = BitmapFactory.decodeStream(in);
         byte[] b = DataUtils.convertToByteArray(in);
        if (this.mListener != null) {
            this.mListener.onSuccess(b);
        }
        if (this.mListenersList != null) {
            for (AKResponseListener l : this.mListenersList) {
                l.onSuccess(b);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AKGifRequest) {
            AKRequest request = (AKGifRequest) o;
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
