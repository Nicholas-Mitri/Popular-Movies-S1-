/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.projects.nanodegree.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.sql.SQLException;

public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;
    private final String LOG_TAG = MoviesProvider.class.getSimpleName();

    static final int MOVIES = 100;
    static final int MOVIES_WITH_ID = 101;
    static final int FAVORITES = 200;
    static final int FAVORITES_WITH_ID = 201;

    // TODO DO YOU NEED A QUERY BUILDER AND A STATIC CONSTRUCTOR?
//    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
//    static{
//        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
//
//        //This is an inner join which looks like
//        //weather INNER JOIN location ON weather.location_id = location._id
//        sWeatherByLocationSettingQueryBuilder.setTables(
//                MoviesContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
//                        MoviesContract.LocationEntry.TABLE_NAME +
//                        " ON " + MoviesContract.WeatherEntry.TABLE_NAME +
//                        "." + MoviesContract.WeatherEntry.COLUMN_LOC_KEY +
//                        " = " + MoviesContract.LocationEntry.TABLE_NAME +
//                        "." + MoviesContract.LocationEntry._ID);
//    }

    //Movies.mov_id = ?
    private static final String sMoviesIDSelection =
            MoviesContract.MoviesEntry.TABLE_NAME+
                    "." + MoviesContract.MoviesEntry.COLUMN_MOV_ID + " = ? ";

    //Movies.mov_id = ?
    private static final String sFavoritesIDSelection =
            MoviesContract.FavoritesEntry.TABLE_NAME+
                    "." + MoviesContract.FavoritesEntry.COLUMN_MOV_ID + " = ? ";

// TODO make decision about code that is commented out
//    //location.location_setting = ? AND date = ?
//    private static final String sLocationSettingAndDaySelection =
//            MoviesContract.LocationEntry.TABLE_NAME +
//                    "." + MoviesContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    MoviesContract.WeatherEntry.COLUMN_DATE + " = ? ";

//    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
//        String locationSetting = MoviesContract.WeatherEntry.getLocationSettingFromUri(uri);
//        long startDate = MoviesContract.WeatherEntry.getStartDateFromUri(uri);
//
//        String[] selectionArgs;
//        String selection;
//
//        if (startDate == 0) {
//            selection = sLocationSettingSelection;
//            selectionArgs = new String[]{locationSetting};
//        } else {
//            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
//            selection = sLocationSettingWithStartDateSelection;
//        }
//
//        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                sortOrder
//        );
//    }
//
//    private Cursor getWeatherByLocationSettingAndDate(
//            Uri uri, String[] projection, String sortOrder) {
//        String locationSetting = MoviesContract.WeatherEntry.getLocationSettingFromUri(uri);
//        long date = MoviesContract.WeatherEntry.getDateFromUri(uri);
//
//        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                sLocationSettingAndDaySelection,
//                new String[]{locationSetting, Long.toString(date)},
//                null,
//                null,
//                sortOrder
//        );
//    }


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIES_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/*", FAVORITES_WITH_ID);

        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new MoviesDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIES_WITH_ID:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MoviesContract.FavoritesEntry.CONTENT_TYPE;
            case FAVORITES_WITH_ID:
                return MoviesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        Log.d(LOG_TAG, "URI is " + uri);

        switch (sUriMatcher.match(uri)) {


            // "movies"
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movies/*"
            case MOVIES_WITH_ID: {
                String mov_id =  MoviesContract.MoviesEntry.getMovIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        sMoviesIDSelection,
                        new String[]{mov_id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "favorites"
            case FAVORITES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "favorites/*"
            case FAVORITES_WITH_ID: {
                String mov_id =  MoviesContract.FavoritesEntry.getMovIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        sFavoritesIDSelection,
                        new String[]{mov_id},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES: {
                long _id = db.insert(MoviesContract.FavoritesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.FavoritesEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = db.delete(
                        MoviesContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITES:
                rowsUpdated = db.update(MoviesContract.FavoritesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.d(LOG_TAG, "Matched int is " + match);

        delete(uri, null, null);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}