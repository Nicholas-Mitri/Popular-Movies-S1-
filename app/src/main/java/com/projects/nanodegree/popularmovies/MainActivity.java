package com.projects.nanodegree.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String DETAILSFRAGMENT_TAG = "DFTAG";

    private String mSortPref;
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "MAIN ACTIVITY CREATED!");
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
            mSortPref = Utility.getSortOrder(this);
        else
            mSortPref = savedInstanceState.getString("SORT", null);

        if(findViewById(R.id.container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new DetailActivityFragment(), DETAILSFRAGMENT_TAG)
                        .commit();
            }
        }else
            mTwoPane = false;


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveState CREATED!");
        outState.putString("SORT", mSortPref);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, DETAILSFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "MAIN ACTIVITY RESUMED!");
        String sortPref = Utility.getSortOrder(this);
        Log.d(LOG_TAG, "Sort Order is " + sortPref);
        Log.d(LOG_TAG, "Sort Order is " + mSortPref);

        if (sortPref != null && !sortPref.equals(mSortPref)) {
            Log.d(LOG_TAG, "SORTING CHANGED!!");
            mSortPref = sortPref;
            MainActivityFragment mf = (MainActivityFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.main_fragment);
            if (mf != null) {
                mf.onSortChange();
            }
            DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager()
                    .findFragmentByTag(DETAILSFRAGMENT_TAG);
            if (df != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(df)
                        .commit();
            }

        }
    }
}
