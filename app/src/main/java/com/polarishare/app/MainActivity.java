package com.polarishare.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.File;


public class MainActivity extends Activity {

    private WebView mWebView;
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.activity_main_webview);
        mWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);     // HTML5 local storage 설정
        webSettings.setDatabaseEnabled(true);       // database storage 사용 설정
        webSettings.setUserAgentString(USER_AGENT);     // 구글 인증 위한 user agent 정보 설정

        // 롤리팝 이후 app cache 설정 변경에 따른 코드 추가
        File dir = getCacheDir();
        if (!dir.exists()) dir.mkdirs();
        webSettings.setAppCachePath(dir.getPath());
        webSettings.setAppCacheEnabled(true);


        // auth0 관련 third party cookie 허용
        // 롤리팝 이전에는 default 사용 가능, 이후 버전에만 적용
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
           // Log.d(AppConstants.TAG,"SDk version above android L so forcibaly enabling ThirdPartyCookies");
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView,true);
        }


        // REMOTE RESOURCE
        mWebView.loadUrl("http://share.decompany.io");
        //mWebView.loadUrl("http://10.0.2.2:8000");

        mWebView.setWebViewClient(new MyWebViewClient() {
                                      public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                          view.loadUrl(url);
                                          return false; // then it is not handled by default action
                                      }
                                  }
        );


        // 크롬 유저 한하여 alert 사용 위한 설정 (임시)
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        // LOCAL RESOURCE
        // mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
