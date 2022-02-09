package com.example.getnanny20;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.example.getnanny20.R;
import com.example.getnanny20.CallBack_List;
import com.example.getnanny20.CallBack_Map;
import com.example.getnanny20.FragmentMap;
import com.example.getnanny20.FragmentList;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class ActivityMap extends AppCompatActivity implements LocationListener {
    private FragmentList fragmentList;
    private FragmentMap fragmentMap;
    private MaterialButton search_BTN_back;

    private LocationManager locationManager;
    private double lat, lon;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initBTN();
        checkLocationPermission();
        initFragmentMap();
        initFragmentList();
    }

    private void initBTN() {
        search_BTN_back = findViewById(R.id.search_BTN_back);
        search_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ActivityMap.this, ActivityMenu.class);
                startActivity(intent);
            }
        });

    }

    private void initFragmentList() {
        fragmentList = new FragmentList();
        fragmentList.setActivity(this);
        fragmentList.setCallBackList(callBack_list);
        callBack_list.getPostLocation(lat,lon);
        getSupportFragmentManager().beginTransaction().add(R.id.search_frame1, fragmentList).commit();
    }

    private void initFragmentMap() {
        fragmentMap = new FragmentMap();
        fragmentMap.setActivity(this);
        fragmentMap.setCallBackMap(callBack_map);
        fragmentMap.setLat(lat).setLon(lon);
        callBack_map.getLocation(lat,lon);
        getSupportFragmentManager().beginTransaction().add(R.id.search_frame2, fragmentMap).commit();
    }

    CallBack_List callBack_list = new CallBack_List() {
        @Override
        public void getPostLocation(double lat, double lon) {
            fragmentMap.setLat(lat).setLon(lon);
            callBack_map.getLocation(lat,lon);
        }
    };

    CallBack_Map callBack_map = (lat, lon) -> {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.search_map);
        mapFragment.getMapAsync(googleMap -> {
            LatLng latLng = new LatLng(lat, lon);
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Meal is here!"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 5000, null);
        });
    };


    private void checkLocationPermission() {
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(ActivityMap.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ActivityMap.this,new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION }, 100);
        }

        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5, (LocationListener) ActivityMap.this);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lat = location.getLatitude();
            lon = location.getLongitude();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(ActivityMap.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            lat = location.getLatitude();
            lon = location.getLongitude();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


}