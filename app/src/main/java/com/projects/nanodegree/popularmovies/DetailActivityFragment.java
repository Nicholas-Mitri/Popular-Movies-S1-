package com.projects.nanodegree.popularmovies;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.nanodegree.popularmovies.data.MoviesContract;
import com.projects.nanodegree.popularmovies.data.MoviesContract.FavoritesEntry;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Toast mToast = null;
    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private  static final int DETAIL_LOADER = 0;
    private Uri mUri;
    private Cursor mCursor;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOV_ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_RELEASE,
            MoviesContract.MoviesEntry.COLUMN_RATING,
            MoviesContract.MoviesEntry.COLUMN_POSTER,
            MoviesContract.MoviesEntry.COLUMN_REVIEWS,
            MoviesContract.MoviesEntry.COLUMN_TRAILERS,
            MoviesContract.MoviesEntry.COLUMN_POPULARITY

    };

    private static final String[] FAVORITES_COLUMNS = {
            MoviesContract.FavoritesEntry.TABLE_NAME + "." + MoviesContract.FavoritesEntry._ID,
            MoviesContract.FavoritesEntry.COLUMN_MOV_ID,
            MoviesContract.FavoritesEntry.COLUMN_TITLE,
            MoviesContract.FavoritesEntry.COLUMN_OVERVIEW,
            MoviesContract.FavoritesEntry.COLUMN_RELEASE,
            MoviesContract.FavoritesEntry.COLUMN_RATING,
            MoviesContract.FavoritesEntry.COLUMN_POSTER,
            MoviesContract.FavoritesEntry.COLUMN_REVIEWS,
            MoviesContract.FavoritesEntry.COLUMN_TRAILERS,
            MoviesContract.FavoritesEntry.COLUMN_POPULARITY

    };

    static final int COL_ID = 0;
    static final int COL_MOV_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_RELEASE = 4;
    static final int COL_RATING = 5;
    static final int COL_POSTER = 6;
    static final int COL_REVIEWS = 7;
    static final int COL_TRAILERS = 8;
    static final int COL_POPULARITY = 9;


    private ImageView imageview_bg;
    private ImageView imageview_poster;
    private TextView textview_title;
    private TextView textview_release;
    private TextView textview_rating;
    private TextView textview_overview;
    private ImageButton imageButton_trailer;
    private LinearLayout reviews_linearLayout;
    private CheckBox favoriteButton;
    private RelativeLayout mainView;

    private String trailerJson;
    private String[] trailerNames = null;
    private String[] reviewsArr = {"asdasd","asdasd"};

    private Uri[] trailerUris = null;

    public DetailActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null)
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);

        imageview_bg = (ImageView) rootView.findViewById(R.id.detail_imageview_bg);
        imageButton_trailer = (ImageButton) rootView.findViewById(R.id.detail_trailer_button);
        imageButton_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTrailer(v);
            }
        });
        imageview_poster = (ImageView) rootView.findViewById(R.id.detail_imageview_poster);
        textview_title = (TextView) rootView.findViewById(R.id.detail_textview_title);
        textview_release = (TextView) rootView.findViewById(R.id.detail_textview_release);
        textview_rating = (TextView) rootView.findViewById(R.id.detail_textview_rating);
        textview_overview = (TextView) rootView.findViewById(R.id.detail_textview_overview);
        reviews_linearLayout = (LinearLayout) rootView.findViewById(R.id.detail_linearLayout_reviews);
        mainView = (RelativeLayout) rootView.findViewById(R.id.detail_main_view);
        mainView.setVisibility(View.INVISIBLE);
        favoriteButton = (CheckBox) rootView.findViewById(R.id.favorite_checkbox);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(v);
            }
        });
        reviews_linearLayout.setFocusable(false);

        imageButton_trailer.bringToFront();

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(LOG_TAG, "URI is " + mUri);
        Intent intent = getActivity().getIntent();
        if (mUri == null)
            return null;

        String sortPref = Utility.getSortOrder(getActivity());

        String[] COLUMNS;

        if (!sortPref.equals("favorites")) {
            COLUMNS = MOVIE_COLUMNS;
        } else {
            COLUMNS = FAVORITES_COLUMNS;
        }
        return new CursorLoader(
                getActivity(),
                mUri,
                COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            mCursor = data;
            String imgUrl = Utility.BASE_IMG_URL + Utility.IMG_SIZE + data.getString(COL_POSTER);
            Log.d(LOG_TAG, "Image URL is " + imgUrl);
            try {
                Picasso.with(getActivity()).load(imgUrl).
                        transform(new BlurTransformation(getActivity())).into(imageview_bg);
                Picasso.with(getActivity()).load(imgUrl).
                        transform(new CropCircleTransformation()).into(imageview_poster, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        mainView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
            } catch (Exception e) {
                imageview_poster.setImageResource(R.drawable.sample);
            }

            Uri newUri = MoviesContract.BASE_CONTENT_URI.buildUpon()
                    .appendPath(MoviesContract.PATH_FAVORITES)
                    .appendPath(mUri.getPathSegments().get(1))
                    .build();

            Cursor c = getActivity().getContentResolver().query(
                    newUri,
                    FAVORITES_COLUMNS,
                    null,
                    null,
                    null
                    );

            if (c.moveToFirst()) {
                favoriteButton.setChecked(true);
            }

            c.close();

            int poster_h = imageview_poster.getHeight();
            imageButton_trailer.setTop(poster_h);
            textview_title.setText(data.getString(COL_TITLE));
            textview_release.setText("Release Date: " + data.getString(COL_RELEASE));
            textview_rating.setText("Score: " + data.getString(COL_RATING));
            textview_overview.setText(data.getString(COL_OVERVIEW));
            trailerJson = data.getString(COL_TRAILERS);

            parseReviewJson(data.getString(COL_REVIEWS));

            TextView reviewTextview;
            reviews_linearLayout.removeAllViewsInLayout();
            for (String review : reviewsArr) {
                reviewTextview = new TextView(getActivity());
                reviewTextview.setText(review);
                reviewTextview.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                reviewTextview.setPadding(20,20,20,20);
                reviews_linearLayout.addView(reviewTextview);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public void toggleFavorite(View v) {
        if (mCursor.moveToFirst()) {

            Uri newUri = MoviesContract.BASE_CONTENT_URI.buildUpon()
                    .appendPath(MoviesContract.PATH_FAVORITES)
                    .build();

            if (favoriteButton.isChecked()) {


                ContentValues cv = new ContentValues();

                cv.put(FavoritesEntry.COLUMN_MOV_ID, mCursor.getInt(COL_MOV_ID));
                cv.put(FavoritesEntry.COLUMN_OVERVIEW, mCursor.getString(COL_OVERVIEW));
                cv.put(FavoritesEntry.COLUMN_POSTER, mCursor.getString(COL_POSTER));
                cv.put(FavoritesEntry.COLUMN_RATING, mCursor.getDouble(COL_RATING));
                cv.put(FavoritesEntry.COLUMN_POPULARITY, mCursor.getDouble(COL_POPULARITY));
                cv.put(FavoritesEntry.COLUMN_RELEASE, mCursor.getString(COL_RELEASE));
                cv.put(FavoritesEntry.COLUMN_TITLE, mCursor.getString(COL_TITLE));
                cv.put(FavoritesEntry.COLUMN_TRAILERS, mCursor.getString(COL_TRAILERS));
                cv.put(FavoritesEntry.COLUMN_REVIEWS, mCursor.getString(COL_REVIEWS));

                getActivity().getContentResolver().insert(newUri, cv);

                if (mToast != null)
                    mToast.cancel();

                mToast = Toast.makeText(getActivity(), "Added to favorite list", Toast.LENGTH_SHORT);
                mToast.show();
            } else {

                getActivity().getContentResolver().delete(
                        newUri,
                        FavoritesEntry.COLUMN_MOV_ID + " = ? ",
                        new String[] {mUri.getPathSegments().get(1)}
                );

                if (mToast != null)
                    mToast.cancel();

                mToast = Toast.makeText(getActivity(), "Removed from favorite list", Toast.LENGTH_SHORT);
                mToast.show();
            }
        } else {
            favoriteButton.toggle();
            if (mToast != null)
                mToast.cancel();

            mToast = Toast.makeText(getActivity(), "Movie info not available yet", Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public void playTrailer(View view) {
        Log.d(LOG_TAG, "PLAY TRAILER");
        if (trailerNames == null) {
            parseTrailerJson(trailerJson);
        }
        if (trailerNames.length > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Trailer");
            builder.setItems(trailerNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selection) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUris[selection]);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.d(LOG_TAG, "No application can handle this request."
                                + " Please install a webbrowser");
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (mToast != null)
                mToast.cancel();

            mToast = Toast.makeText(getActivity(), "Sorry, no trailers available!", Toast.LENGTH_SHORT);
            mToast.show();
        }



    }

    private void parseTrailerJson(String trailerJsonStr) {

        final String RESULTS = "results";
        final String KEY = "key";
        final String NAME = "name";

        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray resultsArray = trailerJson.getJSONArray(RESULTS);

            int numOfResults = resultsArray.length();
            trailerNames = new String[numOfResults];
            trailerUris = new Uri[numOfResults];
            Uri builtUri;

            JSONObject singleTrailer;
            for(int i = 0; i < numOfResults; i++) {

                singleTrailer = resultsArray.getJSONObject(i);

                builtUri = Uri.parse("http://www.youtube.com").buildUpon()
                        .appendPath("watch")
                        .appendQueryParameter("v", singleTrailer.getString(KEY))
                        .build();
                trailerUris[i] = builtUri;
                trailerNames[i] = singleTrailer.getString(NAME);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSON string could not be parsed.");
        }
    }

    private void parseReviewJson(String reviewJsonStr) {

        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        try {
            JSONObject trailerJson = new JSONObject(reviewJsonStr);
            JSONArray resultsArray = trailerJson.getJSONArray(RESULTS);

            int numOfResults = resultsArray.length();
            reviewsArr = new String[numOfResults];

            JSONObject singleReview;
            for(int i = 0; i < numOfResults; i++) {

                singleReview = resultsArray.getJSONObject(i);

                reviewsArr[i] = singleReview.getString(CONTENT) + "\n\nby " +
                        singleReview.getString(AUTHOR);
            }
            if (numOfResults == 0) {
                reviewsArr = new String[] {"NO REVIEWS..."};
            }

        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSON string could not be parsed.");
        }
    }

}
