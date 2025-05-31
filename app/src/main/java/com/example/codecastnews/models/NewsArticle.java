package com.example.codecastnews.models;

import java.io.Serializable;

public class NewsArticle implements Serializable {
    private String id; // This will hold the Firebase unique key for the article
    private String title;
    private String date;
    private String imageName; // IMPORTANT: This MUST exactly match the key in your Firebase (e.g., "football", "general", "academic_building")
    private String type; // e.g., "academic", "sport", "event"
    private String timeAgo; // String representation of "time ago" (e.g., "2 hours ago")
    private String description; // Full news content for the detail screen
    private boolean isFeaturedForCategory; // IMPORTANT: This MUST exactly match the key in your Firebase (boolean true/false)

    // Required empty constructor for Firebase DataSnapshot.getValue(NewsArticle.class)
    public NewsArticle() {
    }

    // Constructor with all fields for creating new NewsArticle objects programmatically
    public NewsArticle(String title, String date, String imageName, String type, String timeAgo, String description, boolean isFeaturedForCategory) {
        this.title = title;
        this.date = date;
        this.imageName = imageName;
        this.type = type;
        this.timeAgo = timeAgo;
        this.description = description;
        this.isFeaturedForCategory = isFeaturedForCategory;
    }

    // Optional constructor for convenience if you often create non-featured articles
    public NewsArticle(String title, String date, String imageName, String type, String timeAgo, String description) {
        this(title, date, imageName, type, timeAgo, description, false); // Defaults isFeaturedForCategory to false
    }

    // --- Getters ---
    // Firebase uses these getters to deserialize data. Ensure field names match your Firebase keys.
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    // Getter for 'imageName' - must be 'getImageName()' for Firebase to map 'imageName' key
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

    // Getter for 'isFeaturedForCategory' - must be 'isFeaturedForCategory()' for Firebase to map 'isFeaturedForCategory' key
    public boolean isFeaturedForCategory() {
        return isFeaturedForCategory;
    }

    // --- Setters ---
    // Firebase uses these setters to populate the object after deserialization.
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Setter for 'imageName' - must be 'setImageName()'
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

    // Setter for 'isFeaturedForCategory' - must be 'setFeaturedForCategory()'
    public void setFeaturedForCategory(boolean featuredForCategory) {
        isFeaturedForCategory = featuredForCategory;
    }
}