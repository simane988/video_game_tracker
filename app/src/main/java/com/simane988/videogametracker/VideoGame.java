package com.simane988.videogametracker;

public class VideoGame {

    private String name;
    private boolean owned;
    private String completion_status;
    private int rating;
    private final String genres;


    public VideoGame() {
        this.name = "No Name";
        this.owned = false;
        this.completion_status = "N/A";
        this.rating = 0;
        this.genres = "No Genre";
    }

    public VideoGame(String name, boolean owned, String cstatus, int rating, String genre) {
        this.name = name;
        this.owned = owned;
        this.completion_status = cstatus;
        this.rating = rating;
        this.genres = genre;
    }


    public String getName() {
        return this.name;
    }

    public boolean isOwned() {
        return this.owned;
    }

    public String getCompletionStatus() {
        return this.completion_status;
    }

    public int getRating() {
        return this.rating;
    }

    public String getRatingAsString() {
        return Integer.toString(this.rating);
    }

    public String getGenres() {
        return this.genres;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setCompletionStatus(String cstatus) {
        this.completion_status = cstatus;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void changeOwnedStatus() {


        this.owned = !this.owned;
    }
}
