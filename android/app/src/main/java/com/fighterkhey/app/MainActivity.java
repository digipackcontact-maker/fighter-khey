package com.fighterkhey.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

// Fighter Khey : charge la dernière version en ligne, et la copie embarquée quand il n'y a pas de réseau.
public class MainActivity extends Activity {
    private static final String EN_LIGNE = "https://digipackcontact-maker.github.io/fighter-khey/";
    private static final String HORS_LIGNE = "file:///android_asset/index.html";
    private WebView web;
    private boolean secours = false;

    @Override
    protected void onCreate(Bundle etat) {
        super.onCreate(etat);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#0c0c10"));
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame()) versLeSecours(v);
            }
            @Override
            public void onReceivedHttpError(WebView v, WebResourceRequest r, WebResourceResponse rep) {
                if (r.isForMainFrame() && rep.getStatusCode() >= 400) versLeSecours(v);
            }
        });
        setContentView(web);
        cacherBarres();
        if (etat != null) web.restoreState(etat); else web.loadUrl(EN_LIGNE);
    }

    private void versLeSecours(WebView v) {
        if (secours) return;
        secours = true;
        v.loadUrl(HORS_LIGNE);
    }

    private void cacherBarres() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (focus) cacherBarres();
    }

    @Override
    protected void onPause() {
        super.onPause();
        web.evaluateJavascript("try{AC&&AC.suspend()}catch(e){}", null);
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
        web.evaluateJavascript("try{AC&&AC.resume()}catch(e){}", null);
        cacherBarres();
    }

    @Override
    protected void onSaveInstanceState(Bundle sortie) {
        super.onSaveInstanceState(sortie);
        web.saveState(sortie);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}
