package com.example.administrator.hotelservice.showdetails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.administrator.hotelservice.MainActivity;
import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.update.Update_data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ShowMyDetail extends AppCompatActivity {

    TextView textDetails, description, note_text;
    ImageView imageView;

    String id = "";
    String convert;
    String event_title = "";
    String event_description = "";
    String event_contact = "";
    Double lng = 0.0;
    Double lat = 0.0;
    String img = "";
    String website = "";
    String note = "";
    String jsondata, message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);


        textDetails = findViewById(R.id.textViewDetails);
        description = findViewById(R.id.description);
        note_text = findViewById(R.id.note);
        imageView = findViewById(R.id.image_view);




        final Intent intent = getIntent();
        message = intent.getStringExtra("Event");

        final Intent intent2 = getIntent();
        jsondata = intent2.getStringExtra("idface");

        convert = (message);

        query();

        textDetails.setText(event_title);
        description.setText(event_description);
        note_text.setText("***" + note + "***");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.barmylist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.edit:
                Intent intent = new Intent(getApplicationContext(), Update_data.class);
                intent.putExtra("idface",jsondata);
                intent.putExtra("id",id);

                startActivity(intent);
                return true;

            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("คุณต้องการลบข้อมูลหรือไม่");
                alert.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowMyDetail.this);
                        builder.setTitle("ลบข้อมูลเรียบร้อย");
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
                                Intent intent = new Intent(ShowMyDetail.this, MainActivity.class);
                                intent.putExtra("userProfile", jsondata);
                                startActivity(intent);
                                ShowMyDetail.this.finish();
                            }
                        },3000);

                    }
                });
                alert.setNegativeButton("ไม่ใช่", null);
                alert.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void delete() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://bnmsgps.hostingerapp.com/event/delete.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        textDetails = findViewById(R.id.textViewDetails);
                        try {
                            JSONObject jsonObj = new JSONObject(response);

                            //                    AlertDialog


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", event_title);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void query() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://bnmsgps.hostingerapp.com/event/post_Event.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        textDetails = findViewById(R.id.textViewDetails);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            event_title = jsonObj.getString("event_title");
                            event_description = jsonObj.getString("event_description");
                            lat = jsonObj.getDouble("event_latitude");
                            lng = jsonObj.getDouble("event_longitude");
                            note = jsonObj.getString("event_note");
                            img = jsonObj.getString("image");
                            Glide.with(ShowMyDetail.this).load("http://bnmsgps.hostingerapp.com/event/upload/" + img).into(imageView);


                            textDetails.setText(event_title);
                            note_text.setText(note);
                            description.setText(event_description);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", convert);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}

