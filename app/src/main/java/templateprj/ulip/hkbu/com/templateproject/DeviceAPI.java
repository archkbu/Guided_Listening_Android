package templateprj.ulip.hkbu.com.templateproject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.util.Base64;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64OutputStream;
import android.graphics.Matrix;
import android.database.Cursor;

import org.json.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

@SuppressLint("SimpleDateFormat")
public class DeviceAPI {

    public static File appdir;

    private static Context activity;
    private static ArrayList<Activity> activities=new ArrayList<Activity>();

    public static void init(Context _activity){
        activity=_activity;
        appdir=activity.getDir("appdata", Context.MODE_PRIVATE);
        if(!appdir.exists())appdir.mkdir();
    }

    public static void setAppUsed(){
        try{
            File usedfile=new File(DeviceAPI.appdir,"usedt.json");
            DeviceAPI.saveTextFile("{\"used\":true}",usedfile);
        }catch(Exception e){}
    }

    public static void setAppUsedWithUserDic(JSONObject dic){
        try{
            File usedfile=new File(DeviceAPI.appdir,"usedt.json");
            DeviceAPI.saveTextFile(dic.toString(),usedfile);
        }catch(Exception e){}
    }

    public static File getFile(String name){
        return (new File(appdir,name));
    }

    public static boolean isFileExist(String name){
        return (getFile(name).exists());
    }

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void closeApp(){
        for(Activity activity : activities){
            activity.finish();
        }
        System.exit(0);
    }

    public static int getScreenWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static int getScreenHeight(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getListOrMapFromJSON(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return jsonToMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return jsonToList((JSONArray) json);
        } else {
            return json;
        }
    }

    private static List jsonToList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(getListOrMapFromJSON(array.get(i)));
        }
        return list;
    }

    private static Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, getListOrMapFromJSON(object.get(key)).toString());
        }
        return map;
    }

    public static String getStringFromJSON(Object jsonObject){
        return jsonObject.toString();
    }

    public static String getStringFromInputStream(InputStream is)throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[2048];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is,
                    "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }finally{
            is.close();
        }
        String text = writer.toString();
        return text;
    }

    public static String getStringFromPath(String name){
        return getStringFromFile(getFile(name));
    }

    public static String getStringFromFile(File file){
        String content = null;
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {Log.e("bello","getStringFromFile error: "+e.toString());}
        return content;
    }

    public static String getStringFromInternet(String webLink) throws Exception{
        URL url = new URL(webLink);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        String body = new String(baos.toByteArray(),encoding);
        return body;
    }

    public static File copyFileFromAssetsToSystem(String name) {
        String temp[]=name.split("\\/");
        String newName=temp[temp.length-1];
        AssetManager assetManager = activity.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(name);
            File outFile = new File(appdir, newName);
            out = new FileOutputStream(outFile);
            copyFileCore(in, out);
            in.close();
            out.flush();
            out.close();
            outFile.createNewFile();
            in=null;
            out=null;
            return outFile;
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + newName, e);
        }
        return null;
    }

    private static void copyFileCore(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while((len=in.read(buf))>0){
            out.write(buf,0,len);
        }
    }

    public static Object getJSONFromString(String string) throws Exception{
        if(string.substring(0,1).equals("{"))return (new JSONObject(string));
        else if(string.substring(0,1).equals("["))return (new JSONArray(string));
        return null;
    }

    public static Object getJSONFromFile(File file) throws Exception{
        String str=DeviceAPI.getStringFromFile(file);
        return getJSONFromString(str);
    }

    public static Object getJSONFromPath(String name) throws Exception{
        String str = DeviceAPI.getStringFromPath(name+".json");
        return getJSONFromString(str);
    }

    public static Object getJSONFromJSFile(File file) throws Exception{
        String str = DeviceAPI.getStringFromFile(file);
        String[] strArr=str.split("\\=");
        str=strArr[1];
        if(str.substring(str.length()-1).equals(";")){
            str=str.substring(0,str.length()-1);
        }
        return getJSONFromString(str);
    }

    public static Object getJSONFromJSPath(String name) throws Exception{
        String str="";
        try{
            StringBuilder buf = new StringBuilder();
            InputStream json=activity.getAssets().open(name);
            BufferedReader in=new BufferedReader(new InputStreamReader(json, "UTF-8"));
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            str = buf.toString();
        }catch(Exception e){e.printStackTrace();}
        String[] strArr=str.split("\\=");
        str=str.replaceAll(strArr[0]+"=","");
        if(str.substring(str.length()-1).equals(";")){
            str=str.substring(0,str.length()-1);
        }
        return getJSONFromString(str);
    }

    public static String getStringFromAssets(String name) throws Exception{
        String str="";
        try{
            StringBuilder buf = new StringBuilder();
            InputStream json=activity.getAssets().open(name);
            BufferedReader in=new BufferedReader(new InputStreamReader(json, "UTF-8"));
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            str = buf.toString();
        }catch(Exception e){e.printStackTrace();}
        return str;
    }

    public static Object getJSONFromAssets(String name) throws Exception{
        return getJSONFromString(getStringFromAssets(name));
    }

    public static Object getJSONFromJSAssets(String name) throws Exception{
        String str="";
        try{
            StringBuilder buf = new StringBuilder();
            InputStream json=activity.getAssets().open(name);
            BufferedReader in=new BufferedReader(new InputStreamReader(json, "UTF-8"));
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            str = buf.toString();
        }catch(Exception e){e.printStackTrace();}
        String[] strArr=str.split("\\=");
        str=str.replaceAll(strArr[0]+"=","");
        if(str.substring(str.length()-1).equals(";")){
            str=str.substring(0,str.length()-1);
        }
        return getJSONFromString(str);
    }



    public static Object getJSONFromInternet(String webLink) throws Exception{
        String str=DeviceAPI.getStringFromInternet(webLink);
        return getJSONFromString(str);
    }

    public static void saveTextFile(String inputString,File file){
        FileWriter fileWriter=null;
        try{
            fileWriter=new FileWriter(file);
            fileWriter.write(inputString);
            fileWriter.close();
        }catch(Exception e){}
        try{
            fileWriter.close();
        }catch(Exception e){}
    }

    public static void saveTextFileWithPath(String inputString, String path){
        saveTextFile(inputString,new File(appdir,path));
    }

    public static void saveJSONFile(Object json,File file){
        String str=json.toString();
        saveTextFile(str,file);
    }

    public static void saveJSONFileWithPath(Object json, String path){
        String str=json.toString();
        saveTextFile(str,new File(appdir,path+".json"));
    }

    public static String saveFileWithBase64String(String fileString, String name){
        File fileDir=new File(appdir,"apistoragepool");
        if(!fileDir.exists())fileDir.mkdir();
        File fileInfo=new File(fileDir,"apistoragepoolinfo.json");
        if(!fileInfo.exists())saveTextFile("[]",fileInfo);
        String fileName=name;
        String fileExtension=(fileString.split("\\;")[0]).split("\\/")[1];
        fileExtension=fileExtension.toLowerCase();
        if(fileExtension.equals("jpeg"))fileExtension="jpg";
        try{
            JSONArray infoArr=(JSONArray)getJSONFromFile(fileInfo);
            JSONObject fileDetail=new JSONObject();
            fileDetail.put("name",fileName+".txt");
            fileDetail.put("extension", fileExtension);
            File fileObject=new File(fileDir,fileName+".txt");
            saveTextFile(fileString, fileObject);
            infoArr.put(fileDetail);
            saveJSONFile(infoArr, fileInfo);
            Log.e("apperror", fileName + ".txt");
            return fileName;
        }catch(Exception e){Log.e("apperror","Cannot save file.");return null;}
    }

    public static String saveFileWithBase64String(String fileString){
        String fileName=getTodayDateTimeString("yyyyMMddHHmmssSSS");
        return saveFileWithBase64String(fileString,fileName);
    }

    public static String getBase64FileFromAPIStoragePool(String name){
        File fileDir=new File(appdir,"apistoragepool");
        if(!fileDir.exists())return null;
        File fileInfo=new File(fileDir,"apistoragepoolinfo.json");
        if(!fileInfo.exists())return null;
        name=name+".txt";
        try{
            JSONArray infoArr=(JSONArray)getJSONFromFile(fileInfo);
            for(int i=0; i<infoArr.length(); i++){
                JSONObject fileDetail=infoArr.getJSONObject(i);
                if(fileDetail.getString("name").equals(name)){
                    File sfile=new File(fileDir,name);
                    return getStringFromFile(sfile);
                }
            }
        }catch(Exception e){Log.e("apperror","Cannot open file.");}
        return null;
    }

    public static boolean isConnectedToInternet(Activity activity){
        ConnectivityManager cm;
        cm=(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();
        if(info==null)return false;
        else{
            if(info.isConnected())return true;
            else return false;
        }
    }

    public static Drawable loadImageFromAssets(Activity activity, String link){
        try{
            InputStream ims = activity.getAssets().open(link);
            Drawable draw = Drawable.createFromStream(ims, "src");
            return draw;
        }catch (Exception e) {
            Log.e("loadingImg", e.toString());
            return null;
        }
    }

    public static String getTodayDateTimeString(String format){
        DateFormat dateFormat=new SimpleDateFormat(format);
        Date date=new Date();
        return dateFormat.format(date);
    }

    public static Date getDateTimeDate(String dateString, String format){
        try{
            DateFormat dateFormat=new SimpleDateFormat(format);
            return dateFormat.parse(dateString);
        }catch(ParseException e){return null;}
    }

    public static String convertDateStringFormat(String dateString, String format1, String format2){
        Date old_date=DeviceAPI.getDateTimeDate(dateString, format1);
        DateFormat dateFormat=new SimpleDateFormat(format2);
        return dateFormat.format(old_date);
    }

    public static int getDifferenceDays(Date d1, Date d2){
        if(d2==null)return 0;
        int daysdiff=0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays=0;
        double ddd=diff / (24 * 60 * 60 * 1000);
        if(ddd>0)diffDays = diff / (24 * 60 * 60 * 1000)+1;
        else if(ddd<0)diffDays = diff / (24 * 60 * 60 * 1000);
        else return 0;
        daysdiff = (int)diffDays;
        return daysdiff;
    }

    public static int getDaysBetweenTodayAndADate(String aDate, String format){
        Date date=getDateTimeDate(aDate, format);
        return DeviceAPI.getDifferenceDays(new Date(), date);
    }

    public static int getMinIndexInArray(ArrayList<Double> arr){
        for(int i=0; i<arr.size(); i++){
            double thisNum=arr.get(i);
            int numAdded=0;
            for(int k=0; k<arr.size(); k++){
                if(thisNum<=arr.get(k))numAdded++;
            }
            if(numAdded==arr.size())return i;
        }
        return 0;
    }

    public static int getMinIndexInArray(JSONArray arr){
        try{
            for(int i=0; i<arr.length(); i++){
                double thisNum=arr.getDouble(i);
                int numAdded=0;
                for(int k=0; k<arr.length(); k++){
                    if(thisNum<=arr.getDouble(k))numAdded++;
                }
                if(numAdded==arr.length())return i;
            }
        }catch(Exception e){}
        return 0;
    }

    public static String postDataToInternetWithSync(String targetURL, String urlParameters){
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("charset", "UTF-8");
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
            if(urlParameters!=null)wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();
            connection.connect();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            Log.e("bello",e.toString());
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    /*
    public static int postFileAndDataToInternetWithSync(String sourceFileUri, String fileName, String mediaPostName, String data) {
        int serverResponseCode=0;
        HttpURLConnection connection = null;
        String boundary = "---------------------------14737809831466499882746641449";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        Log.e("bello","fileName="+fileName);

        if (!sourceFile.isFile()) {
            Log.e("bello", "Source File not exist");
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(GlobalValue.serverLink);

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


                DataOutputStream outputStream;


                outputStream = new DataOutputStream(connection.getOutputStream());


                JSONObject jData = (JSONObject) DeviceAPI.getJSONFromString(data);
                String dataStr = "";
                for (int i = 0; i < jData.names().length(); i++) {
                    String key = jData.names().getString(i);
                    String value = jData.get(jData.names().getString(i)).toString();
                    outputStream.writeBytes("--"+boundary+"\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; name=\""+key+"\"\r\n\r\n");
                    outputStream.writeBytes(value+"\r\n");
                }

                outputStream.writeBytes("\r\n--"+boundary+"\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\""+mediaPostName+"\"; filename=\""+fileName+"\"\r\n");
                outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                fileInputStream = new FileInputStream(sourceFileUri);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes("\r\n--"+boundary+"--\r\n");


                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                Log.e("bello","serverResponseCode=" + serverResponseCode);
                Log.e("bello","serverResponseMessage=" + serverResponseMessage);

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                Log.e("bello","response="+response.toString());

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

            } catch (Exception e) {
                Log.e("bello","Upload file to server 1 error: " + e.getMessage());
            }
            return serverResponseCode;
        }
    }
    */

    public static void openCameraToTakeAPhoto(Activity act){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new File(Environment.getExternalStorageDirectory()+File.separator + "tmpimage.jpg");
            } catch (Exception e) {}
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                act.startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    public static void openAlbumToPickAPhoto(Activity act){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        act.startActivityForResult(i, 1);
    }

    public static HashMap<String,String> getBase64StringFromCameraPhotoResult(Activity act, int requestCode, int resultCode, Intent data, File image){
        if(requestCode==1 && resultCode==act.RESULT_OK){
            Bitmap bm=imageOreintationValidator(BitmapFactory.decodeFile(image.getAbsolutePath()),image.getAbsolutePath());
            String imgBase64String=encodeBitmapTobase64(bm);
            HashMap<String,String> value=new HashMap<String,String>();
            value.put("smallImage", encodeBitmapTobase64(scaleBitmap(bm, 0.3)));
            value.put("image", imgBase64String);
            return value;
        }
        return null;
    }

    public static HashMap<String,String> getAndSaveBase64StringFromCameraPhotoResult(Activity act, int requestCode, int resultCode, Intent data, File image) {
        HashMap<String,String> value=getBase64StringFromCameraPhotoResult(act, requestCode, resultCode, data, image);
        if(value==null)return null;
        image.delete();
        String name=saveFileWithBase64String(value.get("image"));
        String smallName=saveFileWithBase64String(value.get("smallImage"), name+"s");
        value.put("image",value.get("image"));
        value.put("name",name);
        value.put("smallImage",value.get("smallImage"));
        value.put("smallName",smallName);
        return value;
    }

    public static HashMap<String,String> getBase64StringFromAlbumResult(Activity act, int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == act.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = act.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bm=BitmapFactory.decodeFile(picturePath);
            String imgBase64String=encodeBitmapTobase64(bm);
            HashMap<String,String> value=new HashMap<String,String>();
            value.put("smallImage", encodeBitmapTobase64(scaleBitmap(bm, 0.3)));
            value.put("image", imgBase64String);
            return value;
        }
        return null;
    }

    public static HashMap<String,String> getAndSaveBase64StringFromAlbumResult(Activity act, int requestCode, int resultCode, Intent data) {
        HashMap<String,String> value=getBase64StringFromAlbumResult(act, requestCode, resultCode, data);
        if(value==null)return null;
        String name=saveFileWithBase64String(value.get("image"));
        String smallName=saveFileWithBase64String(value.get("smallImage"), name+"s");
        value.put("image",value.get("image"));
        value.put("name",name);
        value.put("smallImage",value.get("smallImage"));
        value.put("smallName",smallName);
        return value;
    }

    public static String scaleBase64StringImage(String str, int newWidth, int newHeight){
        Bitmap img=decodeBase64ToBitmap(str);
        img=scaleBitmap(img, newWidth, newHeight);
        return encodeBitmapTobase64(img);
    }

    public static String scaleBase64StringImage(String str, double scale){
        Bitmap img = decodeBase64ToBitmap(str);
        int newWidth=(int)(Math.round(img.getWidth()*scale));
        int newHeight=(int)(Math.round(img.getHeight()*scale));
        img=scaleBitmap(img, newWidth, newHeight);
        return encodeBitmapTobase64(img);
    }

    public static Bitmap scaleBitmap(Bitmap bitmapToScale, int newWidth, int newHeight){
        return Bitmap.createScaledBitmap(bitmapToScale, newWidth, newHeight, false);
    }

    public static Bitmap scaleBitmap(Bitmap bitmapToScale, double scale){
        int newWidth=(int)(Math.round(bitmapToScale.getWidth()*scale));
        int newHeight=(int)(Math.round(bitmapToScale.getHeight()*scale));
        return Bitmap.createScaledBitmap(bitmapToScale, newWidth, newHeight, false);
    }

    public static Bitmap scaleBitmap(Bitmap bitmapToScale, int maxWidthHeight){
        int bw=bitmapToScale.getWidth();
        int bh=bitmapToScale.getHeight();
        double scale=0.9;
        if(bw>=bh){
            while(bw>maxWidthHeight){
                bw=(int)(Math.round(bitmapToScale.getWidth()*scale));
                bh=(int)(Math.round(bitmapToScale.getHeight()*scale));
                scale-=0.1;
            }
        }else{
            while(bh>maxWidthHeight){
                bw=(int)(Math.round(bitmapToScale.getWidth()*scale));
                bh=(int)(Math.round(bitmapToScale.getHeight()*scale));
                scale-=0.1;
            }
        }
        Log.e("bello","bw="+bw+", bh="+bh+", scale="+scale);
        return Bitmap.createScaledBitmap(bitmapToScale, bw, bh, false);
    }

    public static String encodeBitmapTobase64(Bitmap image){
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
        return "data:image/jpeg;charset=utf-8;base64,"+imageEncoded;
    }

    public static Bitmap decodeBase64ToBitmap(String input){
        input=input.split("\\,")[1];
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeFileToBase64(File file){
        try{
            InputStream inputStream = null;//You can get an inputStream using any IO API
            inputStream = new FileInputStream(file.getAbsolutePath());
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output64.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            output64.close();
            return "data:image/jpeg;charset=utf-8;base64,"+output.toString();
        }catch(Exception e){}
        return null;
    }

    public static Bitmap imageOreintationValidator(Bitmap bitmap, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void pushANotification(){

    }

}