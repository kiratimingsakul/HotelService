package com.example.administrator.hotelservice.search_data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.showdetails.ShowDetail;

import java.util.ArrayList;

/**
 * Created by Juned on 2/1/2017.
 */

public class ListAdapter extends ArrayAdapter<Module> {

    public ArrayList<Module> MainList;
    private Activity context;

    public ArrayList<Module> moduleListTemp;

    public SubjectDataFilter subjectDataFilter ;

    public ListAdapter(Activity context, int id, ArrayList<Module> moduleArrayList) {

        super(context, id, moduleArrayList);

        this.moduleListTemp = new ArrayList<Module>();

        this.moduleListTemp.addAll(moduleArrayList);

        this.MainList = new ArrayList<Module>();

        this.MainList.addAll(moduleArrayList);
    }

    @Override
    public Filter getFilter() {

        if (subjectDataFilter == null){

            subjectDataFilter  = new SubjectDataFilter();
        }
        return subjectDataFilter;
    }


    public class ViewHolder {

        TextView SubjectName;
        TextView SubjectFullForm;
        ImageView image_List;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.custom_search, null);

            holder = new ViewHolder();

            holder.SubjectName =  convertView.findViewById(R.id.textviewName);

            holder.SubjectFullForm = convertView.findViewById(R.id.textviewFullForm);
            holder.image_List = convertView.findViewById(R.id.imagelist);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Module module = moduleListTemp.get(position);

        holder.SubjectName.setText(module.getTitle());

        holder.SubjectFullForm.setText(module.getProvince());
//        Glide.with(ShowDetail.this).load("http://192.168.1.22/image/" + img).into(imageView);
        Glide.with(getContext()).load("http://bnmsgps.hostingerapp.com/event/upload/" + module.getImage()).into(holder.image_List);
        return convertView;

    }

    private class SubjectDataFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            if(charSequence != null && charSequence.toString().length() > 0)
            {
                ArrayList<Module> arrayList1 = new ArrayList<Module>();

                for(int i = 0, l = MainList.size(); i < l; i++)
                {
                    Module module = MainList.get(i);

                    if(module.toString().toLowerCase().contains(charSequence))

                        arrayList1.add(module);
                }
                filterResults.count = arrayList1.size();

                filterResults.values = arrayList1;
            }
            else
            {
                synchronized(this)
                {
                    filterResults.values = MainList;

                    filterResults.count = MainList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            moduleListTemp = (ArrayList<Module>)filterResults.values;

            notifyDataSetChanged();

            clear();

            for(int i = 0, l = moduleListTemp.size(); i < l; i++)
                add(moduleListTemp.get(i));

            notifyDataSetInvalidated();
        }
    }


}