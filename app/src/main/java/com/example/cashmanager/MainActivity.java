package com.example.cashmanager;

import android.app.Activity;
import android.content.Context;\nimport android.content.Intent;\nimport android.content.pm.ShortcutInfo;\nimport android.content.pm.ShortcutManager;\nimport android.graphics.Bitmap;\nimport android.graphics.BitmapFactory;\nimport android.graphics.drawable.Icon;\nimport android.os.Build;\nimport android.util.Base64;\nimport android.widget.Toast;
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

    private class PrintBridge {\n        @JavascriptInterface\n        public void createHomeShortcut(String label, String base64Png) {\n            runOnUiThread(() -> {\n                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {\n                    Toast.makeText(MainActivity.this, "Custom shortcuts require Android 8 or newer.", Toast.LENGTH_LONG).show();\n                    return;\n                }\n                try {\n                    byte[] bytes = Base64.decode(base64Png, Base64.DEFAULT);\n                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);\n                    if (bitmap == null) throw new IllegalArgumentException("Invalid image");\n                    ShortcutManager manager = getSystemService(ShortcutManager.class);\n                    if (manager == null || !manager.isRequestPinShortcutSupported()) {\n                        Toast.makeText(MainActivity.this, "This launcher does not support pinned shortcuts.", Toast.LENGTH_LONG).show();\n                        return;\n                    }\n                    Intent shortcutIntent = new Intent(MainActivity.this, MainActivity.class);\n                    shortcutIntent.setAction(Intent.ACTION_VIEW);\n                    ShortcutInfo shortcut = new ShortcutInfo.Builder(MainActivity.this, "cashmanager-" + System.currentTimeMillis())\n                        .setShortLabel(label.length() > 25 ? label.substring(0, 25) : label)\n                        .setIcon(Icon.createWithBitmap(bitmap))\n                        .setIntent(shortcutIntent).build();\n                    manager.requestPinShortcut(shortcut, null);\n                    Toast.makeText(MainActivity.this, "Approve the shortcut prompt on your Home Screen.", Toast.LENGTH_LONG).show();\n                } catch (Exception e) {\n                    Toast.makeText(MainActivity.this, "Could not create shortcut: " + e.getMessage(), Toast.LENGTH_LONG).show();\n                }\n            });\n        }
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
