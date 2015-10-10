package com.projects.nanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.nanodegree.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;


public class MoviesAdapter extends CursorAdapter {

    private final String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private static final String BASE_IMG_URL = "http://image.tmdb.org/t/p/";
    private static final String IMG_SIZE = "w342";
    private Context mContext = null;

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false);
        Log.d(LOG_TAG, "View inflated!");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int idx_poster = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER);
        int idx_title = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
        Log.d(LOG_TAG, "Title is: " + cursor.getString(idx_title));
        String poster_path = BASE_IMG_URL + IMG_SIZE + cursor.getString(idx_poster);
        ImageView imageView = (ImageView) view.findViewById(R.id.gridview_item_imageview);

        try {
            Picasso.with(mContext).load(poster_path).placeholder(R.drawable.sample)
                    .into(imageView);
        } catch (Exception e) {
            Log.d("PopMovies", "PICASSO FAILING");
        }

        TextView textview = (TextView) view.findViewById(R.id.gridview_item_textview);
        textview.setText(cursor.getString(idx_title));

    }
}