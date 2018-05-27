package gr.hua.android.mapapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        EditText start = (EditText)findViewById(R.id.start);
        Double latitude = 0.0;
        Double longitude = 0.0;
        LocationManager locationManager;
        locationManager = (LocationManager) getApplication().getSystemService(LOCATION_SERVICE);
        Boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean isNetworkenable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        try {
            if (isGPSEnable) {
                Location location = null;
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                         latitude = location.getLatitude();
                         longitude = location.getLongitude();

                    }
                }


            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission for gps is off!", Toast.LENGTH_SHORT).show();
        }
        try {
            if (isNetworkenable) {
                Location location = null;
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                    }
                }


            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission for gps is off!", Toast.LENGTH_SHORT).show();
        }
        Geocoder geocoder;
        geocoder = new Geocoder(this);
        try {
            List<Address> CurrAddress = geocoder.getFromLocation(latitude,longitude,5 );
            start.setText(CurrAddress.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        Button findBut = (Button)findViewById(R.id.find) ;
        findBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText start = (EditText)findViewById(R.id.start);
                EditText end = (EditText)findViewById(R.id.end);
                String starting = start.getText().toString();
                String ending = end.getText().toString();
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAutocomlete();
                    }
                });

                private void openAutocompleteActivity() {
                    try {
                        // The autocomplete activity requires Google Play Services to be available. The intent
                        // builder checks this and throws an exception if it is not the case.
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(this);
                        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // Indicates that Google Play Services is either not installed or not up to date. Prompt
                        // the user to correct the issue.
                        GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                                0 /* requestCode */).show();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // Indicates that Google Play Services is not available and the problem is not easily
                        // resolvable.
                        String message = "Google Play Services is not available: " +
                                GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

                        Log.e(TAG, message);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                }


                Geocoder geocoder;
                List<Address> addresses = null;
                List<Address> addresses1 = null;
                geocoder=new Geocoder(getApplicationContext());
                try {
                    addresses = geocoder.getFromLocationName(starting,1);
                    addresses1 = geocoder.getFromLocationName(ending,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LatLng startingPlace = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
                LatLng endingPlace=null;
                mMap.addMarker(new MarkerOptions().position(startingPlace).title(addresses.get(0).getAddressLine(0)));

                if (ending.matches("")){
                    Toast.makeText(getApplicationContext(),"You have to write the ending place!",Toast.LENGTH_SHORT).show();
                }else {
                     endingPlace = new LatLng(addresses1.get(0).getLatitude(), addresses1.get(0).getLongitude());
                    mMap.addMarker(new MarkerOptions().position(endingPlace).title(addresses1.get(0).getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLng(startingPlace));
                PolylineOptions polylineOptions = new PolylineOptions().add(startingPlace).add(endingPlace).width(5).color(Color.RED).geodesic(true);
                googleMap.addPolyline(polylineOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPlace,15));
            }

        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
            }
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomOut());

    }
}
