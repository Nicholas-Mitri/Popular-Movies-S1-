package com.projects.nanodegree.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.projects.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private static final String API_KEY = "";
    private static final String BASE_MOV_URL = "http://api.themoviedb.org/3/discover/movie";
    private static final String BASE_EXTRAS_URL = "http://api.themoviedb.org/3/movie";


    private final Context mContext;
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;

    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(LOG_TAG, "ASYNC STARTED ");

        String moviesJsonStr = null;
        String trailersJsonStr = null;
        String reviewsJsonStr = null;

        Vector<ContentValues> movieVector = null;

        final String SORT_PARAM = "sort_by";
        final String API_PARAM = "api_key";

        String sortPref = Utility.getSortOrder(mContext);

        try {
            // Construct the URL for the themoviedb query
            Uri builtUri = Uri.parse(BASE_MOV_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortPref)
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            moviesJsonStr = fetchFromURL(url);
            movieVector = getMoviesDataFromJson(moviesJsonStr);
            ContentValues[] cvArray = new ContentValues[movieVector.size()];
            movieVector.toArray(cvArray);

            int mov_id;
            for (ContentValues cv : cvArray) {
                mov_id = cv.getAsInteger(MoviesEntry.COLUMN_MOV_ID);
                Log.d(LOG_TAG, "ID is  " + cv.getAsInteger(MoviesEntry.COLUMN_MOV_ID));
                builtUri = Uri.parse(BASE_EXTRAS_URL).buildUpon()
                        .appendPath(Integer.toString(mov_id))
                        .appendPath("videos")
                        .appendQueryParameter(API_PARAM, API_KEY)
                        .build();
                url = new URL(builtUri.toString());
                Log.d(LOG_TAG, "URL1 is  " + url.toString());

                trailersJsonStr = fetchFromURL(url);

                builtUri = Uri.parse(BASE_EXTRAS_URL).buildUpon()
                        .appendPath(Integer.toString(mov_id))
                        .appendPath("reviews")
                        .appendQueryParameter(API_PARAM, API_KEY)
                        .build();
                url = new URL(builtUri.toString());
                Log.d(LOG_TAG, "URL2 is  " + url.toString());

                for (int i= 0; i<10; i++) {
                    try {
                        reviewsJsonStr = fetchFromURL(url);
                        break;
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "URLCONNECTION FAILED!");
                    }
                }
                cv.put(MoviesEntry.COLUMN_TRAILERS, trailersJsonStr);
                cv.put(MoviesEntry.COLUMN_REVIEWS, reviewsJsonStr);
            }
            Log.d(LOG_TAG, "FetchMoviesTask fetched. " + movieVector.size() + "movies");
            Log.d(LOG_TAG, "First Movies is: " + cvArray[0].getAsString(MoviesEntry.COLUMN_TITLE) + ", " +
                    cvArray[0].getAsString(MoviesEntry.COLUMN_RELEASE) + ", " +
                    cvArray[0].getAsString(MoviesEntry.COLUMN_POPULARITY) + ", " +
                    cvArray[0].getAsString(MoviesEntry.COLUMN_POSTER) + ", " +
                    cvArray[0].getAsString(MoviesEntry.COLUMN_TRAILERS) + ", " +
                    cvArray[0].getAsString(MoviesEntry.COLUMN_REVIEWS));

            int inserted = 0;
            try {
                inserted = mContext.getContentResolver().bulkInsert(MoviesEntry.CONTENT_URI, cvArray);
            } catch (Exception e) {
                Log.d(LOG_TAG, "SQL operation failed!\n" + e);
            }
            Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + "Inserted");


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
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
        return  null;
    }

    private String fetchFromURL(URL url) throws IOException {


        // Create the request to OpenWeatherMap, and open the connection
        urlConnection = null;
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        int status = urlConnection.getResponseCode();
        Log.d(LOG_TAG, "Connection status is " + status);


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

        urlConnection.disconnect();
        reader.close();

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        } else
            return buffer.toString();
    }

    private Vector<ContentValues> getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_OVERVIEW = "overview";
        final String MDB_VOTE_AVE = "vote_average";
        final String MDB_POPULARITY = "popularity";
        final String MDB_ORIG_TITLE = "original_title";
        final String MDB_RELEASE = "release_date";
        final String MDB_POSTER = "poster_path";
        final String MDB_MOV_ID = "id";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(MDB_RESULTS);

        int numOfResults = resultsArray.length();
        Vector<ContentValues> cvVector = new Vector<ContentValues> ();
        ContentValues values = new ContentValues();
        JSONObject singleMovie;
        for(int i = 0; i < numOfResults; i++) {

            // Get the JSON object representing a single movie
            singleMovie = resultsArray.getJSONObject(i);

            values = new ContentValues();

            values.put(MoviesEntry.COLUMN_MOV_ID, singleMovie.getInt(MDB_MOV_ID));
            values.put(MoviesEntry.COLUMN_OVERVIEW, singleMovie.getString(MDB_OVERVIEW));
            values.put(MoviesEntry.COLUMN_POSTER, singleMovie.getString(MDB_POSTER));
            values.put(MoviesEntry.COLUMN_RATING, singleMovie.getDouble(MDB_VOTE_AVE));
            values.put(MoviesEntry.COLUMN_POPULARITY, singleMovie.getDouble(MDB_POPULARITY));
            values.put(MoviesEntry.COLUMN_RELEASE, singleMovie.getString(MDB_RELEASE));
            values.put(MoviesEntry.COLUMN_TITLE, singleMovie.getString(MDB_ORIG_TITLE));

            cvVector.add(values);
        }
        return cvVector;
    }
}
