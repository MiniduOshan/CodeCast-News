package com.example.codecastnews;

import android.content.Intent; // Import Intent
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // Import CardView
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.codecastnews.adapters.NewsAdapter;
import com.example.codecastnews.models.NewsArticle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NewsScreen Activity: Displays a list of news articles categorized by type (Academic, Sport, Event).
 * Fetches news data from Firebase Realtime Database.
 * Features a dynamic "featured news" card and a RecyclerView for general news.
 * Includes a bottom navigation bar for switching categories.
 */
public class NewsScreen extends AppCompatActivity {

    // TAG for logging messages
    private static final String TAG = "NewsScreen";

    // UI components declarations
    private RecyclerView newsRecyclerView; // RecyclerView to display a list of news articles
    private NewsAdapter newsAdapter; // Adapter for the RecyclerView
    private List<NewsArticle> allNewsList; // Master list holding ALL news articles fetched from Firebase
    private List<NewsArticle> filteredNewsList; // List holding news articles filtered by the current type for RecyclerView

    // Firebase Database reference
    private DatabaseReference newsDatabase; // Reference to the "news" node in Firebase

    // Featured News Card UI elements
    private CardView featuredNewsCard; // Add CardView for click listener
    private ImageView featuredNewsImage; // ImageView for the featured news article's image
    private TextView featuredNewsTitle; // TextView for the featured news article's title
    private TextView featuredNewsDate; // TextView for the featured news article's date
    private TextView sectionTitleTextView; // TextView to display the title of the current news section (e.g., "Academic")

    // Bottom Navigation Tab Layouts and their child views
    private LinearLayout navAcademic, navSport, navEvent; // Layouts for each navigation tab
    private ImageView iconAcademic, iconSport, iconEvent; // Icons for each navigation tab
    private TextView textAcademic, textSport, textEvent; // Text labels for each navigation tab

    // State Variable: Keeps track of the currently selected news category
    private String currentNewsType = "academic"; // Default selected news category is "academic"

    // UI Constants for colors
    private final int SELECTED_COLOR = Color.WHITE; // Color for selected tab elements
    private final int UNSELECTED_COLOR = Color.parseColor("#B0BEC5"); // Color for unselected tab elements (light grey)

    /**
     * Called when the activity is first created.
     * Initializes UI components, sets up Firebase, and fetches data.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable full-screen edge-to-edge display
        setContentView(R.layout.activity_news_screen); // Set the layout for this activity

        // Apply system window insets to adjust padding for status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database Persistence
        // Enables offline capabilities by caching data locally.
        // Needs to be called only once per application instance.
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            // Log an error if persistence fails (e.g., already called elsewhere)
            Log.e(TAG, "Error enabling Firebase persistence. This usually means it was already called: " + e.getMessage());
        }
        // Get a reference to the "news" node in your Firebase Realtime Database
        newsDatabase = FirebaseDatabase.getInstance().getReference("news");

        // Initialize RecyclerView and its components
        newsRecyclerView = findViewById(R.id.newsRecyclerView); // Get RecyclerView from layout
        // Set a LinearLayoutManager to arrange items in a vertical list
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allNewsList = new ArrayList<>(); // Initialize the master list of all news articles
        filteredNewsList = new ArrayList<>(); // Initialize the list for filtered news
        // Create and set the adapter for the RecyclerView
        newsAdapter = new NewsAdapter(this, filteredNewsList);
        newsRecyclerView.setAdapter(newsAdapter);

        // Initialize Featured News UI elements by finding them in the layout
        featuredNewsCard = findViewById(R.id.featuredNewsCard); // Initialize the CardView
        featuredNewsImage = findViewById(R.id.featuredNewsImage);
        featuredNewsTitle = findViewById(R.id.featuredNewsTitle);
        featuredNewsDate = findViewById(R.id.featuredNewsDate);
        sectionTitleTextView = findViewById(R.id.sectionTitleTextView);

        // Initialize Bottom Navigation Tab Layouts and their icons/texts
        navAcademic = findViewById(R.id.navAcademic);
        navSport = findViewById(R.id.navSport);
        navEvent = findViewById(R.id.navEvent);

        iconAcademic = findViewById(R.id.iconAcademic);
        iconSport = findViewById(R.id.iconSport);
        iconEvent = findViewById(R.id.iconEvent);

        textAcademic = findViewById(R.id.textAcademic);
        textSport = findViewById(R.id.textSport);
        textEvent = findViewById(R.id.textEvent);

        // Set Click Listeners for Bottom Navigation Tabs
        // When a tab is clicked, call selectTab() with its corresponding type
        navAcademic.setOnClickListener(v -> selectTab("academic"));
        navSport.setOnClickListener(v -> selectTab("sport"));
        navEvent.setOnClickListener(v -> selectTab("event"));

        // Fetch news data from Firebase when the activity starts
        fetchNewsFromFirebase();

        // The addDummyNewsToFirebase() method call has been removed from here
        // as the data is already in Firebase.
    }

    /**
     * Handles the selection of a navigation tab.
     * Updates the UI (section title, tab colors), finds and displays the featured article
     * for the selected category, and filters the general news list for the RecyclerView.
     * @param type The type of news to display ("academic", "sport", "event").
     */
    private void selectTab(String type) {
        currentNewsType = type; // Update the state variable to the newly selected type
        sectionTitleTextView.setText(capitalizeFirstLetter(type)); // Set the section title (e.g., "Academic")
        setSelectedTabColor(type); // Update the colors of the bottom navigation tabs

        // Find and display the featured news article relevant to the selected category
        updateFeaturedNewsCard(type);

        // Filter the main news list and update the RecyclerView with articles
        // that match the selected type and are NOT featured.
        filterNewsByType(type);
    }

    /**
     * Finds the single article marked as featured for the given category from `allNewsList`
     * and updates the `featuredNewsCard` UI elements (image, title, date).
     * If no featured article is found for the category, displays a default "No Featured News" message.
     * @param categoryType The category to find the featured article for (e.g., "academic").
     */
    private void updateFeaturedNewsCard(String categoryType) {
        NewsArticle featuredArticleForCategory = null; // Variable to hold the found featured article
        for (NewsArticle article : allNewsList) {
            // Check if the article matches the current category AND is marked as featured for that category
            if (categoryType.equalsIgnoreCase(article.getType()) && article.isFeaturedForCategory()) {
                featuredArticleForCategory = article; // Found the featured article
                break; // Exit loop once found, as there should only be one featured per category
            }
        }

        // Update UI based on whether a featured article was found
        if (featuredArticleForCategory != null) {
            final NewsArticle articleToDisplay = featuredArticleForCategory; // Final variable for click listener

            // Set the title and date from the featured article
            featuredNewsTitle.setText(articleToDisplay.getTitle());
            featuredNewsDate.setText("Date: " + articleToDisplay.getDate());

            // Get the resource ID of the image from the drawable folder using its name
            int featuredImageResId = getResources().getIdentifier(
                    articleToDisplay.getImageName(), // Image name from the article (e.g., "media")
                    "drawable",                     // Look in the 'drawable' folder
                    getPackageName()                // Use the current application's package name
            );

            // Load the image using Glide for efficient image loading
            if (featuredImageResId != 0) { // If a valid drawable resource ID is found
                Glide.with(NewsScreen.this) // Context for Glide
                        .load(featuredImageResId) // The image resource to load
                        .placeholder(R.drawable.placeholder_image) // Placeholder while loading (optional)
                        .error(R.drawable.error_image)         // Image to show if loading fails (optional)
                        .into(featuredNewsImage); // Target ImageView
            } else {
                // If the image resource is not found, display a default "no image" placeholder
                featuredNewsImage.setImageResource(R.drawable.no_image_available);
                Log.w(TAG, "Featured image for " + categoryType + " not found: " + articleToDisplay.getImageName());
            }

            // Set OnClickListener for the featured news card
            featuredNewsCard.setOnClickListener(v -> {
                Intent intent = new Intent(NewsScreen.this, NewsDetailScreen.class);
                intent.putExtra(NewsDetailScreen.EXTRA_NEWS_ARTICLE, articleToDisplay);
                startActivity(intent);
            });

        } else {
            // If no featured article is found for the current category, display a default message
            featuredNewsTitle.setText("No Featured News for " + capitalizeFirstLetter(categoryType));
            featuredNewsDate.setText(""); // Clear the date text
            featuredNewsImage.setImageResource(R.drawable.no_image_available); // Show a default "no image"
            // Disable click listener if no featured article
            featuredNewsCard.setOnClickListener(null);
        }
    }


    /**
     * Filters the 'allNewsList' based on the given news type.
     * Only articles matching the type and NOT marked as 'isFeaturedForCategory' are added
     * to `filteredNewsList`, which then updates the RecyclerView.
     * @param type The type of news to display (e.g., "academic").
     */
    private void filterNewsByType(String type) {
        filteredNewsList.clear(); // Clear the previous filtered list

        // Iterate through all news articles
        for (NewsArticle article : allNewsList) {
            // Add article to filtered list if its type matches the selected type
            // AND it is NOT a featured article for its category (to avoid duplication with the featured card)
            if (type.equalsIgnoreCase(article.getType()) && !article.isFeaturedForCategory()) {
                filteredNewsList.add(article);
            }
        }
        Collections.shuffle(filteredNewsList); // Optional: Shuffle the filtered list for varied display
        newsAdapter.updateNewsList(filteredNewsList); // Update the RecyclerView adapter with the new list
    }

    /**
     * Resets all bottom navigation tab icons and text colors to their unselected state,
     * and then highlights the selected tab's icon and text.
     * @param selectedType The type of tab that is currently selected (e.g., "academic").
     */
    private void setSelectedTabColor(String selectedType) {
        // Reset all icons and text colors to the unselected state
        iconAcademic.setColorFilter(UNSELECTED_COLOR);
        textAcademic.setTextColor(UNSELECTED_COLOR);
        iconSport.setColorFilter(UNSELECTED_COLOR);
        textSport.setTextColor(UNSELECTED_COLOR);
        iconEvent.setColorFilter(UNSELECTED_COLOR);
        textEvent.setTextColor(UNSELECTED_COLOR);

        // Set the selected tab's icon and text color to the selected state based on type
        switch (selectedType) {
            case "academic":
                iconAcademic.setColorFilter(SELECTED_COLOR); // Set icon color to white
                textAcademic.setTextColor(SELECTED_COLOR); // Set text color to white
                break;
            case "sport":
                iconSport.setColorFilter(SELECTED_COLOR);
                textSport.setTextColor(SELECTED_COLOR);
                break;
            case "event":
                iconEvent.setColorFilter(SELECTED_COLOR);
                textEvent.setTextColor(SELECTED_COLOR);
                break;
        }
    }

    /**
     * Helper method to capitalize the first letter of a given string.
     * Used for display titles like "Academic", "Sport", "Event".
     * @param text The input string.
     * @return The string with its first letter capitalized.
     */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text; // Return as is if null or empty
        }
        // Convert first character to uppercase and append the rest of the string in lowercase
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Fetches all news articles from the Firebase Realtime Database.
     * Uses a ValueEventListener to listen for real-time updates to the "news" node.
     * When data changes, it clears the existing list, repopulates it, and updates the UI.
     */
    private void fetchNewsFromFirebase() {
        newsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allNewsList.clear(); // Clear the master list to prevent duplicate data when data changes

                // Iterate through each child node (which represents a NewsArticle) in the "news" data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Deserialize the Firebase dataSnapshot into a NewsArticle object
                    NewsArticle article = postSnapshot.getValue(NewsArticle.class);
                    if (article != null) {
                        article.setId(postSnapshot.getKey()); // Set the Firebase unique key as the article ID
                        allNewsList.add(article); // Add the article to the master list
                    }
                }
                // After fetching all news, call selectTab to initialize the featured card
                // and RecyclerView for the default 'currentNewsType' (initially "academic").
                // This ensures the UI is populated as soon as data is available.
                selectTab(currentNewsType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data fetching from Firebase
                Log.w(TAG, "Failed to load news from Firebase: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(NewsScreen.this, "Failed to load news: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show(); // Display a short error message to the user
            }
        });
    }
}