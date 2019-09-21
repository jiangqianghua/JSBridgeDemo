package com.jqh.jsbridgedemo;

import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

// 使用 js 注入api方式
public class SchemaMainActivity extends AppCompatActivity {

    private WebView webView ;
    private Button refreshBtn,showBtn ;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        long timestamp = new Date().getTime();
        webView.loadUrl("http://192.168.1.103:8000/index.html?timestamp=" + timestamp);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if(!message.startsWith("jqh://")) {
                    return super.onJsAlert(view, url, message, result);
                }
                result.confirm();
                String text = message.substring(message.indexOf("=") + 1);
                showNativeDialog(text);

                return true;
            }
        });
        webView.setWebContentsDebuggingEnabled(true);

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
                webView.loadUrl("http://192.168.1.103:8000/schema_index.html?timestamp=" + timestamp);
            }
        });
    }

    // 使用url拦截的方式
    private void showWebDialog(String text){
        String jsCode = String.format("window.showWebDialog('%s')",text);
        webView.evaluateJavascript(jsCode, null);
    }

    private void showNativeDialog(String text){
        new AlertDialog.Builder(this).setMessage(text).create().show();
    }
}
