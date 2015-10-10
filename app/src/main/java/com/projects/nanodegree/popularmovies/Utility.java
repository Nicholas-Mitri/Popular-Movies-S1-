package com.projects.nanodegree.popularmovies;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static final String API_KEY = "";
    public static final String BASE_IMG_URL = "http://image.tmdb.org/t/p/";
    public static final String STATE_MOVIES = "state_movies";
    public String BASE_MOV_URL = "http://api.themoviedb.org/3/discover/movie";
    public static final String IMG_SIZE = "w500";

    public static String getSortOrder(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String sortPref = sharedPref.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));

        return sortPref;
    }

    public static long convertDateStringToTime(String dateStr, String format) {
        if (format == null) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            return 0;
        }
        return date.getTime();
    }
}
