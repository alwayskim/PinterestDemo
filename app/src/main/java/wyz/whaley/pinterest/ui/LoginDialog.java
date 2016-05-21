package wyz.whaley.pinterest.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.data.TokenInfo;
import wyz.whaley.pinterest.http.AKHttpRequestEngine;
import wyz.whaley.pinterest.http.AKHttpTask;
import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.http.utils.AKHttpConstant;
import wyz.whaley.pinterest.http.utils.AKJSONRequestNew;
import wyz.whaley.pinterest.utils.DataUtils;
import wyz.whaley.pinterest.utils.SharedPreferenceUtils;

/**
 * Created by alwayking on 16/3/11.
 */
public class LoginDialog extends Dialog {

    private WebView mLoginWebView;

    private ProgressBar mProgressBar;

    private String mToken;

    private LoginListener mListener;

    private Handler mHandler;

    public LoginDialog(Context context, LoginListener listener) {
        super(context);
        this.mListener = listener;
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }

    protected LoginDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login);
        setDialogSize();
        mLoginWebView = (WebView) findViewById(R.id.login_wv);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progressbar);
        mLoginWebView.getSettings().setJavaScriptEnabled(true);
        mLoginWebView.getSettings().setDomStorageEnabled(true);
        mLoginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                String code = decodeCode(url);
                if (code != null) {
                    mLoginWebView.setVisibility(View.INVISIBLE);
                    requestToken(code);
                } else {
                    super.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

    }

    private void setDialogSize() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().setGravity(Gravity.CENTER);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        lp.height = (int) (d.getHeight() * 0.85);
        lp.width = (int) (d.getWidth() * 0.95);
        getWindow().setAttributes(lp);
    }

    private String decodeCode(String url) {
        String code = null;
        int index = url.indexOf("?code=");
        if (index >= 0) {
            code = url.substring(index + 6);
        }
        return code;
    }

    private void requestToken(String code) {
        AKRequest request = new AKJSONRequestNew(AKHttpConstant.TOKEN_URI, new AKResponseListener() {
            @Override
            public void onSuccess(Object in) {
                String data = (String) in;
                Gson gson = new GsonBuilder().create();
                TokenInfo tokenInfo = gson.fromJson(data, TokenInfo.class);
                mToken = tokenInfo.getAccess_token();
                AKHttpConstant.USER_ACCESS_TOKEN = mToken;
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(SharedPreferenceUtils.COMMON_PREFERENCE, Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(SharedPreferenceUtils.COMMON_TOKEN, mToken).commit();
                LoginDialog.this.dismiss();
                if (mListener != null) {
                    mListener.success();
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        }, AKHttpRequestEngine.REQUEST_POST, getContext());
        Map<String, String> map = new HashMap<>();
        map.put(AKHttpConstant.CLIENT_CODE_PARAM, code);
        map.put(AKHttpConstant.CLIENT_ID_PARAM, AKHttpConstant.CLIENT_ID);
        map.put(AKHttpConstant.CLIENT_SECRET_PARAM, AKHttpConstant.CLIENT_SECRET);
        request.setParams(map);
        AKHttpTask.getInstace().add(request);
//        AKHttpTask.getInstace().addToRequestQueue(request);
    }

    @Override
    public void show() {
        super.show();
        Map<String, String> map = new HashMap<>();
        map.put(AKHttpConstant.CLIENT_ID_PARAM, AKHttpConstant.CLIENT_ID);
        map.put(AKHttpConstant.SCOPE, AKHttpConstant.SCOPE_TYPE);
        map.put(AKHttpConstant.REDIRECT_URI, AKHttpConstant.REDIRECT_URI_PARAM);
        String url = AKHttpConstant.AUTH_URI + DataUtils.dealHttpParams(map);
        mLoginWebView.loadUrl(url);
    }

    public interface LoginListener {
        void success();
    }
}
