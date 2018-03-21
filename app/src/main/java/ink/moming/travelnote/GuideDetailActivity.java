package ink.moming.travelnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 攻略文章展示
 * 采用WebView 直接显示百度攻略网页版
 *
 */

public class GuideDetailActivity extends AppCompatActivity {

    private static final String TAG = GuideDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_detail);


        Intent web =  getIntent();
        final String url = web.getStringExtra("url");
        Log.d(TAG, getString(R.string.opening_article_str)+ url);

        WebView webView = findViewById(R.id.guide_article);

        if (webView!=null&&url!=null){
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    view.loadUrl(url);
                    return true;
                }
            });

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

}
