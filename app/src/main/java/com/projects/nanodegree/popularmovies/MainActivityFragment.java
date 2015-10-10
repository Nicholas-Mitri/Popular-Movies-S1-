package com.projects.nanodegree.popularmovies;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.projects.nanodegree.popularmovies.data.MoviesContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();;
    private Toast mToast = null;
    private int mPosition = GridView.INVALID_POSITION;


    // TODO clean up class fields

    private  static final int MOVIE_LOADER = 0;
    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOV_ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_RELEASE,
            MoviesContract.MoviesEntry.COLUMN_RATING,
            MoviesContract.MoviesEntry.COLUMN_POSTER,
            MoviesContract.MoviesEntry.COLUMN_REVIEWS,
            MoviesContract.MoviesEntry.COLUMN_TRAILERS
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
            MoviesContract.FavoritesEntry.COLUMN_TRAILERS
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

    private MoviesAdapter mMoviesAdapter;
    private GridView movie_gridview;
    private static final String SELECTED_KEY = "selected_position";


    public MainActivityFragment() {
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);

        movie_gridview = (GridView) rootView.findViewById(R.id.gridview_movies);
        movie_gridview.setAdapter(mMoviesAdapter);

        movie_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String sortPref = Utility.getSortOrder(getActivity());
//                    Log.d(LOG_TAG, "Sort Preference is " + sortPref);

                    Intent intent;
                    Uri itemUri;
                    if (!sortPref.equals("favorites")) {
                        itemUri = MoviesContract.MoviesEntry.buildMovieWithMovId(
                                Integer.toString(cursor.getInt(COL_MOV_ID)));
                    } else {
                        itemUri = MoviesContract.FavoritesEntry.buildFavoriteWithMovId(
                                Integer.toString(cursor.getInt(COL_MOV_ID)));
                    }

                    ((Callback) getActivity())
                            .onItemSelected(itemUri);


                }
                mPosition = position;

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            Log.d(LOG_TAG,"position at " + mPosition);
            if (mPosition != GridView.INVALID_POSITION) {
                // If we don't need to restart the loader, and there's a desired position to restore
                // to, do so now.
                Log.d(LOG_TAG,"position at " + mPosition);

                movie_gridview.smoothScrollToPositionFromTop(mPosition, 0);
            }
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            String sortPref = Utility.getSortOrder(getActivity());

            if (!sortPref.equals("favorites"))
                updateMovies();
            else {
                if (mToast != null)
                    mToast.cancel();

                mToast = Toast.makeText(getActivity(), "Can't refresh favorite list. Switch to another sorting order to refresh list.", Toast.LENGTH_LONG);
                mToast.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void onSortChange() {
        Log.d(LOG_TAG, "OnSortChange!!");
        mPosition = GridView.INVALID_POSITION;
        String sortPref = Utility.getSortOrder(getActivity());
        if (!sortPref.equals("favorites"))
            updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        moviesTask.execute();

        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(getActivity(), "Fetching Movies...", Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.d(LOG_TAG, "Loader RESTARTED!!");
        String sortPref = Utility.getSortOrder(getActivity());
        Uri uri;
        String[] COLUMNS;

        if (!sortPref.equals("favorites")) {
            uri = MoviesContract.MoviesEntry.CONTENT_URI;
            COLUMNS = MOVIE_COLUMNS;
        } else {
            uri = MoviesContract.FavoritesEntry.CONTENT_URI;
            COLUMNS = FAVORITES_COLUMNS;
        }

        return new CursorLoader(getActivity(),
                uri,
                COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }


}