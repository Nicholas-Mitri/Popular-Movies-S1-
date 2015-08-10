package com.projects.nanodegree.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ImageView imageview_bg = (ImageView) rootView.findViewById(R.id.detail_imageview_bg);
        ImageView imageview_poster = (ImageView) rootView.findViewById(R.id.detail_imageview_poster);
        TextView textview_title = (TextView) rootView.findViewById(R.id.detail_textview_title);
        TextView textview_release = (TextView) rootView.findViewById(R.id.detail_textview_release);
        TextView textview_rating = (TextView) rootView.findViewById(R.id.detail_textview_rating);
        TextView textview_overview = (TextView) rootView.findViewById(R.id.detail_textview_overview);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("title")) {
            String imgUrl = intent.getStringExtra("imgUrl");
            Picasso.with(getActivity()).load(imgUrl).
                    transform(new BlurTransformation(getActivity())).into(imageview_bg);
            Picasso.with(getActivity()).load(imgUrl).
                    transform(new CropCircleTransformation()).into(imageview_poster);
            textview_title.setText(intent.getStringExtra("title"));
            textview_release.setText("Release Date: " + intent.getStringExtra("release"));
            textview_rating.setText("Score: " + intent.getStringExtra("rating"));
            textview_overview.setText(intent.getStringExtra("overview"));
        }

        return rootView;
    }
}
