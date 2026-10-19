package com.example.cashmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView web;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        web.addJavascriptInterface(new PrintBridge(), "AndroidPrint");
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    private class PrintBridge {
        @JavascriptInterface
        public void printPage() {
            runOnUiThread(() -> {
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                if (printManager != null && web != null) {
                    android.print.PrintDocumentAdapter adapter = web.createPrintDocumentAdapter("Cash Manager History");
                    printManager.print("Cash Manager History", adapter, new android.print.PrintAttributes.Builder().build());
                }
            });
        }
    }
}
