package com.projects.nanodegree.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private Context mContext = null;
    private int mTextViewResourceID;
    private int mImageViewResourceID;
    private int mResource;
    ArrayList<Movie> mObjects = null;
    private LayoutInflater mInflater;
    private int testImageDrawable = R.drawable.sample;
    private static final String BASE_IMG_URL = "http://image.tmdb.org/t/p/";
    private static final String IMG_SIZE = "w342";

    public MyAdapter(Context c, int resource, int textViewResourceID, int imageViewResourceID,
                     ArrayList<Movie> objects) {
        mContext = c;
        mTextViewResourceID = textViewResourceID;
        mImageViewResourceID = imageViewResourceID;
        mObjects = objects;
        mResource = resource;
        mInflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    public void clear() {
        mObjects.clear();
    }

    public void add(Movie item) {
        mObjects.add(item);

        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mObjects != null) {
            View mView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                mView = mInflater.inflate(mResource, parent, false);
            } else {
                mView = convertView;
            }
            String poster_path = BASE_IMG_URL + IMG_SIZE + mObjects.get(position).getPosterURL();
            ImageView imageView = (ImageView) mView.findViewById(mImageViewResourceID);
            try {
                Picasso.with(mContext).load(poster_path).placeholder(R.drawable.sample)
                        .into(imageView);
//                Picasso.with(mContext).load(poster_path).transform(new BlurTransformation(mContext)).into(imageView);
            } catch (Exception e) {
                Log.d("PopMovies", "PICASSO FAILING");
            }
                    TextView textview = (TextView) mView.findViewById(mTextViewResourceID);
                    textview.setText(mObjects.get(position).getTitle());
            return mView;
        }
        return null;
    }
}
