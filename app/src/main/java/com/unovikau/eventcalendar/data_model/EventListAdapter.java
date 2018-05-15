package com.unovikau.eventcalendar.data_model;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.fragments.EventListFragment;
import com.unovikau.eventcalendar.fragments.GMapFragment;

import java.util.ArrayList;

public class EventListAdapter extends BaseAdapter implements View.OnClickListener, Filterable{
    private ArrayList<Event> dataSet;
    Filter filter;
    Context mContext;
    private ArrayList<Event> filterList;


    // View lookup cache
    private static class ViewHolder {
        ImageView event_type_item;
        TextView txtName;
        TextView txtDate;
        ImageView show_on_map;
    }

    public EventListAdapter(ArrayList<Event> data, Context context) {
        //super(context, R.layout.row_event_item, data);
        this.dataSet = data;
        this.filterList = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Event dataModel=(Event)object;

        switch (v.getId())
        {
            case R.id.show_event_on_map:{
                /*
                Toast toast = Toast.makeText(mContext,
                        dataModel.toString() + " on map", Toast.LENGTH_SHORT);
                toast.show();*/

                Gson gson = new Gson();
                String json = gson.toJson(dataModel);

                FragmentManager fragmentManager = ((Activity)this.mContext).getFragmentManager();

                GMapFragment gMapFragment = new GMapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("event", json);
                gMapFragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.content_frame, gMapFragment).addToBackStack("google_map_item").commit();
                break;
            }
        }
    }

    private int lastPosition = -1;

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataSet.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event dataModel = (Event)getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.row_event_item, parent, false);
            viewHolder.event_type_item = (ImageView) convertView.findViewById(R.id.event_type_item);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name_item);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date_item);
            viewHolder.show_on_map = (ImageView) convertView.findViewById(R.id.show_event_on_map);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        Drawable typeIcon;
        switch(dataModel.getType().intValue()){
            case 1:{
                typeIcon = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_football, null);
                break;
            }
            case 2:{
                typeIcon = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_lyre, null);
                break;
            }
            case 3:{
                typeIcon = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_open_book, null);
                break;
            }
            default:{
                typeIcon = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_menu_slideshow, null);
                break;
            }

        }
        viewHolder.event_type_item.setImageDrawable(typeIcon);
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtDate.setText(dataModel.getDateString());
        viewHolder.show_on_map.setOnClickListener(this);
        viewHolder.show_on_map.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new DateFilter();
        }

        return filter;
    }

    public Filter getStringFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new StringFilter();
        }

        return filter;
    }


    class DateFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTARINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                ArrayList<Event> filters=new ArrayList<Event>();

                //get specific items
                for(int i=0;i<filterList.size();i++)
                {
                    String curDate = filterList.get(i).getDateString();
                    if(curDate.contains(constraint))
                    {
                        filters.add(filterList.get(i));
                    }
                }

                results.count=filters.size();
                results.values=filters;

            }else
            {
                results.count=filterList.size();
                results.values=filterList;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            dataSet=(ArrayList<Event>) results.values;
            notifyDataSetChanged();
        }

    }

    class StringFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTARINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                ArrayList<Event> filters=new ArrayList<Event>();

                //get specific items
                for(int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getName().toUpperCase().contains(constraint))
                    {
                        filters.add(filterList.get(i));
                    }
                }

                results.count=filters.size();
                results.values=filters;

            }else
            {
                results.count=filterList.size();
                results.values=filterList;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            dataSet=(ArrayList<Event>) results.values;
            notifyDataSetChanged();
        }

    }

}
