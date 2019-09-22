package com.jqh.jsbridgedemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

public class DSBridgeActivity extends AppCompatActivity {
    private DWebView webView ;
    private Button refreshBtn, getWebValBtn ;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsbridge);

        webView = findViewById(R.id.webview);
        long timestamp = new Date().getTime();
        webView.loadUrl("http://192.168.1.104:8000/dsbridge_index.html?timestamp=" + timestamp);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebContentsDebuggingEnabled(true);
        // 添加js拦截监听事件
        webView.addJavascriptObject(new JSApi(this),null);
        editText = findViewById(R.id.editText);

        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timestamp = new Date().getTime();
                webView.loadUrl("http://192.168.1.104:8000/dsbridge_index.html?timestamp=" + timestamp);
            }
        });

        getWebValBtn = findViewById(R.id.getWebValBtn);
        getWebValBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputVal = editText.getText().toString();
                webView.callHandler("getWebEditTextValue", new Object[]{inputVal}, new OnReturnValue<Object>() {
                    @Override
                    public void onValue(Object retValue) {
                        showDialog(retValue.toString());
                    }
                });

            }
        });
    }

    // 使用url拦截的方式
    private void showDialog(String text){
        new AlertDialog.Builder(this).setMessage(text).create().show();
    }


    class JSApi {
        private Context ctx;

        public JSApi(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void getNativeEditTextValue(Object msg, CompletionHandler<String> handler){
            String inputVal = ((DSBridgeActivity)ctx).editText.getText().toString();
            handler.complete(inputVal);
        }
    }
}
