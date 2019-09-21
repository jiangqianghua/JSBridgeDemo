package com.jqh.jsbridgedemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;
// 使用 api 注入的方式处理交互
public class MainActivity extends AppCompatActivity {

    private WebView webView ;
    private Button refreshBtn,showBtn ;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        long timestamp = new Date().getTime();
        webView.loadUrl("http://192.168.1.103:8000/api_index.html?timestamp=" + timestamp);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebContentsDebuggingEnabled(true);
        // 添加js拦截监听事件
        webView.addJavascriptInterface(new NativeBridge(this),"NativeBridge");
        editText = findViewById(R.id.editText);
        showBtn = findViewById(R.id.showBtn);

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputVal = editText.getText().toString();
                showWebDialog(inputVal);
            }
        });
        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timestamp = new Date().getTime();
                webView.loadUrl("http://192.168.1.103:8000/index.html?timestamp=" + timestamp);
            }
        });
    }

    // 使用url拦截的方式
    private void showWebDialog(String text){
        String jsCode = String.format("window.showWebDialog('%s')",text);
        webView.evaluateJavascript(jsCode, null);
    }



    class NativeBridge{
        private Context ctx;

        public NativeBridge(Context ctx) {
            this.ctx = ctx;
        }
        @JavascriptInterface
        public void showNativeDialog(String text){
            new AlertDialog.Builder(ctx).setMessage(text).create().show();
        }
    }
}
