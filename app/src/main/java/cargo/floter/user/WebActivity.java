package cargo.floter.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cargo.floter.user.utils.AppConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class WebActivity extends CustomActivity {
    private Toolbar toolbar;
    private TextView txt_title;
    private WebView webview;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_title = (TextView) toolbar.findViewById(R.id.txt_title);
        txt_title.setText(getIntent().getStringExtra(AppConstants.EXTRA_1));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");


        webview = (WebView) findViewById(R.id.webView);
        progress = (ProgressBar) findViewById(R.id.progress);
//        WebSettings settings = webview.getSettings();
//        settings.setJavaScriptEnabled(true);
//        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        if (getIntent().getIntExtra(AppConstants.EXTRA_2, 0) == 1) {
            loadUrlInWebView("http://stubuz.com/floterweb/privacy-policy.html");
        } else {
            loadUrlInWebView("http://stubuz.com/floterweb/about-us.html");
        }



    }

    private void loadUrlInWebView(String webUrl) {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("", "Processing webView url click...");
                progress.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("", "Finished loading URL: " + url);
                progress.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("", "Error: " + description);
            }
        });

        webview.loadUrl(webUrl);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

    }
}
