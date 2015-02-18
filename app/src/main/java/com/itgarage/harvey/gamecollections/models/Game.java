package com.itgarage.harvey.gamecollections.models;

public class Game {
    private int id;
    private String title, platform, genre, hardwarePlatform,
            manufacturer, feature, smallImage, mediumImage, largeImage;

    public Game(int id, String title, String platform, String genre, String hardwarePlatform, String manufacturer, String feature, String smallImage, String mediumImage, String largeImage) {
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.genre = genre;
        this.hardwarePlatform = hardwarePlatform;
        this.manufacturer = manufacturer;
        this.feature = feature;
        this.smallImage = smallImage;
        this.mediumImage = mediumImage;
        this.largeImage = largeImage;
    }

    public Game() {
        id = -1;
        title = null;
        platform = null;
        genre = null;
        hardwarePlatform = null;
        manufacturer = null;
        feature = null;
        smallImage = null;
        manufacturer = null;
        largeImage = null;
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
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

    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + "'" +
                ", platform='" + platform + "'";
    }
}
