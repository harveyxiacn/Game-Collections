package com.itgarage.harvey.gamecollections.models;

import android.util.Log;

/**
 * This class is a model for game, format the attributes.
 */
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
    private int contactId;
    private int favourite;
    private int wish;

    public Game(int id, String title, String platform, String genre, String hardwarePlatform, String edition, String publicationDate, String releaseDate, String manufacturer, String smallImage, String mediumImage, String largeImage, int rating, String upcCode, int contactId, int favourite, int wish) {
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
        this.contactId = contactId;
        this.favourite = favourite;
        this.wish = wish;
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
        contactId = -1;
        favourite = 0;
        wish = 0;
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
        /*filter for remove platform name from title*/
        if (title.toLowerCase().contains("wiiu")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("wiiu");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("wiiu"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("wiiu");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("wiiu"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("wiiu"));
        }
        if (title.toLowerCase().contains("wii")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("wii");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("wii"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("wii");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("wii"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("wii"));
        }
        if (title.toLowerCase().contains("xbox")) {
            Log.i("game title", "contain xbox");
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("xbox");
                Log.i("game title", "- is infron of platform at "+(platformIndex-hIndex));
                if (platformIndex - hIndex <= 2) {
                    Log.i("game title", "- in front of platform "+hIndex+" "+platformIndex);
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, platformIndex);
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("xbox");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("xbox"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("xbox"));
        }
        if (title.toLowerCase().contains("playstation")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("playstation");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("playstation"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("playstation");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("playstation"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("playstation"));
        }
        if (title.toLowerCase().contains("pc")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("pc");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("pc"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("pc");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("pc"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("pc"));
        }
        if (title.toLowerCase().contains("mac")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("mac");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("mac"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("mac");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("mac"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("mac"));
        }
        if (title.toLowerCase().contains("nintendo 3ds")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("nintendo 3ds");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("nintendo 3ds"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("nintendo 3ds");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("nintendo 3ds"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("nintendo 3ds"));
        }
        if (title.toLowerCase().contains("nintendo ds")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("nintendo ds");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("nintendo ds"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("nintendo ds");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("nintendo ds"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("nintendo ds"));
        }
        if (title.toLowerCase().contains("sony psp")) {
            if (title.contains("-")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='-'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("sony psp");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("sony psp"));
                }
            } else if (title.contains("|")) {
                int hIndex = -1;
                for(int i=0; i<title.length(); i++){
                    if(title.charAt(i)=='|'){
                        hIndex = i;
                    }
                }
                int platformIndex = title.toLowerCase().indexOf("sony psp");
                if (platformIndex - hIndex <= 2) {
                    title = title.substring(0, hIndex);
                }else {
                    title = title.substring(0, title.toLowerCase().indexOf("sony psp"));
                }
            } else
                title = title.substring(0, title.toLowerCase().indexOf("sony psp"));
        }
        /*filter for title length*/
        if (!title.contains("\n")) {
            if (title.length() > 20) {
                Log.i("Title length", "" + title.length());
                int index = 20;
                do {
                    index--;
                } while (title.charAt(index) != ' ');
                String preSpaceTitle = title.substring(0, index);
                String postSpaceTitle = title.substring(index + 1);
                title = preSpaceTitle + "\n" + postSpaceTitle;
                Log.i("New title", title);
            }
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
        genre = genre.replaceAll("-", " ");
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

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public int getWish() {
        return wish;
    }

    public void setWish(int wish) {
        this.wish = wish;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + "'" +
                ", platform='" + platform + "'";
    }
}
