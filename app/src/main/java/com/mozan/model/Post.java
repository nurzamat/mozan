package com.mozan.model;

import java.util.ArrayList;

/**
 * Created by User on 16.12.2014.
 */
public class Post{
    private String id, content, username, category;
    private String thumbnailUrl = "";
    private String price = "";
    private ArrayList<String> image_urls;

    public Post() {
    }

    public Post(String id, String content, String category, String thumbnailUrl, String price, String username,
                 ArrayList<String> image_urls) {
        this.id = id;
        this.content = content;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.price = price;
        this.username = username;
        this.image_urls = image_urls;
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

    public ArrayList<String> getImageUrls() {
        return image_urls;
    }

    public void setImageUrls(ArrayList<String> image_urls)
    {
        this.image_urls = image_urls;
    }

}
