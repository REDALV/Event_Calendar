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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.fragments.EventListFragment;
import com.unovikau.eventcalendar.fragments.GMapFragment;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event> implements View.OnClickListener{
    private ArrayList<Event> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        ImageView event_type_item;
        TextView txtName;
        TextView txtDate;
        ImageView show_on_map;
    }

    public EventListAdapter(ArrayList<Event> data, Context context) {
        super(context, R.layout.row_event_item, data);
        this.dataSet = data;
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

                fragmentManager.beginTransaction().add(R.id.content_frame, gMapFragment).addToBackStack("google_map_item").commit();
                break;
            }
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
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

}
