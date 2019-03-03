package com.example.administrator.hotelservice.listmydata;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.search_data.EditTextWithDel;
import com.example.administrator.hotelservice.search_data.HttpServiceClass;
import com.example.administrator.hotelservice.search_data.ListAdapter;
import com.example.administrator.hotelservice.search_data.Module;
import com.example.administrator.hotelservice.search_data.Search_Data;
import com.example.administrator.hotelservice.showdetails.ShowDetail;
import com.example.administrator.hotelservice.showdetails.ShowMyDetail;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Mylist extends Fragment {
    ListView listView;
    ArrayList<Module> eventList = new ArrayList<>();
    String HttpURL = "http://bnmsgps.hostingerapp.com/event/get_list.php";
    ListAdapter listAdapter;
    String idfacebook;
    String facebook = "";
    ProgressBar progressBar;
    JSONObject response;


    public Fragment_Mylist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment__mylist, container, false);

        Bundle bundle = this.getArguments();
        idfacebook = bundle.getString("idface");
        listView = view.findViewById(R.id.listView2);
        progressBar = view.findViewById(R.id.progressbar);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Module ListViewClickData = (Module) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ShowMyDetail.class);
                intent.putExtra("Event", ListViewClickData.getTitle());
                intent.putExtra("idface",idfacebook);
                startActivity(intent);
//                Toast.makeText(getActivity(), ListViewClickData.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        new ParseJSonDataClass(getActivity()).execute();
        try {
            response = new JSONObject(idfacebook);
            facebook = response.get("id").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }



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

            HttpServiceClass httpServiceClass = new HttpServiceClass(HttpURL+"?id="+facebook);

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

                                Log.d("test","Title ="+title);

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
