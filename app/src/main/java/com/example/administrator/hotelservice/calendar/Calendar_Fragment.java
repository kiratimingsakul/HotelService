package com.example.administrator.hotelservice.calendar;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.hotelservice.R;

import com.example.administrator.hotelservice.showdetails.ShowDetail;
import com.example.administrator.hotelservice.mydata_event.Events;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 */
public class Calendar_Fragment extends Fragment {


    View view;
    private String path = "http://bnmsgps.hostingerapp.com/event/test.php";
    CompactCalendarView compactCalendarView;
    Toolbar toolbar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    ArrayList<Events> countries = new ArrayList<>();


    public Calendar_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        toolbar = view.findViewById(R.id.toolbar_calendar);


        final List<String> mutableBookings = new ArrayList<>();
        final ListView listView = view.findViewById(R.id.bookings_listview);
        final ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mutableBookings);
        listView.setAdapter(adapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(dateFormatMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        }

        final JsonArrayRequest req = new JsonArrayRequest(path,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonobject = response.getJSONObject(i);
                                final String title = jsonobject.getString("event_title");
                                final String date = jsonobject.getString("event_date");
                                String str = date+" "+"00:00:00";
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date datep = null;
                                try {
                                    datep = df.parse(str);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long epoch = datep.getTime();

                                final Event event = new Event(Color.GREEN, epoch, title);

                                compactCalendarView.addEvent(event);
                                compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                                    @Override
                                    public void onDayClick(Date dateClicked) {
                                        List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);


                                        if (bookingsFromMap != null) {
                                            Log.d(TAG, bookingsFromMap.toString());
                                            mutableBookings.clear();
                                            for (Event booking : bookingsFromMap) {
                                                mutableBookings.add((String) booking.getData());
                                                Log.d("56", (String) booking.getData());
                                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                        Intent intent = new Intent(getActivity(), ShowDetail.class);
                                                        intent.putExtra("Event", mutableBookings.get(i));
                                                        Log.d("555", mutableBookings.get(i));
                                                        startActivity(intent);
//                                                        Toast.makeText(getActivity(),mutableBookings.get(i),Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }

                                            adapter.notifyDataSetChanged();


                                        }


                                    }


                                    @Override
                                    public void onMonthScroll(Date firstDayOfNewMonth) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            toolbar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
                                        }

                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }


        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(req);



        return view;

    }

}
