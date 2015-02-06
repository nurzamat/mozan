package com.mozan.model;

import java.util.ArrayList;

/**
 * Created by User on 16.12.2014.
 */
public class Post{
    private String id, content, username, category, category_name, user_id, quickblox_id, displayed_name, hitcount, hitcount_id;
    private String thumbnailUrl = "";
    private String avatarUrl = "";
    private String price, price_currency = "";
    //private ArrayList<String> image_urls;
    private ArrayList<Image> images;

    public Post() {
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

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String name) {
        this.category_name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setAvatarUrl(String url) {
        this.avatarUrl = url;
    }

    public String getAvatarUrl() {
        return avatarUrl;
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

    public String getPriceCurrency() {
        return price_currency;
    }

    public void setPriceCurrency(String price_currency) {
        this.price_currency = price_currency;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> _images)
    {
        this.images = _images;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getDisplayedName() {
        return displayed_name;
    }

    public void setDisplayedName(String displayed_name) {
        this.displayed_name = displayed_name;
    }

    public String getHitcount() {
        return hitcount;
    }

    public void setHitcount(String count) {
        this.hitcount = count;
    }

    public String getHitcountId() {
        return hitcount_id;
    }

    public void setHitcountId(String id) {
        this.hitcount_id = id;
    }

    //quickblox
    public String getQuickbloxId() {
        return quickblox_id;
    }

    public void setQuickbloxId(String _quickblox_id) {
        this.quickblox_id = _quickblox_id;
    }
}
