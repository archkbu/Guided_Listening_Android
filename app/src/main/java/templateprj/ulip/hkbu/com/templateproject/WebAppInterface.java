package templateprj.ulip.hkbu.com.templateproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.net.Uri;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;

/*
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
*/

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class WebAppInterface {

    public static String link = "";
    public static String serpage = "";

    public static boolean hidesBackButton=false;
    public static boolean hidesNavigationBar=false;
    public static String nextPageLink="index";
    public static String nextPageTittle="Home";

    Activity mContext;
    WebView webv;

    public WebAppInterface(Activity c) {
        mContext = c;
    }

    public WebAppInterface(Activity c, WebView _webv) {
        mContext = c;
        webv = _webv;
        final Intent intent = mContext.getIntent();
        webv.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (intent.hasExtra("paras")) {
                    String parasStr = intent.getStringExtra("paras");
                    boolean isJson=false;
                    try{
                        if(DeviceAPI.getJSONFromString(parasStr)!=null)isJson=true;
                    }catch(Exception e){}
                    if(isJson)webv.loadUrl("javascript:Phone.setPassedByPreviousPageObject(" + parasStr + ");");
                    else webv.loadUrl("javascript:Phone.setPassedByPreviousPageObject(\"" + parasStr + "\");");
                }
            }
        });
    }

    public WebAppInterface(Activity c, WebView _webv, final String methodName) {
        mContext = c;
        webv = _webv;
        final Intent intent = mContext.getIntent();
        webv.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                try{
                    Method method = mContext.getClass().getMethod(methodName);
                    method.invoke(mContext);
                }catch(Exception e){Log.e("bello","WebAppInterface init error: "+e.toString());}
            }
        });
    }

    public void callJavaScript(final String jStr) {
        new Thread() {
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        if(webv!=null)webv.loadUrl("javascript:" + jStr);
                    }
                });
            }
        }.start();
    }


    @JavascriptInterface
    public void toPage(String page) {
        try {
            Class<?> cls = Class.forName("templateprj.ulip.hkbu.com.templateproject."+page);
            Intent intent = new Intent();
            intent.setClass(mContext, cls);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e("bello", e.toString());
        }
    }

    @JavascriptInterface
    public void toPage(String page, String parasStr) {
        try {
            Class<?> cls = Class.forName("templateprj.ulip.hkbu.com.templateproject."+page);
            Intent intent = new Intent();
            intent.setClass(mContext, cls);
            intent.putExtra("paras", parasStr);
            mContext.startActivity(intent);
            Log.e("bello", "pe");
        } catch (Exception e) {
            Log.e("bello", e.toString());
        }
    }

    @JavascriptInterface
    public void toPageWithLink(String page, String parasStr) {
        Log.e("bello", "pe");
        try {
            try{
                JSONObject j=(JSONObject)DeviceAPI.getJSONFromString(parasStr);
                nextPageLink=j.getString("link");
                nextPageTittle=j.getString("title");
                hidesBackButton=j.getBoolean("hidesBackButton");
                hidesNavigationBar=j.getBoolean("hidesNavigationBar");
            }catch(Exception e){}
            Class<?> cls = Class.forName("templateprj.ulip.hkbu.com.templateproject."+page);
            Intent intent = new Intent();
            intent.setClass(mContext, cls);
            intent.putExtra("paras", parasStr);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e("bello", e.toString());
        }
    }

    @JavascriptInterface
    public void back() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        if(webv!=null)webv.loadUrl("about:blank");
                    }
                });
            }
        }, 100);
        mContext.finish();
    }


    @JavascriptInterface
    public void toast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void alert(String input) {
        try {
            Log.e("bello", "alert");
            JSONObject json = (JSONObject) DeviceAPI.getJSONFromString(input);
            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle(json.getString("title"));
            if (json.has("msg")) alertDialog.setMessage(json.getString("msg"));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, json.getString("cancel"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            Log.e("bello", e.toString());
        }
    }

    /*
    @JavascriptInterface
    public void prompt(String input) {
        try {
            JSONObject json = (JSONObject) DeviceAPI.getJSONFromString(input);
            String title = json.getString("title"), msg = json.getString("msg"), cancel = json.getString("cancel"), confirm = json.getString("confirm");
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View promptView = layoutInflater.inflate(R.layout.prompt, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            alertDialogBuilder.setMessage(msg)
                    .setTitle(title);
            alertDialogBuilder.setView(promptView);
            final EditText tinput = (EditText) promptView.findViewById(R.id.prompt_input);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callJavaScript("Phone.promptAnswered(" + tinput.getText() + ");");
                        }
                    })
                    .setNegativeButton(cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();
        } catch (Exception e) {
        }
    }
    */


    @JavascriptInterface
    public void setAppUsed() {
        DeviceAPI.setAppUsed();
    }

    @JavascriptInterface
    public void setAppUsed(String user_id) {
        //DeviceAPI.setAppUsedWithUserId(user_id);
    }

    @JavascriptInterface
    public void setAppUsedAndToPage(String page) {
        setAppUsed();
        toPage(page);
    }

    @JavascriptInterface
    public void setAppUsedAndToPage(String page, String user_id) {
        setAppUsed(user_id);
        toPage(page);
    }


    @JavascriptInterface
    public void getJsonFromAPP(String file, boolean isFromDocuments) {
        try {
            String jstr = "";
            if (isFromDocuments) {
                jstr = DeviceAPI.getStringFromFile(new File(DeviceAPI.appdir, file + ".json"));
            } else {
                jstr = DeviceAPI.getStringFromAssets(file + ".json");
            }
            jstr = DeviceAPI.getJSONFromString(jstr).toString();
            callJavaScript("Phone.receiveJsonFromAPP(" + jstr + ");");
        } catch (Exception e) {
        }
    }

    @JavascriptInterface
    public void getJsonsFromAPP(String files, boolean isFromDocuments) {
        try {
            JSONArray filesArr = (JSONArray) DeviceAPI.getJSONFromString(files);
            JSONArray jsArr = new JSONArray();

            for (int i = 0; i < filesArr.length(); i++) {
                if (isFromDocuments)
                    jsArr.put(DeviceAPI.getJSONFromFile(new File(DeviceAPI.appdir, filesArr.getString(i) + ".json")));
                else jsArr.put(DeviceAPI.getJSONFromAssets(filesArr.getString(i) + ".json"));
            }

            callJavaScript("Phone.receiveJsonsFromAPP(" + jsArr.toString() + ");");
        } catch (Exception e) {
            Log.e("bello", "getJsonsFromAPP: " + e.toString());
        }
    }

    @JavascriptInterface
    public void saveJsonToAPP(String file, String json) {
        try {
            File usedfile = new File(DeviceAPI.appdir, file + ".json");
            DeviceAPI.saveTextFile(json, usedfile);
        } catch (Exception e) {
        }
    }


    @JavascriptInterface
    public void getJsonFromServer(String id, String data) {
        Log.e("bello", "getJsonFromServer");
        try {
            JSONObject jData = (JSONObject) DeviceAPI.getJSONFromString(data);
            String dataStr = "";
            for (int i = 0; i < jData.names().length(); i++) {
                String key = jData.names().getString(i);
                String value = jData.get(jData.names().getString(i)).toString();
                if (i == 0) dataStr = key + "=" + value;
                else dataStr = dataStr + "&" + key + "=" + value;
            }
            final String fid = id;
            final String fdata = dataStr;
            //Log.e("bello","up: "+fdata);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = DeviceAPI.postDataToInternetWithSync(link + serpage, fdata);
                        callJavaScript("Phone.receiveJsonFromServer(\"" + fid + "\"," + res + ");");
                    } catch (Exception e) {
                        Log.e("bello", "getJsonFromServer: " + e.toString());
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            Log.e("bello", "getJsonFromServer: " + e.toString());
        }
    }


    @JavascriptInterface
    public void getAndSaveJsonFromServer(String id, String data) {
        final String fid = id;
        try {
            JSONObject jdata = (JSONObject) DeviceAPI.getJSONFromString(data);
            final String fdata = jdata.getJSONObject("data").toString();
            final String ffile = jdata.getJSONObject("file").toString();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String res = DeviceAPI.postDataToInternetWithSync(link + serpage, fdata);
                        File usedfile = new File(DeviceAPI.appdir, ffile + ".json");
                        DeviceAPI.saveTextFile(res, usedfile);
                        callJavaScript("Phone.receiveJsonFromServer('" + fid + "'," + res + ");");
                    } catch (Exception e) {
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
        }
    }


    @JavascriptInterface
    public void openBrowser(String link2) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link2)));
    }

    private void askGPSPermissions() {
        String[] permissions = {
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_LOCATION",
                "android.permission.ACCESS_GPS",
                "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"
        };
        int requestCode = 200;
        ActivityCompat.requestPermissions(mContext, permissions, requestCode);
    }



    @JavascriptInterface
    public void log(String str){
        Log.e("log",str);
    }



    @JavascriptInterface
    public void useBookmarks(String name){
        BookmarksHelper.useBookmarks(name);
    }

    @JavascriptInterface
    public void getBookmarks(String name){
        BookmarksHelper.useBookmarks(name);
        Log.e("bello","Phone.receiveBookmarks("+BookmarksHelper.getBookmarksJSONString(name)+")");
        callJavaScript("Phone.receiveBookmarks("+BookmarksHelper.getBookmarksJSONString(name)+")");
    }

    @JavascriptInterface
    public void checkHasBookmarks(String names){
        try{
            JSONArray marr=new JSONArray();
            JSONArray jArr=(JSONArray)DeviceAPI.getJSONFromString(names);
            for(int i=0; i<jArr.length(); i++){
                BookmarksHelper.useBookmarks(jArr.getString(i));
                if(BookmarksHelper.getBookmarks(jArr.getString(i)).length()>0)marr.put(true);
                else marr.put(false);
            }
            callJavaScript("Phone.receiveHasBookmarks("+marr.toString()+")");
        }catch(Exception e){}
    }

    @JavascriptInterface
    public void hasBookmarked(String name){
        try{
            String[] arr2=name.split("#####PHONEW2PBM#####");
            BookmarksHelper.useBookmarks(arr2[0]);
            boolean hasBM=BookmarksHelper.isAlreadyAddedToBookmarksWithString(arr2[0],arr2[1]);
            if(hasBM)callJavaScript("Phone.receiveHasBookmarked(true);");
            else callJavaScript("Phone.receiveHasBookmarked(false);");
        }catch(Exception e){}
    }

    @JavascriptInterface
    public void addBookmark(String name, String stringId){
        Log.e("bello","name:"+name+", stringId:"+stringId);
        BookmarksHelper.useBookmarks(name);
        BookmarksHelper.addBookmarkWithString(name,stringId);
    }

    @JavascriptInterface
    public void removeBookmark(String name, String stringId){
        BookmarksHelper.useBookmarks(name);
        BookmarksHelper.removeBookmarkWithString(name,stringId);
    }

}
