package com.example.administrator.hotelservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.hotelservice.Input_event.Input_Event_Fragment;
import com.example.administrator.hotelservice.calendar.Calendar_Fragment;
import com.example.administrator.hotelservice.listmydata.Fragment_Mylist;
import com.example.administrator.hotelservice.mydata_event.Events;
import com.example.administrator.hotelservice.search_data.Search_Data;
import com.example.administrator.hotelservice.service.GeofenceTransitionsIntentService;
import com.example.administrator.hotelservice.showdetails.ShowDetail;
import com.example.administrator.hotelservice.system.MapsActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, OnCompleteListener<Void> {

    private static final float GEOFENCE_RADIUS_IN_METERS = 500; //1km
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 4320000; // 12hr
    private GoogleMap mMap;
    private String datetime;
    String jsondata;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private String path = "http://bnmsgps.hostingerapp.com/event/test.php";
    // GEO Fence
    private HashMap<String, LatLng> myLocation = new HashMap<>();
    private ArrayList<Geofence> geofenceList;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    Button button;
    EditText editText;

    JSONObject response, profile_pic_data, profile_pic_url;
    double latti, longi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        FacebookSdk.sdkInitialize(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.ed_latlng);
        setSupportActionBar(toolbar);

        AppEventsLogger.activateApp(this);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        datetime = dateformat.format(c.getTime());
        Log.d("datetime", datetime);
        button = findViewById(R.id.button_l);


        geofenceList = new ArrayList<>();
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofencePendingIntent = null; // default

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        jsondata = intent.getStringExtra("userProfile");
        initGeoFenceMark();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        ImageView user_picture = header.findViewById(R.id.profileImage);
        TextView nameView = header.findViewById(R.id.nameAndSurname);
        TextView emailView = header.findViewById(R.id.email_view);


        try {
            response = new JSONObject(jsondata);
            nameView.setText(response.get("name").toString());
            emailView.setText(response.get("email").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Picasso.get().load(profile_pic_url.getString("url")).into(user_picture);
            //with(this)

            String idFacebook = response.get("id").toString();
            checkLogin(response.get("id").toString(), response.get("name").toString(), response.get("email").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void checkLogin(final String id, final String name, final String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://bnmsgps.hostingerapp.com/event/registerFb.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("name", name);
                params.put("email", email);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void initGeoFenceMark() {
        final JsonArrayRequest req = new JsonArrayRequest(path,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonobject = response.getJSONObject(i);
                                final String title = jsonobject.getString("event_title");
                                final double longitude = jsonobject.getDouble("event_longitude");
                                final double latitude = jsonobject.getDouble("event_latitude");
                                myLocation.put(title, new LatLng(latitude, longitude));
                            }

                            // create Geofence
                            creatGeofenceList(myLocation);

                            // add Geofence
                            addGeofences();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    @SuppressLint("MissingPermission")
    private void addGeofences() {
        geofencingClient.addGeofences(requestGeofence(), getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    private void creatGeofenceList(HashMap<String, LatLng> myLocation) {
        for (Map.Entry<String, LatLng> entry : myLocation.entrySet()) {

            geofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {

//            Toast.makeText(this, "Added my location", Toast.LENGTH_SHORT).show();
        }
    }

    private GeofencingRequest requestGeofence() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        } else if (id == R.id.calendar) {
            Calendar_Fragment calendar_fragment = new Calendar_Fragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.show, calendar_fragment, calendar_fragment.getTag()).commit();


        } else if (id == R.id.listMyData) {
            Fragment_Mylist fragment_mylist = new Fragment_Mylist();
            FragmentManager manager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("idface", jsondata);
            fragment_mylist.setArguments(bundle);
            manager.beginTransaction().replace(R.id.show, fragment_mylist, fragment_mylist.getTag()).commit();


        } else if (id == R.id.searching) {
            Search_Data search_data = new Search_Data();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.show, search_data, search_data.getTag()).commit();


        } else if (id == R.id.input_evens) {
            Input_Event_Fragment input_event_fragment = new Input_Event_Fragment();
            FragmentManager manager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("idface", jsondata);
            input_event_fragment.setArguments(bundle);
            manager.beginTransaction().replace(R.id.show, input_event_fragment, input_event_fragment.getTag()).commit();

        }else if (id == R.id.service) {
            Intent service = new Intent(MainActivity.this, com.example.administrator.hotelservice.system.MapsActivity.class);
            MainActivity.this.startActivity(service);
            MainActivity.this.finish();
            startActivity(service);

        } else if (id == R.id.logout) {
            LoginManager.getInstance().logOut();
            Intent logout = new Intent(MainActivity.this, com.example.administrator.hotelservice.login.Login.class);
            MainActivity.this.startActivity(logout);
            MainActivity.this.finish();
            startActivity(logout);

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng[] sydney = new LatLng[1];
        final LatLng center = new LatLng(latti, longi);
        final JsonArrayRequest req = new JsonArrayRequest("http://bnmsgps.hostingerapp.com/event/get_list.php",
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonobject = response.getJSONObject(i);
                                final String title = jsonobject.getString("event_title");
                                final String status = jsonobject.getString("event_status");
                                final double longitude = jsonobject.getDouble("event_longitude");
                                final double latitude = jsonobject.getDouble("event_latitude");
                                sydney[0] = new LatLng(latitude, longitude);
                                LatLng latLng = new LatLng(latitude, longitude);

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(title)
                                        .snippet(status)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                    @Override
                                    public View getInfoWindow(Marker marker) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {
                                        View v = null;
                                        try {

                                            v = getLayoutInflater().inflate(R.layout.windowlayout, null);
                                            TextView title = v.findViewById(R.id.tv_title);
                                            title.setText(marker.getTitle());

                                            TextView description = v.findViewById(R.id.tv_description);
                                            description.setText("สถานะ:" + marker.getSnippet());
                                        } catch (Exception ev) {

                                        }
                                        return v;
                                    }
                                });

                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        Intent intent = new Intent(MainActivity.this, ShowDetail.class);
                                        intent.putExtra("Event", marker.getTitle());
                                        startActivity(intent);

                                    }
                                });
                            }
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 9));


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }


        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);


            mMap.resetMinMaxZoomPreference();

            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            mMap.clear();
            JsonArrayRequest req = new JsonArrayRequest("http://localhost/Project/getMylocation.php?lat="+latti+"&lon="+longi+"&km="+ editText.getText().toString(),
                    new com.android.volley.Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());
                            final LatLng[] sydney = new LatLng[1];
                            final LatLng center = new LatLng(latti, longi);
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonobject = response.getJSONObject(i);
                                    final String title = jsonobject.getString("event_title");
                                    final String status = jsonobject.getString("Status");
                                    final double longitude = jsonobject.getDouble("event_longitude");
                                    final double latitude = jsonobject.getDouble("event_latitude");

                                    sydney[0] = new LatLng(latitude, longitude);
                                    LatLng latLng = new LatLng(latitude, longitude);

                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(title)
                                            .snippet(status)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                        @Override
                                        public View getInfoWindow(Marker marker) {
                                            return null;
                                        }

                                        @Override
                                        public View getInfoContents(Marker marker) {
                                            View v = null;
                                            try {

                                                v = getLayoutInflater().inflate(R.layout.windowlayout, null);
                                                TextView title = v.findViewById(R.id.tv_title);
                                                title.setText(marker.getTitle());

                                                TextView description = v.findViewById(R.id.tv_description);
                                                description.setText("สถานะ:" + marker.getSnippet());
                                            } catch (Exception ev) {

                                            }
                                            return v;
                                        }
                                    });

                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {

                                            Intent intent = new Intent(MainActivity.this, ShowDetail.class);
                                            intent.putExtra("Event", marker.getTitle());
                                            startActivity(intent);

                                        }
                                    });
                                }
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 7));


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }


                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }


            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(req);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        mMap.resetMinMaxZoomPreference();
                    } else {

                    }

                }
            case REQUEST_LOCATION:
                getLocation();
                break;

        }
    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            latti = location.getLatitude();
            longi = location.getLongitude();

        }

    }


}

