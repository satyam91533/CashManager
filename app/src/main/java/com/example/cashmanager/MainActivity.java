package com.example.cashmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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

    @Override public void onBackPressed() {
        if (web != null) {
            web.evaluateJavascript("if (typeof currentScreen !== 'undefined' && currentScreen !== 'home') { history.back(); } else { backHome(true); }", null);
        } else {
            super.onBackPressed();
        }
    }

    private class PrintBridge {
        @JavascriptInterface
        public void createHomeShortcut(String label, String base64Png) {
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Toast.makeText(MainActivity.this, "Custom shortcuts require Android 8 or newer.", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    byte[] bytes = Base64.decode(base64Png, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmap == null) throw new IllegalArgumentException("Invalid image");
                    ShortcutManager manager = getSystemService(ShortcutManager.class);
                    if (manager == null || !manager.isRequestPinShortcutSupported()) {
                        Toast.makeText(MainActivity.this, "This launcher does not support pinned shortcuts.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent shortcutIntent = new Intent(MainActivity.this, MainActivity.class);
                    shortcutIntent.setAction(Intent.ACTION_VIEW);
                    ShortcutInfo shortcut = new ShortcutInfo.Builder(MainActivity.this, "cashmanager-" + System.currentTimeMillis())
                        .setShortLabel(label.length() > 25 ? label.substring(0, 25) : label)
                        .setIcon(Icon.createWithBitmap(bitmap))
                        .setIntent(shortcutIntent).build();
                    manager.requestPinShortcut(shortcut, null);
                    Toast.makeText(MainActivity.this, "Approve the shortcut prompt on your Home Screen.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Could not create shortcut: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

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
