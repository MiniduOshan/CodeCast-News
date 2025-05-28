// File: app/app/src/main/java/com/example/codecastnews/models/NewsArticle.java
package com.example.codecastnews.models;

public class NewsArticle {
    private String id;
    private String title;
    private String date;
    private String imageName;
    private String type; // e.g., "academic", "sport", "event", "featured" (for the global featured)
    private String timeAgo;
    private String description;
    private boolean isFeaturedForCategory; // NEW FIELD: indicates if it's a featured article for its category

    // Required empty constructor for Firebase
    public NewsArticle() {
    }

    // Constructor with new field
    public NewsArticle(String title, String date, String imageName, String type, String timeAgo, String description, boolean isFeaturedForCategory) {
        this.title = title;
        this.date = date;
        this.imageName = imageName;
        this.type = type;
        this.timeAgo = timeAgo;
        this.description = description;
        this.isFeaturedForCategory = isFeaturedForCategory;
    }

    // Existing constructor (can keep for backward compatibility or remove if not used elsewhere)
    public NewsArticle(String title, String date, String imageName, String type, String timeAgo, String description) {
        this.title = title;
        this.date = date;
        this.imageName = imageName;
        this.type = type;
        this.timeAgo = timeAgo;
        this.description = description;
        this.isFeaturedForCategory = false; // Default to false if using this constructor
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getImageName() {
        return imageName;
    }

    public String getType() {
        return type;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFeaturedForCategory() { // Getter for the new field
        return isFeaturedForCategory;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFeaturedForCategory(boolean featuredForCategory) { // Setter for the new field
        isFeaturedForCategory = featuredForCategory;
    }
}