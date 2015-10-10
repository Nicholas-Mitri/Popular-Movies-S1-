package com.projects.nanodegree.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MoviesContract {


    public static final String CONTENT_AUTHORITY = "com.projects.nanodegree.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVORITES = "favorites";


    /* Inner class that defines the table contents of the Movies table */
    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOV_ID = "mov_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE = "release";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_TRAILERS = "trailers";
        public static final String COLUMN_REVIEWS = "reviews";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieWithMovId(String mdbId) {
            return CONTENT_URI.buildUpon().appendPath(mdbId).build();
        }

        public static String getMovIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /* Inner class that defines the table contents of the Favorites nevermind table */
    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOV_ID = "mov_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE = "release";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_TRAILERS = "trailers";
        public static final String COLUMN_REVIEWS = "reviews";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavoriteWithMovId(String mdbId) {
            return CONTENT_URI.buildUpon().appendPath(mdbId).build();
        }

        public static String getMovIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
