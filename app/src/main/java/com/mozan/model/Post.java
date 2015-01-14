package com.mozan.model;

import java.util.ArrayList;

/**
 * Created by User on 16.12.2014.
 */
public class Post{
    private String id, content, category;
    private String thumbnailUrl = "";
    private String username;
    private String price = "";
    private ArrayList<String> genre;

    public Post() {
    }

    public Post(String id, String content, String category, String thumbnailUrl, String price, String username,
                 ArrayList<String> genre) {
        this.id = id;
        this.content = content;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
        this.username = username;
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String name) {
        this.content = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String name) {
        this.category = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

}
