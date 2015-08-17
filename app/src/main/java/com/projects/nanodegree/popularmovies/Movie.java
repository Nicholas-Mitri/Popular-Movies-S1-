package com.projects.nanodegree.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicholas on 8/17/2015.
 */
public class Movie implements Parcelable{

    private String title;
    private String releaseDate;
    private String vote;
    private String overview;
    private String posterURL;

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVote() {
        return vote;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterURL() {
        return posterURL;
    }

    protected Movie(String mTitle, String mReleaseDate, Double mVote, String mOverview,
                    String mPosterURL) {
        title = mTitle;
        releaseDate = mReleaseDate;
        vote = Double.toString(mVote);
        overview = mOverview;
        posterURL = mPosterURL;

    }
    protected Movie(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        vote = in.readString();
        overview = in.readString();
        posterURL = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(vote);
        dest.writeString(overview);
        dest.writeString(posterURL);
    }
}
