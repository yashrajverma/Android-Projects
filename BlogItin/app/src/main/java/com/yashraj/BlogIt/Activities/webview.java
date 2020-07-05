package com.yashraj.BlogIt.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yashraj.BlogIt.R;


public class webview extends AppCompatActivity {
    private WebView webView=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView=findViewById(R.id.webViewID);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClientImpl(this));
        webView.loadUrl("https://www.google.com/");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((keyCode==KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()){
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    class WebViewClientImpl extends WebViewClient{
        private Activity activity;
        public WebViewClientImpl(Activity activity){
            this.activity=activity;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.indexOf("google.com") > -1 ) return false;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
            return true;
        }
    }


}