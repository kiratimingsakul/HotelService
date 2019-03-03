package com.example.administrator.hotelservice.showdetails;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.calendar.Calendar_Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShowDetail extends AppCompatActivity {

    TextView textDetails, description, note_text;
    ImageView imageView;

    String convert;
    String event_title = "";
    String event_description = "";
    String event_contact = "";
    Double lng = 0.0;
    Double lat = 0.0;
    String img = "";
    String website = "";
    String note = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        textDetails = findViewById(R.id.textViewDetails);
        description = findViewById(R.id.description);
        note_text = findViewById(R.id.note);
        imageView = findViewById(R.id.image_view);


        final Intent intent = getIntent();
        String message = intent.getStringExtra("Event");
        convert = (message);
        query();

        textDetails.setText(event_title);
        description.setText(event_description);
        note_text.setText("***" + note + "***");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bar_show_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.map:
                String format = "geo:0,0?q=" + lat + "," + lng + "(" + event_title + ")";
                Uri uri = Uri.parse(format);
                Intent intent_map = new Intent(Intent.ACTION_VIEW, uri);
                intent_map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_map);
                return true;

            case R.id.link:
                Intent intent_link = new Intent(Intent.ACTION_VIEW);
                intent_link.addCategory(Intent.CATEGORY_BROWSABLE);
                intent_link.setData(Uri.parse("https://"+website));
                startActivity(intent_link);
                return true;

            case R.id.call:
                Intent intent_call = new Intent(Intent.ACTION_DIAL);
                intent_call.setData(Uri.parse("tel:" + event_contact));
                Log.d("event_contact", event_contact);
                startActivity(intent_call);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

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
                            Glide.with(ShowDetail.this).load("http://bnmsgps.hostingerapp.com/event/upload/" + img).into(imageView);

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
//                        Toast.makeText(ShowDetail.this,error.toString(),Toast.LENGTH_LONG).show();
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


