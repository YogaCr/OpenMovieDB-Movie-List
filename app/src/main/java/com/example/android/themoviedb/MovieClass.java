package com.example.android.themoviedb;


/**
 * Created by Sakata Yoga on 20/02/2018.
 */

public class MovieClass {
    private String posterUrl;
    private String idFilm;

    public void setIdFilm(String idFilm) {
        this.idFilm = idFilm;
    }

    public String getIdFilm() {

        return idFilm;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

}
