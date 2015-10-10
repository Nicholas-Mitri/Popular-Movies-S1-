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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.projects.nanodegree.popularmovies.data.MoviesContract.MoviesEntry;
import com.projects.nanodegree.popularmovies.data.MoviesContract.FavoritesEntry;

/**
 * Manages a local database for weather data.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 9;
    private final String LOG_TAG = MoviesDbHelper.class.getSimpleName();

    static final String DATABASE_NAME = "popularmovies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesEntry.COLUMN_MOV_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_TRAILERS + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_REVIEWS + " TEXT NOT NULL, " +
                " UNIQUE (" + MoviesEntry.COLUMN_MOV_ID + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesEntry.COLUMN_MOV_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RELEASE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_TRAILERS + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_REVIEWS + " TEXT NOT NULL," +
                " UNIQUE (" + MoviesEntry.COLUMN_MOV_ID + ") ON CONFLICT IGNORE);";


        Log.d(LOG_TAG, SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
