package com.example.codecastnews.models;

import java.io.Serializable;

public class NewsArticle implements Serializable {
    private String id;
    private String title;
    private String date;
    private String imageName; // This should be the drawable resource name (e.g., "media", "spectral_20")
    private String type;
    private String timeAgo;
    private String description; // This will hold the full news content for the detail screen
    private boolean isFeaturedForCategory;

    public NewsArticle() {
        // Required empty constructor for Firebase
    }

    public NewsArticle(String title, String date, String imageName, String type, String timeAgo, String description, boolean isFeaturedForCategory) {
        this.title = title;
        this.date = date;
        this.imageName = imageName;
        this.type = type;
        this.timeAgo = timeAgo;
        this.description = description;
        this.isFeaturedForCategory = isFeaturedForCategory;
    }

    // You can keep this constructor if you use it, or remove it if the above is sufficient.
    public NewsArticle(String title, String date, String imageName, String type, String timeAgo, String description) {
        this.title = title;
        this.date = date;
        this.imageName = imageName;
        this.type = type;
        this.timeAgo = timeAgo;
        this.description = description;
        this.isFeaturedForCategory = false; // Default to false
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getImageName() { return imageName; }
    public String getType() { return type; }
    public String getTimeAgo() { return timeAgo; }
    public String getDescription() { return description; }
    public boolean isFeaturedForCategory() { return isFeaturedForCategory; }

    // Setters (Firebase might use these, or you can use them if you build the object programmatically)
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public void setType(String type) { this.type = type; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
    public void setDescription(String description) { this.description = description; }
    public void setFeaturedForCategory(boolean featuredForCategory) { isFeaturedForCategory = featuredForCategory; }
}