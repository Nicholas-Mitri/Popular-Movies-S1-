package com.projects.nanodegree.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String API_KEY = "";
    private static final String BASE_IMG_URL = "http://image.tmdb.org/t/p/";
    private static final String STATE_MOVIES = "state_movies";
    private String BASE_MOV_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String IMG_SIZE = "w500";
    private ArrayList<Movie> movieInfo = new ArrayList<Movie> ();
    private final String LOG_TAG = "PopMovies";
    private Toast mToast = null;
    private MyAdapter mMovieAdapter;


    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIES, movieInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity().getApplicationContext();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState != null) {
            movieInfo = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
//            if (mToast != null)
//                mToast.cancel();
//
//            mToast = Toast.makeText(getActivity(), "Movies Retrieved!", Toast.LENGTH_SHORT);
//            mToast.show();
        } else {
            updateMovies();
        }
        mMovieAdapter = new MyAdapter(getActivity(),
                    R.layout.gridview_item, R.id.gridview_item_textview, R.id.gridview_item_imageview,
                movieInfo);

        GridView movie_gridview = (GridView) rootView.findViewById(R.id.gridview_movies);
        movie_gridview.setAdapter(mMovieAdapter);
        movie_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("title", movieInfo.get(position).getTitle());
                intent.putExtra("release", movieInfo.get(position).getReleaseDate());
                intent.putExtra("rating", movieInfo.get(position).getVote());
                intent.putExtra("overview", movieInfo.get(position).getOverview());
                intent.putExtra("imgUrl", BASE_IMG_URL + IMG_SIZE +
                        movieInfo.get(position).getPosterURL());
                startActivity(intent);

            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh)
            updateMovies();

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();

        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(getActivity(), "Movies fetched!", Toast.LENGTH_SHORT);
        mToast.show();

    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {

            if (result != null) {

                mMovieAdapter.clear();
                for (Movie movie : result) {
                    mMovieAdapter.add(movie);
                }
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            ArrayList<Movie> moviesList = new ArrayList<Movie> ();
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortPref = sharedPref.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_default));
            Log.v(LOG_TAG, "Sort order is: " + sortPref);
            try {
                // Construct the URL for the themoviedb query
                Uri builtUri = Uri.parse(BASE_MOV_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortPref)
                        .appendQueryParameter(API_PARAM, API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
//                Log.v(LOG_TAG, "Fetched URL is: " + url);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                moviesJsonStr = buffer.toString();
//                Log.d(LOG_TAG, moviesJsonStr);
                moviesList = getMoviesDataFromJson(moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            movieInfo = moviesList;

            return moviesList;
        }

    }

    private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_OVERVIEW = "overview";
        final String MDB_VOTE_AVE = "vote_average";
        final String MDB_ORIG_TITLE = "original_title";
        final String MDB_RELEASE = "release_date";
        final String MDB_POSTER = "poster_path";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(MDB_RESULTS);

        int numOfResults = resultsArray.length();
        ArrayList<Movie> resultStrs = new ArrayList<Movie> ();

        for(int i = 0; i < numOfResults; i++) {

            // Get the JSON object representing a single movie
            JSONObject singleMovie = resultsArray.getJSONObject(i);

            resultStrs.add(new Movie(singleMovie.getString(MDB_ORIG_TITLE),
                    singleMovie.getString(MDB_RELEASE),
                    singleMovie.getDouble(MDB_VOTE_AVE),
                    singleMovie.getString(MDB_OVERVIEW), singleMovie.getString(MDB_POSTER)));
        }

        return resultStrs;

    }
}
