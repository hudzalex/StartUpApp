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

import javax.net.ssl.HttpsURLConnection;

public class HttpService extends Service {
    private Context ctx;
    private SQLiteDatabase db;
    private Cursor cursor;
    boolean syncTest = false;
    SessionManagement session;
    boolean isRunning;

    //  String userid;
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //  String url = "http://httpbin.org/post";
    // RequestQueue queue = Volley.newRequestQueue(this);
    public void onCreate() {
        super.onCreate();
        ctx = this;
        //  session = new SessionManagement(getApplicationContext());
        //  HashMap<String, String> user = session.getUserDetails();


        //  userid = user.get(SessionManagement.KEY_ID);

        isRunning = true;
        new HttpWorker().execute();
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


    private class HttpWorker extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            while (isRunning) {

                HttpURLConnection conn = null;
                int sleepTime = 15000;

                try {
                    SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(getApplicationContext());
                    db = biosensDatabaseHelper.getReadableDatabase();

                    cursor = db.query("place",
                            new String[]{"_id", "name", "longitude", "latitude", "user_id", "photo", "sync"},
                            "sync= ?",
                            new String[]{"0"},
                            null, null, null);

                    cursor.moveToFirst();

                    if (!cursor.isAfterLast()) {
//                    do {
                        String s = cursor.getString(1);
                        String a = cursor.getString(6);
                        URL url = new URL("http://httpbin.org/post");
      //                  RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        Map<String, String> jsonParams = new HashMap<String, String>();

                        String id = cursor.getString(0);
                        jsonParams.put("id", id);
                        jsonParams.put("name", cursor.getString(1));
                        jsonParams.put("longitude", String.valueOf(cursor.getDouble(2)));
                        jsonParams.put("latitude", String.valueOf(cursor.getDouble(3)));
                        jsonParams.put("userId", cursor.getString(4));
                        jsonParams.put("photo", String.valueOf(cursor.getInt(5)));

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
                            String line;
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                response += line;
                            }
                        }

                        Log.d("HttpService", "Response " + response);
                        sleepTime = 1000;
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
//                    } while (cursor.moveToNext());
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

                try {

                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    return null;
                }
            }

            return null;
        }

    }
}
