package templateprj.ulip.hkbu.com.templateproject.splash;
import templateprj.ulip.hkbu.com.templateproject.DeviceAPI;
import templateprj.ulip.hkbu.com.templateproject.GlobalValue;
import templateprj.ulip.hkbu.com.templateproject.R;
import templateprj.ulip.hkbu.com.templateproject.VC;
import templateprj.ulip.hkbu.com.templateproject.WebAppInterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.os.Handler;
import android.content.pm.ActivityInfo;
import java.io.File;

public class MainActivity extends Activity {

    Activity activity;
    WebView webV;
    Handler mHandler;
    Runnable myTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        activity=this;
        DeviceAPI.init(activity);
        DeviceAPI.addActivity(activity);
        webV=(WebView)findViewById(R.id.webV);
        WebSettings webSettings = webV.getSettings();
        webV.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webV.setVerticalScrollBarEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webV.getSettings().setJavaScriptEnabled(true);
        webV.loadUrl("file:///android_asset/splash_web/university.html");
        webV.setLongClickable(false);

        toNext();
    }

    @Override
    public void onBackPressed(){}

    public void toNext(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                File usedfile=new File(DeviceAPI.appdir,"usedfile.json");
                Intent intent = new Intent();
                if(!usedfile.exists() || GlobalValue.isOnDemo)intent.setClass(activity, WTAActivity.class);
                else{
                    WebAppInterface.hidesBackButton=true;
                    WebAppInterface.hidesNavigationBar=true;
                    intent.setClass(activity, VC.class);
                }
                activity.startActivity(intent);
                finish();
            }
        }, 3100);
    }

}