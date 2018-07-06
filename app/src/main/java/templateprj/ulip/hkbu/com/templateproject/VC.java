package templateprj.ulip.hkbu.com.templateproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import templateprj.ulip.hkbu.com.templateproject.splash.AboutActivity;

@SuppressLint("SetJavaScriptEnabled")

public class VC extends AppCompatActivity {

    Activity activity;
    WebView webV;
    private boolean isFirstVC=false;

    private boolean doubleBackToExitPressedOnce;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!WebAppInterface.hidesBackButton);
        getSupportActionBar().setTitle(WebAppInterface.nextPageTittle);
        if(WebAppInterface.hidesNavigationBar)getSupportActionBar().hide();
        setContentView(R.layout.activity_vc);
        if(WebAppInterface.nextPageLink.equals("index"))isFirstVC=true;

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.setAcceptFileSchemeCookies(true);

        activity=this;
        webV=(WebView)findViewById(R.id.webV);
        webV.loadUrl("file:///android_asset/web/"+WebAppInterface.nextPageLink+".html");
        webV.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webV.setVerticalScrollBarEnabled(true);
        webV.getSettings().setDatabaseEnabled(true);
        webV.getSettings().setDomStorageEnabled(true);
        webV.getSettings().setJavaScriptEnabled(true);
        webV.addJavascriptInterface(new VCWebAppInterface(this, webV), "Android");
        webV.setLongClickable(false);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                thingNTD();
            }
        }, 300);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        webV.loadUrl("about:blank");
        webV=null;
        finish();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        thingNTD();
    }

    @Override
    public void onBackPressed() {
        if(isFirstVC){
            if (doubleBackToExitPressedOnce) {
                DeviceAPI.closeApp();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
            Toast.makeText(this, "Tap BACK again to exit", Toast.LENGTH_SHORT).show();
        }else{
            webV.loadUrl("about:blank");
            webV=null;
            finish();
        }
    }

    private void thingNTD(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(WebAppInterface.nextPageLink.contains("fcmenu"))webV.loadUrl("javascript:refreshBM();");
            }
        });
    }

    protected void reloadFCWeb(final int pindex){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webV.loadUrl("about:blank");
                webV.loadUrl("file:///android_asset/web/"+WebAppInterface.nextPageLink+".html");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("bello","javascript:setPIndex("+pindex+");");
                                webV.loadUrl("javascript:setPIndex("+pindex+");");
                            }
                        });
                    }
                }, 200);
            }
        });
    }

    class VCWebAppInterface extends WebAppInterface {

        VC vc;

        public VCWebAppInterface(Activity c, WebView w) {
            super(c,w);
            vc=(VC)c;
        }

        @JavascriptInterface
        public void reloadFCWeb(int pindex){
            vc.reloadFCWeb(pindex);
        }

        @JavascriptInterface
        public void toAbout(){
            Intent intent = new Intent();
            intent.setClass(vc, AboutActivity.class);
            vc.startActivity(intent);
        }

    }

}