package com.biosens.biosens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SQLiteDatabase db;
    SQLiteOpenHelper biosensDatabaseHelper;
    Cursor cursor;
    SessionManagement session;
    Location location;
    double latitude=0;
    double longitude=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private LocationManager locationManager;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng myPosition = null;
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            myPosition = new LatLng(latitude, longitude);
        }

        // Add a marker in Sydney and move the camera

        session = new SessionManagement(this);


      /*  mMap.addMarker(new MarkerOptions().position(myPosition).title("Marker in good").icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions().position(sydneyBad).title("Marker in bad").icon(
                BitmapDescriptorFactory.defaultMarker()).flat(false));*/

        if (myPosition != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        }


        setUpClusterer();
    }

    private GoogleMap getMap(){
        return mMap;
    }


    private ClusterManager<MyClusterItem> mClusterManager;

    private void setUpClusterer() {
        // Declare a variable for the cluster manager.


        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyClusterItem>(this, getMap());

//        mClusterManager.setRenderer(new MyClusterRenderer(this, mMap,
//                mClusterManager));

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    class MyClusterItem implements ClusterItem {
        private final LatLng mPosition;
        private final boolean mHaveToxin;

        public MyClusterItem(double lat, double lng, boolean haveToxin) {
            mPosition = new LatLng(lat, lng);
            mHaveToxin = haveToxin;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public boolean haveToxin(){
            return mHaveToxin;
        }
    }

    public class MyClusterRenderer extends DefaultClusterRenderer<MyClusterItem> {

        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());

        public MyClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<MyClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyClusterItem item,
                                                   MarkerOptions markerOptions) {

            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);

            markerOptions.icon(markerDescriptor);
        }

        @Override
        protected void onClusterItemRendered(MyClusterItem clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MyClusterItem> cluster, MarkerOptions markerOptions){

            boolean haveToxin = false;

            for (MyClusterItem item : cluster.getItems()){
                if (item.haveToxin()) {
                    haveToxin = true;
                }
            }


            //modify padding for one or two digit numbers
            if (haveToxin) {
                final Drawable clusterIcon = getResources().getDrawable(R.drawable.fail);
                clusterIcon.setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);

                mClusterIconGenerator.setBackground(clusterIcon);
//                mClusterIconGenerator.setContentPadding(40, 20, 0, 0);
            }
            else {
                final Drawable clusterIcon = getResources().getDrawable(R.drawable.success);
                clusterIcon.setColorFilter(getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_ATOP);

                mClusterIconGenerator.setBackground(clusterIcon);
//                mClusterIconGenerator.setContentPadding(30, 20, 0, 0);
            }

            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    private void addItems() {
        HashMap<String, String> user = session.getUserDetails();

        String user_id = user.get(SessionManagement.KEY_ID);
        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(this);
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("measurement",
                    new String[]{"longitude", "latitude","value","end_time","unit"},
                    "user_id= ?",
                    new String[] {user_id},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                double lng = cursor.getDouble(0);
                double lat = cursor.getDouble(1);
//                LatLng rezPositions = new LatLng(lat,lng);
//                if(cursor.getDouble(2) < 10){
//                    mMap.addMarker(new MarkerOptions().position(rezPositions ).title("Toxin are not found | Date "+cursor.getString(3)+" | Unit "+cursor.getString(4)).icon(
//                            BitmapDescriptorFactory
//                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));}
//                else{
//                    mMap.addMarker(new MarkerOptions().position(rezPositions ).title("Toxin are found | Date"+cursor.getString(3)+" | Init "+cursor.getString(4)).icon(
//                            BitmapDescriptorFactory
//                                    .defaultMarker()));
//                }


                MyClusterItem offsetItem = new MyClusterItem(lat, lng, cursor.getDouble(2) >= 10);
                mClusterManager.addItem(offsetItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void init() {
    }

    public void onBackPressed(View v) {
        super.onBackPressed();
    }


}
