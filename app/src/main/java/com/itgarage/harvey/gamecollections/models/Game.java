package com.itgarage.harvey.gamecollections.models;

import android.util.Log;

public class Game {
    private int id;
    private String title;
    private String platform;
    private String genre;
    private String hardwarePlatform;
    private String edition;
    private String publicationDate;
    private String releaseDate;
    private String manufacturer;
    private String smallImage;
    private String mediumImage;
    private String largeImage;
    private int rating;
    private String upcCode;

    public Game(int id, String title, String platform, String genre, String hardwarePlatform, String edition, String publicationDate, String releaseDate, String manufacturer, String smallImage, String mediumImage, String largeImage, int rating, String upcCode) {
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.genre = genre;
        this.hardwarePlatform = hardwarePlatform;
        this.edition = edition;
        this.publicationDate = publicationDate;
        this.releaseDate = releaseDate;
        this.manufacturer = manufacturer;
        this.smallImage = smallImage;
        this.mediumImage = mediumImage;
        this.largeImage = largeImage;
        this.rating = rating;
        this.upcCode = upcCode;
    }

    public Game() {
        id = -1;
        title = null;
        platform = null;
        genre = null;
        hardwarePlatform = null;
        manufacturer = null;
        smallImage = null;
        mediumImage = null;
        largeImage = null;
        edition = null;
        publicationDate = null;
        releaseDate = null;
        rating = -1;
        upcCode = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title.length()>20){
            Log.i("Title length", ""+title.length());
            int index = 20;
            do{
                index--;
            }while (title.charAt(index) != ' ');
            String preSpaceTitle = title.substring(0, index-1);
            String postSpaceTitle = title.substring(index+1);
            title = preSpaceTitle + "\n" + postSpaceTitle;
            Log.i("New title", title);
        }
        this.title = title;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        genre = genre.replaceAll("_", " ");
        this.genre = genre;
    }

    public String getHardwarePlatform() {
        return hardwarePlatform;
    }

    public void setHardwarePlatform(String hardwarePlatform) {
        this.hardwarePlatform = hardwarePlatform;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getMediumImage() {
        return mediumImage;
    }

    public void setMediumImage(String mediumImage) {
        this.mediumImage = mediumImage;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUpcCode() {
        return upcCode;
    }

    public void setUpcCode(String upcCode) {
        this.upcCode = upcCode;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + "'" +
                ", platform='" + platform + "'";
    }
}
