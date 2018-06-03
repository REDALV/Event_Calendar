package com.unovikau.eventcalendar.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unovikau.eventcalendar.R;

import java.io.InputStream;
import java.util.List;

public class EventPhotosAdapter extends PagerAdapter{

    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<String> images;

    public EventPhotosAdapter(Context mContext, List<String> images) {
        this.mContext = mContext;
        this.images = images;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
        //return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ViewGroup view = (ViewGroup) layoutInflater.inflate(R.layout.event_photos_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        new DownloadImageTask(imageView).execute(images.get(position));
        //imageView.setImageResource(mResources[position]);
        //view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;

        private DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
