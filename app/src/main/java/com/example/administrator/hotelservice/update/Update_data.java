package com.example.administrator.hotelservice.update;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.administrator.hotelservice.MainActivity;
import com.example.administrator.hotelservice.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Update_data extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    String jsondata, message;
    private static final String URL = "http://unevent.xyz/ProjectBoss/Update_data.php";
    JSONObject response;

    String title = "";
    String description = "";
    String province = "";
    String Address = "";
    String img;
    String status = "";
    String website ="";
    String note = "";
    String contact ="";
    String dayof = "";
    String facebook = "";
    TextView la, lon,date22,date_view2;
    String lng = "";
    String lat = "";
    EditText addTitle, addAddress2, add_description2,wed, tel,ednote;
    Spinner spin_data2,spin_status2;
    ImageView imageView,imageView2,date_pick2;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap bitmap;
    private Uri filePath;



    private int year, month, day;
    String sledate,sledate2;



    private int PLACE_PICKER_REQUEST = 1;

    private static final int IMAGE_REQUEST_CODE = 3;
    private static final int STORAGE_PERMISSION_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        addTitle = findViewById(R.id.addTitle2);
        addAddress2 = findViewById(R.id.addAddress2);
        add_description2 = findViewById(R.id.add_description2);
        spin_data2 = findViewById(R.id.spin_data2);

        la = findViewById(R.id.lat2);
        lon = findViewById(R.id.lon2);
        imageView = findViewById(R.id.add_image2);
        imageView2 = findViewById(R.id.chack_in2);
        date_pick2 = findViewById(R.id.date_pick2);
        date22 = findViewById(R.id.date22);
        date_view2 = findViewById(R.id.date_view2);
        spin_status2 = findViewById(R.id.spin_status2);
        ednote = findViewById(R.id.ex2);

        wed = findViewById(R.id.wed2);
        tel = findViewById(R.id.tel2);


        final Intent intent2 = getIntent();
        message = intent2.getStringExtra("id");

        final Intent intent = getIntent();
        jsondata = intent.getStringExtra("idface");


        try {
            response = new JSONObject(jsondata);
            facebook = response.get("id").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        query();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(Update_data.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
        date_pick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Update_data.this);
                final DatePicker picker = new DatePicker(Update_data.this);
                picker.setCalendarViewShown(false);
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        year = picker.getYear();
                        month = picker.getMonth()+1;
                        day = picker.getDayOfMonth();

//
                        sledate = String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
                        sledate2 = String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day);
                        date22.setText(sledate2);
                        date_view2.setText(sledate);
//                        Toast.makeText(getActivity(),String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year),Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });



//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Update_data.this, MainActivity.class);
//                intent.putExtra("userProfile", jsondata);
//                startActivity(intent);
//                Update_data.this.finish();
//            }
//        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(Update_data.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(Update_data.this, this)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(Update_data.this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, Update_data.this);
                String latitudemap = String.valueOf(place.getLatLng().latitude);
                String longitudemap = String.valueOf(place.getLatLng().longitude);
                la.setText(latitudemap);
                lon.setText(longitudemap);

            }
        }

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(Update_data.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(Update_data.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(Update_data.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void query() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://localhost/Project/post_Id.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            title = jsonObj.getString("event_title");
                            Address = jsonObj.getString("event_address");
                            description = jsonObj.getString("event_description");
                            img = jsonObj.getString("img");
                            lat = jsonObj.getString("event_latitude");
                            lng = jsonObj.getString("event_longitude");
                            website = jsonObj.getString("event_website");
                            contact = jsonObj.getString("event_contact");
                            dayof = jsonObj.getString("event_date");
                            note = jsonObj.getString("event_note");
//                            setText
                            Glide.with(Update_data.this).load("http://bnmsgps.hostingerapp.com/event/image/" + img).into(imageView);
                            tel.setText(contact);
                            wed.setText(website);
                            add_description2.setText(description);
                            addAddress2.setText(Address);
                            addTitle.setText(title);
                            la.setText(String.valueOf(lat));
                            date_view2.setText(dayof);
                            lon.setText(String.valueOf(lng));
                            ednote.setText(note);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(ShowDetail.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(ShowDetail.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", message);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save:
                AlertDialog.Builder alert = new AlertDialog.Builder(Update_data.this);
                alert.setMessage("คุณต้องการบันทึกการแก้ไขข้อมูลใช่หรือไม่");
                alert.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onEditText();

                        onButtonClick();
                    }
                });
                alert.setNegativeButton("ไม่ใช่", null);
                alert.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void onButtonClick() {
        if (!title.isEmpty() && !Address.isEmpty() && !description.isEmpty() && !province.isEmpty() && !lat.isEmpty() && !lng.isEmpty() && !status.isEmpty()&&!sledate2.isEmpty()) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("onResponse", response);

//                    AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(Update_data.this);
                    builder.setTitle("บันทึกข้อมูลเรียบร้อย");
                    builder.setMessage("       ");
                    builder.setCancelable(true);
                    final AlertDialog closedialog= builder.create();
                    closedialog.show();
                    final Timer timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            closedialog.dismiss();
                            timer2.cancel();
                            Intent intent = new Intent(Update_data.this, MainActivity.class);
                            intent.putExtra("userProfile", jsondata);
                            Update_data.this.finish();
                            startActivity(intent);

                        }
                    },3000);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("onError", error.toString());
                    Toast.makeText(Update_data.this, "เกิดข้อผิดพลาดโปรดลองอีกครั้ง", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("title", title);
                    params.put("Address", Address);
                    params.put("description", description);
                    params.put("province", province);
                    params.put("latitude", lat);
                    params.put("longitude", lng);
                    params.put("date",sledate2);
                    params.put("note",note);
                    params.put("status",status);
                    params.put("id",message);
                    params.put("idfacebook", facebook);
                    params.put("note",note);
                    return params;
                }
            };
            requestQueue.add(request);

        } else {
            Toast.makeText(Update_data.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_LONG).show();
        }

    }



    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void onEditText() {

        title = addTitle.getText().toString();
        Address = addAddress2.getText().toString();
        description = add_description2.getText().toString();
        lat = la.getText().toString();
        lng = lon.getText().toString();
        website = wed.getText().toString();
        contact = tel.getText().toString();
        province = spin_data2.getSelectedItem().toString();
        status = spin_status2.getSelectedItem().toString();
        note = ednote.getText().toString();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(imageView2, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }
}

