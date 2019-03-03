package com.example.administrator.hotelservice.search_data;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.showdetails.ShowDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search_Data extends Fragment {
    ListView listView;
    EditTextWithDel editTextWithDel;
    ArrayList<Module> eventList = new ArrayList<>();
    String HttpURL = "http://bnmsgps.hostingerapp.com/event/test.php";
    ListAdapter listAdapter;
    ProgressBar progressBar ;


    public Search_Data() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_data, container, false);
        listView =  view.findViewById(R.id.listView1);
        editTextWithDel =  view.findViewById(R.id.edittext1);
        progressBar = view.findViewById(R.id.progressbar);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Module ListViewClickData = (Module)parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ShowDetail.class);
                intent.putExtra("Event", ListViewClickData.getTitle());
                startActivity(intent);
                Toast.makeText(getActivity(), ListViewClickData.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        editTextWithDel.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence stringVar, int start, int before, int count) {

                listAdapter.getFilter().filter(stringVar.toString());
            }
        });

        new ParseJSonDataClass(getActivity()).execute();


        return view;
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

            HttpServiceClass httpServiceClass = new HttpServiceClass(HttpURL);

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

                                String title = jsonObject.getString("event_title");

                                String province = jsonObject.getString("event_province");

                                String image = jsonObject.getString("image");



                                events = new Module(title, province,image);

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
            listAdapter = new ListAdapter(getActivity(), R.layout.custom_search, eventList);
            listView.setAdapter(listAdapter);
        }
    }

}
