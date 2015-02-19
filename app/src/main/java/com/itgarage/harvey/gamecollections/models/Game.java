package com.itgarage.harvey.gamecollections.models;

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

    public Game(int id, String title, String platform, String genre, String hardwarePlatform, String edition, String publicationDate, String releaseDate, String manufacturer, String smallImage, String mediumImage, String largeImage) {
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
    }

    public Game() {
        id = -1;
        title = null;
        platform = null;
        genre = null;
        hardwarePlatform = null;
        manufacturer = null;
        smallImage = null;
        manufacturer = null;
        largeImage = null;
        edition = null;
        publicationDate = null;
        releaseDate = null;
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

    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + "'" +
                ", platform='" + platform + "'";
    }
}
