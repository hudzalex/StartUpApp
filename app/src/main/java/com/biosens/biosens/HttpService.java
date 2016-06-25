package com.biosens.biosens;

import android.app.Service;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;



import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class HttpService extends Service implements Runnable {
    private Context ctx;
    private SQLiteDatabase db;
    private Cursor cursor;
    boolean syncTest = false;
    SessionManagement session;
    boolean isRunning;
    int countTables=0;
    String tableName="";
    String id="";
String userid;

    ScheduledExecutorService scheduler;
    ScheduledFuture<?> handle;


    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void onCreate() {
        super.onCreate();
        ctx = this;
       session = new SessionManagement(getApplicationContext());
         HashMap<String, String> user = session.getUserDetails();


          userid = user.get(SessionManagement.KEY_ID);

        isRunning = true;

        //schedule(15000);
    }

    private void schedule(int period) {
        if (handle != null) {
            handle.cancel(false);
            handle = null;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        handle = scheduler.scheduleWithFixedDelay(this, period, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
    }


    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void run() {
        int period = 15000;

        if (isRunning) {

            HttpURLConnection conn = null;
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(getApplicationContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            try {

                if(countTables==0){
                    tableName="place";
                    cursor = db.query("place",
                            new String[]{"_id", "name", "longitude", "latitude", "user_id", "photo", "sync"},
                            "sync= ?",
                            new String[]{"0"},
                            null, null, null);


                } else if(countTables==1){
                    tableName="research";
                    cursor = db.query("research",
                            new String[]{"_id", "place_id", "start_time", "end_time", "culture_id", "user_id","have_toxin","description", "sync"},
                            "sync= ?",
                            new String[]{"0"},
                            null, null, null);
                }else if(countTables==2){

                    tableName="measurement";
                    cursor = db.query("measurement",
                            new String[]{"_id", "research_id","start_time","end_time","unit","value","boundary_value","longitude", "latitude", "user_id", "description", "sync"},
                            "sync= ?",
                            new String[]{"0"},
                            null, null, null);
                }else if(countTables==3){
                    tableName="culture";
                    cursor = db.query("culture",
                            new String[]{"_id", "name","user_id", "photo", "sync"},
                            "sync= ?",
                            new String[]{"0"},
                            null, null, null);
                }


                cursor.moveToFirst();

                if (!cursor.isAfterLast()) {
//                    do {
                    String s = cursor.getString(1);
                    String a = cursor.getString(6);
                    URL url = new URL("http://httpbin.org/post");
                    //                  RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    Map<String, String> jsonParams = new HashMap<String, String>();
                    if(countTables==0){
                        id = cursor.getString(0);
                        jsonParams.put("id", id);
                        jsonParams.put("name", cursor.getString(1));
                        jsonParams.put("longitude", String.valueOf(cursor.getDouble(2)));
                        jsonParams.put("latitude", String.valueOf(cursor.getDouble(3)));
                        jsonParams.put("userId", cursor.getString(4));
                        jsonParams.put("photo", String.valueOf(cursor.getInt(5)));

                    } else if(countTables==1){
                        id = cursor.getString(0);
                        jsonParams.put("id", id);
                        jsonParams.put("placeId", cursor.getString(1));
                        jsonParams.put("startTime", cursor.getString(2));
                        jsonParams.put("endTime", cursor.getString(3));
                        jsonParams.put("cultureId", cursor.getString(4));
                        jsonParams.put("userId", cursor.getString(5));
                        jsonParams.put("haveToxin",cursor.getString(6));
                        jsonParams.put("description",cursor.getString(7));

                    }else if(countTables==2){
                        id = cursor.getString(0);
                        jsonParams.put("id", id);
                        jsonParams.put("researchId", cursor.getString(1));
                        jsonParams.put("startTime", cursor.getString(2));
                        jsonParams.put("endTime", cursor.getString(3));
                        jsonParams.put("unit", cursor.getString(4));
                        jsonParams.put("value", String.valueOf(cursor.getDouble(5)));
                        jsonParams.put("boundaryValue", String.valueOf(cursor.getDouble(6)));
                        jsonParams.put("longitude", String.valueOf(cursor.getDouble(7)));
                        jsonParams.put("latitude", String.valueOf(cursor.getDouble(8)));
                        jsonParams.put("userId",cursor.getString(9));
                        jsonParams.put("description",cursor.getString(10));

                    }else if(countTables==3){
                        id = cursor.getString(0);
                        jsonParams.put("id", id);
                        jsonParams.put("name", cursor.getString(1));
                        jsonParams.put("userId", cursor.getString(2));
                        jsonParams.put("photo", String.valueOf(cursor.getInt(3)));

                    }


                    String json = new JSONObject(jsonParams).toString();


                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(json);
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    String response = "";
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BioSensDatabaseHelper. updateSync(db,tableName,userid, id,true);

                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                    }

                    Log.d("HttpService", "Response " + response);
                    period = 1000;
//                        JsonObjectRequest postRequest = new JsonObjectRequest( Request.Method.POST, url,
//                                new JSONObject(jsonParams),
//                                new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        Log.d("HttpService", "Response " + String.valueOf(response));
//
//                                        timer.scheduleAtFixedRate(new mainTask(), 0, 1000);
//                                    }
//                                },
//                                new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        timer.scheduleAtFixedRate(new mainTask(), 0, 15000);
//                                    }
//                                });
//                        queue.add(postRequest);

                    toastHandler.sendEmptyMessage(0);

                }

            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            } catch (Exception e) {
                Log.e("HttpService", e.getMessage());

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            if(countTables>=3)
            {countTables=0;}
            else{countTables++;}

            schedule(period);
        }
    }
}
