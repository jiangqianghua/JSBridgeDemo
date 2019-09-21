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
import java.util.HashMap;
import java.util.Map;

// 使用 api 注入的方式处理交互
public class MainActivity extends AppCompatActivity {

    private WebView webView ;
    private Button refreshBtn,showBtn, getWebValBtn ;
    private EditText editText;
    private NativeSDK nativeSDK ;
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
        nativeSDK = new NativeSDK(this);
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
                webView.loadUrl("http://192.168.1.103:8000/api_index.html?timestamp=" + timestamp);
            }
        });

        getWebValBtn = findViewById(R.id.getWebValBtn);
        getWebValBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeSDK.getWebEditTextValue(new CallBack() {
                    @Override
                    public void invoke(final String value) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this).setMessage(value).create().show();
                            }
                        });
                    }
                });
            }
        });
    }

    // 使用url拦截的方式
    private void showWebDialog(String text){
        String jsCode = String.format("window.showWebDialog('%s')",text);
        webView.evaluateJavascript(jsCode, null);
    }

    interface CallBack{
        void invoke(String value);
    }

    class NativeSDK {
        private Context ctx;
        private int id = 1;
        private Map<Integer, CallBack> callbackMap = new HashMap<>();
        NativeSDK(Context context){
            ctx = context;
        }

        void getWebEditTextValue(CallBack callBack) {
            int callbackId = id++;
            callbackMap.put(callbackId, callBack);
            String jsCode = String.format("window.getWebEditTextValue(%s)", callbackId);
            ((MainActivity)ctx).webView.evaluateJavascript(jsCode, null);
        }

        void receiveMessage(int callbackId , String value){
            if(callbackMap.containsKey(callbackId)){
                callbackMap.get(callbackId).invoke(value);
            }
        }
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

        @JavascriptInterface
        public void getNativeEditTextValue(int callbackId){
            final MainActivity mainActivity = (MainActivity)ctx;
            String value = mainActivity.editText.getText().toString();
            final String jsCode = String.format("window.JSSDK.receiveMessage(%s,'%s')", callbackId,value);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.webView.evaluateJavascript(jsCode, null);
                }
            });
        }

        @JavascriptInterface
        public void receiveMessage(int callbackId , String value){
            nativeSDK.receiveMessage(callbackId,value);
        }
    }
}
