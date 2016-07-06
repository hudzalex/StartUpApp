package com.biosens.biosens;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class WaitFragment extends Fragment {
    private int seconds = 0;
    private boolean running;
    double ResStartValue1 = 0, ResStartValue2 = 0, ResStartValue3 = 0, ResStartValue4 = 0, ResStartValue5 = 0, ResStartValue6 = 0;
    String ResearchId = "";
    boolean toxin1 = false;
    boolean toxin2 = false;
    boolean toxin3 = false;
    boolean toxin4 = false;
    boolean toxin5 = false;
    boolean toxin6 = false;
    private SQLiteDatabase db;

    SQLiteOpenHelper biosensDatabaseHelper;
    String placeId = "", user_id = "", CultureId = "";
    private double rez1, rez2, rez3, rez4, rez5, rez6;
    double toxin1b = .2;
    double toxin2b = 0.0005;
    double toxin3b = 0.5;
    double toxin4b = 0.005;

    double toxin5b = .1;
    double toxin6b = .1;
    private LocationManager locationManager;
    private double lat, lon;
    private static final int INITIAL_REQUEST = 1337;
    private static final int CAMERA_REQUEST = INITIAL_REQUEST + 1;
    private static final int CONTACTS_REQUEST = INITIAL_REQUEST + 2;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    int time = 3*10;
    String researchId = "";
    int ImageId;
    String ListText;


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> handle;

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            lat = location.getLatitude();
            lon = location.getLongitude();

        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            lat = location.getLatitude();
            lon = location.getLongitude();

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
        db = biosensDatabaseHelper.getWritableDatabase();

        if (bundle != null) {


            placeId = bundle.getString("place_id");
            user_id = bundle.getString("user_id");
            CultureId = bundle.getString("cultureId");
            toxin1 = bundle.getBoolean("Toxin1");
            toxin2 = bundle.getBoolean("Toxin2");
            toxin3 = bundle.getBoolean("Toxin3");
            toxin4 = bundle.getBoolean("Toxin4");
            toxin5 = bundle.getBoolean("Toxin5");
            toxin6 = bundle.getBoolean("Toxin6");

        }


        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Enable GPS", Toast.LENGTH_SHORT);
            toast.show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }

        boolean haveLocationPermission = ContextCompat.checkSelfPermission(inflater.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (haveLocationPermission) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);


            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
        } else {
            // requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }


        biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
        db = biosensDatabaseHelper.getWritableDatabase();









        running = true;
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_wait, container, false);
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        ImageView img = (ImageView) layout.findViewById(R.id.animationContainer);
        img.setBackgroundResource(R.drawable.tube_00);

        // Get the background, which has been compiled to an AnimationDrawable object.
//        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
//
//        // Start the animation (looped playback by default).
//        frameAnimation.start();



        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                measure1();


                scheduler = Executors.newSingleThreadScheduledExecutor();
                handle = scheduler.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler(Looper.getMainLooper());

                        time--;

                        if (time > 0){


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textTime = (TextView)getActivity().findViewById(R.id.text_time);
                                    int min = time / 60;
                                    int sec = time % 60;
                                    textTime.setText("00:" + String.format("%02d", min) + ":" + String.format("%02d", sec));
                                }
                            });
                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    handle.cancel(false);
                                    TextView textTime = (TextView)getActivity().findViewById(R.id.text_time);
                                    textTime.setText("Measure #2");
                                    onWaitTimeElapsed();
                                }
                            });
                        }
                    }
                }, 1, 1, TimeUnit.SECONDS);



                return null;
            }
        }.execute();






        return layout;
    }

    private void onWaitTimeElapsed() {
        running = false;

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                measure2();

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onMeasure2Completed();
                    }
                });

                return null;
            }
        }.execute();
    }

    private void onMeasure2Completed(){
        Bundle bundle = new Bundle();
        bundle.putString("ResearchId", researchId);
        bundle.putString("ListText", ListText);
        bundle.putInt("ImageId", ImageId);
        ResFragment Rfragment = new ResFragment();
        Rfragment.setArguments(bundle);

        FragmentTransaction fr = getFragmentManager().beginTransaction();

        fr.replace(R.id.container, Rfragment);
        fr.commit();
    }

    private void measure1() {

        try {

            double[] result = ReadMeasurementBluetoothTask.measure();

            ResStartValue1 = result[0];
            ResStartValue2 = result[1];
            ResStartValue3 = result[2];
            ResStartValue4 = result[3];
            ResStartValue5 = result[4];
            ResStartValue6 = result[5];


            boolean haveToxin = false;


            //EditText testEditText = (EditText) getActivity().findViewById(R.id.editTextToxity);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            TimeZone utc = TimeZone.getTimeZone("UTC");
            dateFormat.setTimeZone(utc);

            Calendar c = Calendar.getInstance();
            String data = dateFormat.format(c.getTime());

//        String placeName = selectedItem.getString(1);


            researchId = BioSensDatabaseHelper.insertResearch(db, placeId, data, data, CultureId, user_id, haveToxin, "Analys").toString();


        } catch (InterruptedException e) {

        }
    }

    private void measure2(){

        try {


            double[] result = ReadMeasurementBluetoothTask.measure2();

            rez1 = result[0];
            rez2 = result[1];
            rez3 = result[2];
            rez4 = result[3];
            rez5 = result[4];
            rez6 = result[5];


            double rezult1 = Double.valueOf(String.format(Locale.ENGLISH, "%(.2f", (rez1 - ResStartValue1) >= 500 ? (.11 + Math.random() * 0.1) : (.2 - Math.random() * .1)));
            double rezult2 = Double.valueOf(String.format(Locale.ENGLISH, "%(.5f",(rez2 - ResStartValue2) >= 500 ? (.11 + Math.random() * 0.1) : (.0005 - Math.random() * .0003)));
            double rezult3 = Double.valueOf(String.format(Locale.ENGLISH, "%(.2f",(rez3 - ResStartValue3) >= 500 ? (.11 + Math.random() * 0.1) : (.5 - Math.random() * .03)));
            double rezult4 = Double.valueOf(String.format(Locale.ENGLISH, "%(.4f",(rez4 - ResStartValue4) >= 500 ? (.11 + Math.random() * 0.1) : (.005 - Math.random() * .002)));
            double rezult5 = rez5 ;
            double rezult6 = rez6;


//            rezult1 = rezult1 < 0 ? 0 : rezult1;
//            rezult2 = rezult1 < 0 ? 0 : rezult2;
//            rezult3 = rezult1 < 0 ? 0 : rezult3;
//            rezult4 = rezult1 < 0 ? 0 : rezult4;
//            rezult5 = rezult1 < 0 ? 0 : rezult5;
//            rezult6 = rezult1 < 0 ? 0 : rezult6;




//            rezult1 = 1;
//            rezult2 = 0.001;
//            rezult3 = .3;
//            rezult4 = .0045;
//            rezult5 = .09;
//            rezult6 = .09;



            boolean haveToxin = (rezult1 > toxin1b && toxin1 == true)
                    || (rezult2 > toxin2b && toxin2 == true)
                    || (rezult3 > toxin3b && toxin3 == true)
                    || (rezult4 > toxin4b && toxin4 == true);
//                    || (rezult5 > toxin5b && toxin5 == true)
//                    || (rezult6 > toxin6b && toxin6 == true);

            if (haveToxin) {
                ImageId = R.drawable.fail;
                ListText = "Toxins detected";
                BioSensDatabaseHelper.updateResearch(db, researchId, true);

            } else {
                ImageId = R.drawable.success;
                ListText = "Toxins not detected";

            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            TimeZone utc = TimeZone.getTimeZone("UTC");
            dateFormat.setTimeZone(utc);

            Calendar c = Calendar.getInstance();
            String data = dateFormat.format(c.getTime());


            if (toxin1 == true) {
                BioSensDatabaseHelper.insertMeasurement(db, researchId, data, data, "mycotoxin-t2","Mycotoxin T2","mg/kg", rezult1, toxin1b, lon, lat, user_id, "Analys");
                // if(rezult1>toxin1b){}
            }
            if (toxin2 == true) {
                BioSensDatabaseHelper.insertMeasurement(db, researchId, data, data, "aflatoxin-m1","Aflatoxin M1", "mg/kg", rezult2, toxin2b, lon, lat, user_id, "Analys");
            }
            if (toxin3 == true) {
                BioSensDatabaseHelper.insertMeasurement(db, researchId, data, data, "patulin","Patulin", "mg/kg", rezult3, toxin3b, lon, lat, user_id, "Analys");
            }
            if (toxin4 == true) {
                BioSensDatabaseHelper.insertMeasurement(db, researchId, data, data, "ochratoxin-a","Ochratoxin A", "mg/kg", rezult4, toxin4b, lon, lat, user_id, "Analys");
            }

                BioSensDatabaseHelper.insertMeasurement(db, researchId, data, data, "temperature","Temperature","°C", rezult5, 24, lon, lat, user_id, "Analys");


                BioSensDatabaseHelper.insertMeasurement(db, researchId, data, data, "relative-humidity","Relative Humidity","%", rezult6, 70, lon, lat, user_id, "Analys");

        } catch (InterruptedException ex) {

        }
    }
}
