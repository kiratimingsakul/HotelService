package com.example.administrator.hotelservice.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.hotelservice.MainActivity;
import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.listmydata.Fragment_Mylist;
import com.example.administrator.hotelservice.search_data.HttpServiceClass;
import com.example.administrator.hotelservice.search_data.ListAdapter;
import com.example.administrator.hotelservice.search_data.Module;
import com.example.administrator.hotelservice.showdetails.ShowMyDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Fesinthai extends AppCompatActivity {
    ListView listView;
    ProgressBar progressBar;
    ListAdapter listAdapter;
    ArrayList<Module> eventList = new ArrayList<>();
    String datetime, dates;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    TextView date, titel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Calendar c = Calendar.getInstance();
        datetime = sdf.format(c.getTime());
        dates = sdf.format(c.getTime());

        setContentView(R.layout.activity_fesinthai);
        listView = findViewById(R.id.fes_list);
        date = findViewById(R.id.fes_date);
        date.setText(dates);

        progressBar = findViewById(R.id.fes_progressbar);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Module ListViewClickData = (Module) parent.getItemAtPosition(position);
                Intent intent = new Intent(Fesinthai.this, ShowMyDetail.class);
                intent.putExtra("Event", ListViewClickData.getTitle());
                startActivity(intent);
                Toast.makeText(Fesinthai.this, ListViewClickData.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        new ParseJSonDataClass(Fesinthai.this).execute();

    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {
        public Context context;
        String FinalJSonResult;

        public ParseJSonDataClass(Context context) {

            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpServiceClass httpServiceClass = new HttpServiceClass("http://localhost/Project/getlocation.php?date=%22" + datetime + "%22");

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {

                        JSONArray jsonArray = null;
                        try {

                            jsonArray = new JSONArray(FinalJSonResult);

                            JSONObject jsonObject;


                            Module events;

                            eventList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                jsonObject = jsonArray.getJSONObject(i);

                                String title = jsonObject.getString("event_title").toString();

                                String province = jsonObject.getString("event_province").toString();

                                String image = jsonObject.getString("img").toString();

                                events = new Module(title, province, image);

                                eventList.add(events);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else {

                    Toast.makeText(context, httpServiceClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.INVISIBLE);
            listAdapter = new ListAdapter(Fesinthai.this, R.layout.custom_search, eventList);
            listView.setAdapter(listAdapter);
        }
    }


}
