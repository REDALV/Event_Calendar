package com.unovikau.eventcalendar.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.fragments.GMapFragment;
import com.unovikau.eventcalendar.models.CityEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventListAdapter extends BaseAdapter implements View.OnClickListener, Filterable{
    private List<CityEvent> dataSet;

    Filter filter;
    Context mContext;
    private List<CityEvent> filterList;

    private static final String TAG = "EventListAdapter";


    // View lookup cache
    private static class ViewHolder {
        ImageView event_type_item;
        TextView txtName;
        TextView txtDate;
        ImageView show_on_map;
    }

    public EventListAdapter(List<CityEvent> data, Context context) {
        //super(context, R.layout.row_event_item, data);
        this.dataSet = data;
        this.filterList = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        CityEvent dataModel=(CityEvent)object;

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
        CityEvent dataModel = (CityEvent)getItem(position);
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

        if(dataModel.isPastEvent()){
            convertView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.row_event_past_item_background, null));
        }
        else{
            convertView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.row_event_item_background, null));
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

        if(dataModel.getDateEnd() == null)
            viewHolder.txtDate.setText(dataModel.getDateString());
        else
            viewHolder.txtDate.setText(dataModel.getDateString() + " - " + dataModel.getDateEndString());



        viewHolder.show_on_map.setOnClickListener(this);
        viewHolder.show_on_map.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Get month filter.
     * @return month filter
     */
    @Override
    public Filter getFilter() {
        if(filter == null)
        {
            filter=new MonthFilter();
        }

        return filter;
    }

    /**
     * Get name filter.
     * @return name filter
     */
    public Filter getNameFilter() {
        if(filter == null)
        {
            filter=new NameFilter();
        }

        return filter;
    }

    /**
     * Get date filter.
     * @return date filter
     */
    public Filter getDateFilter() {
        if(filter == null)
        {
            filter=new DateFilter();
        }

        return filter;
    }

    public void removeFilter(){
        filter = null;
    }


    /**
     * CityEvent list filter by month
     */
    class MonthFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            String[] args = constraint.toString().split(";;;");
            int type = 0;
            if(args.length > 1)
                type = Integer.valueOf(args[1]);

            FilterResults results = new FilterResults();

            if (constraint.length() > 0) {

                constraint = args[0];

                ArrayList<CityEvent> filters = new ArrayList<CityEvent>();

                //get specific items
                for (int i = 0; i < filterList.size(); i++) {
                    if(filterList.get(i).getDateEnd() == null){
                        String startDate = filterList.get(i).getDateString();
                        if (startDate.contains(constraint)) {
                            if(type != 0){
                                if(filterList.get(i).getType() == type)
                                    filters.add(filterList.get(i));
                            }
                            else
                                filters.add(filterList.get(i));
                        }
                    }
                    else{
                        String startDate = filterList.get(i).getDateString();
                        String endDate = filterList.get(i).getDateEndString();

                        if (startDate.contains(constraint) || endDate.contains(constraint)) {
                            if(type != 0){
                                if(filterList.get(i).getType() == type)
                                    filters.add(filterList.get(i));
                            }
                            else
                                filters.add(filterList.get(i));
                        }
                    }


                }

                results.count = filters.size();
                results.values = filters;

            } else {
                results.count = filterList.size();
                results.values = filterList;

            }

            return results;
        }



        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            dataSet = (ArrayList<CityEvent>) results.values;
            notifyDataSetChanged();
        }
    }

    /**
     * CityEvent list filter by date
     */
    class DateFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            String[] args = constraint.toString().split(";;;");
            int type = 0;
            if(args.length > 1)
                type = Integer.valueOf(args[1]);

            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                String constr = constraint.toString();
                String[] values = constr.split("\\.");
                List<CityEvent> filters=new ArrayList<CityEvent>();

                //GregorianCalendar selected_date = new GregorianCalendar(Integer.parseInt(values[2]),Integer.parseInt(values[1]),Integer.parseInt(values[0]));
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date selectedDate = sdf.parse(constraint.toString());

                    //get specific items
                    for(CityEvent x: filterList)
                    {
                        Date startDate = x.getDate();

                        if(x.getDateEnd() != null){
                            Date endDate = x.getDateEnd();
                            if(!(selectedDate.before(startDate) || selectedDate.after(endDate))) {
                                if (type != 0) {
                                    if (x.getType() == type)
                                        filters.add(x);
                                } else
                                    filters.add(x);
                            }
                        }
                        else{
                            if(selectedDate.equals(startDate)) {
                                if (type != 0) {
                                    if (x.getType() == type)
                                        filters.add(x);
                                } else
                                    filters.add(x);
                            }
                        }
                    }
                }
                catch (ParseException ex){
                    Log.i(TAG, ex.getMessage());
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

            dataSet=(ArrayList<CityEvent>) results.values;
            notifyDataSetChanged();
        }

    }

    /**
     * CityEvent list filter by name
     */
    class NameFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTARINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                List<CityEvent> filters=new ArrayList<CityEvent>();

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

            dataSet=(ArrayList<CityEvent>) results.values;
            notifyDataSetChanged();
        }

    }

}
