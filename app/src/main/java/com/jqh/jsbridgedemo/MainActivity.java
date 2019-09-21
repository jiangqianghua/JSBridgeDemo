package com.jqh.jsbridgedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private WebView webView ;
    private Button refreshBtn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        long timestamp = new Date().getTime();
        webView.loadUrl("http://192.168.1.103:8000/index.html?timestamp=" + timestamp);


        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timestamp = new Date().getTime();
                webView.loadUrl("http://192.168.1.103:8000/index.html?timestamp=" + timestamp);
            }
        });
    }
}
