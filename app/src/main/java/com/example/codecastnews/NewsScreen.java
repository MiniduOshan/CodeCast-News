package com.example.codecastnews;

import android.content.Intent;
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
import androidx.cardview.widget.CardView;
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
 * Includes a bottom navigation bar for switching categories and handling navigation from NewsDetailScreen.
 */
public class NewsScreen extends AppCompatActivity {

    // TAG for logging messages
    private static final String TAG = "NewsScreen";

    // UI components declarations
    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsArticle> allNewsList; // Master list holding ALL news articles fetched from Firebase
    private List<NewsArticle> filteredNewsList; // List holding news articles filtered by the current type for RecyclerView

    // Firebase Database reference
    private DatabaseReference newsDatabase;

    // Featured News Card UI elements
    private CardView featuredNewsCard;
    private ImageView featuredNewsImage;
    private TextView featuredNewsTitle;
    private TextView featuredNewsDate;
    private TextView sectionTitleTextView;

    // Bottom Navigation Tab Layouts and their child views
    private LinearLayout navAcademic, navSport, navEvent;
    private ImageView iconAcademic, iconSport, iconEvent;
    private TextView textAcademic, textSport, textEvent;

    // State Variable: Keeps track of the currently selected news category
    private String currentNewsType = "academic"; // Default selected news category is "academic"

    // UI Constants for colors
    private final int SELECTED_COLOR = Color.WHITE; // Color for selected tab elements
    private final int UNSELECTED_COLOR = Color.parseColor("#B0BEC5"); // Color for unselected tab elements (light grey)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database Persistence
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Error enabling Firebase persistence. This usually means it was already called: " + e.getMessage());
        }
        newsDatabase = FirebaseDatabase.getInstance().getReference("news");

        // Initialize RecyclerView and its components
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allNewsList = new ArrayList<>();
        filteredNewsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, filteredNewsList);
        newsRecyclerView.setAdapter(newsAdapter);

        // Initialize Featured News UI elements
        featuredNewsCard = findViewById(R.id.featuredNewsCard);
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
        navAcademic.setOnClickListener(v -> selectTab("academic"));
        navSport.setOnClickListener(v -> selectTab("sport"));
        navEvent.setOnClickListener(v -> selectTab("event"));

        // Fetch news data from Firebase when the activity starts
        fetchNewsFromFirebase();
    }

    /**
     * Called when the activity receives a new Intent. This happens if the activity is already
     * running in the current task and a new Intent is delivered to it (e.g., from NewsDetailScreen
     * with FLAG_ACTIVITY_SINGLE_TOP).
     *
     * @param intent The new intent that was started for the activity.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Set the new intent as the activity's current intent
        handleIntent(intent); // Process the new intent
    }

    /**
     * Called when the activity is becoming visible to the user.
     * We call handleIntent here to ensure that if NewsScreen is brought to the foreground
     * (e.g., from background), it processes any pending category selection from the intent.
     */
    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent()); // Process the intent when the activity resumes
    }

    /**
     * Handles the incoming intent to select the correct tab if a category is provided.
     * This method is crucial for navigating back from NewsDetailScreen to a specific tab.
     *
     * @param intent The intent that started or resumed this activity.
     */
    private void handleIntent(Intent intent) {
        // Check if the intent contains the EXTRA_SELECTED_CATEGORY from NewsDetailScreen
        if (intent != null && intent.hasExtra(NewsDetailScreen.EXTRA_SELECTED_CATEGORY)) {
            String category = intent.getStringExtra(NewsDetailScreen.EXTRA_SELECTED_CATEGORY);
            if (category != null && !category.isEmpty()) {
                selectTab(category); // Select the tab corresponding to the received category
                // Clear the extra from the intent to prevent it from being re-processed
                // if the activity is resumed again without a new intent.
                intent.removeExtra(NewsDetailScreen.EXTRA_SELECTED_CATEGORY);
            }
        } else {
            // If no specific category is passed, ensure the default or last selected tab is active.
            // This covers cases where NewsScreen is launched normally or resumed without a category intent.
            selectTab(currentNewsType);
        }
    }


    /**
     * Handles the selection of a navigation tab.
     * Updates the UI (section title, tab colors), finds and displays the featured article
     * for the selected category, and filters the general news list for the RecyclerView.
     *
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
     *
     * @param categoryType The category to find the featured article for (e.g., "academic").
     */
    private void updateFeaturedNewsCard(String categoryType) {
        NewsArticle featuredArticleForCategory = null;
        for (NewsArticle article : allNewsList) {
            // Check if the article matches the current category AND is marked as featured for that category
            if (categoryType.equalsIgnoreCase(article.getType()) && article.isFeaturedForCategory()) {
                featuredArticleForCategory = article;
                break;
            }
        }

        // Update UI based on whether a featured article was found
        if (featuredArticleForCategory != null) {
            final NewsArticle articleToDisplay = featuredArticleForCategory;

            // Set the title and date from the featured article
            featuredNewsTitle.setText(articleToDisplay.getTitle());
            featuredNewsDate.setText("Date: " + articleToDisplay.getDate());

            // Get the resource ID of the image from the drawable folder using its name
            int featuredImageResId = getResources().getIdentifier(
                    articleToDisplay.getImageName(),
                    "drawable",
                    getPackageName()
            );

            // Load the image using Glide for efficient image loading
            if (featuredImageResId != 0) {
                Glide.with(NewsScreen.this)
                        .load(featuredImageResId)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(featuredNewsImage);
            } else {
                // If the image resource is not found, display a default "no image" placeholder
                featuredNewsImage.setImageResource(R.drawable.no_image_available);
                Log.w(TAG, "Featured image for " + categoryType + " not found: " + articleToDisplay.getImageName());
            }

            // Set OnClickListener for the featured news card to open NewsDetailScreen
            featuredNewsCard.setOnClickListener(v -> {
                Intent intent = new Intent(NewsScreen.this, NewsDetailScreen.class);
                intent.putExtra(NewsDetailScreen.EXTRA_NEWS_ARTICLE, articleToDisplay);
                startActivity(intent);
            });

        } else {
            // If no featured article is found for the current category, display a default message
            featuredNewsTitle.setText("No Featured News for " + capitalizeFirstLetter(categoryType));
            featuredNewsDate.setText("");
            featuredNewsImage.setImageResource(R.drawable.no_image_available);
            featuredNewsCard.setOnClickListener(null); // Disable click listener
        }
    }


    /**
     * Filters the 'allNewsList' based on the given news type.
     * Only articles matching the type and NOT marked as 'isFeaturedForCategory' are added
     * to `filteredNewsList`, which then updates the RecyclerView.
     *
     * @param type The type of news to display (e.g., "academic").
     */
    private void filterNewsByType(String type) {
        filteredNewsList.clear();

        // Iterate through all news articles
        for (NewsArticle article : allNewsList) {
            // Add article to filtered list if its type matches the selected type
            // AND it is NOT a featured article for its category
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
     *
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
                iconAcademic.setColorFilter(SELECTED_COLOR);
                textAcademic.setTextColor(SELECTED_COLOR);
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
     *
     * @param text The input string.
     * @return The string with its first letter capitalized.
     */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
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
                allNewsList.clear(); // Clear the master list to prevent duplicate data

                // Iterate through each child node (which represents a NewsArticle)
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Deserialize the Firebase dataSnapshot into a NewsArticle object
                    NewsArticle article = postSnapshot.getValue(NewsArticle.class);
                    if (article != null) {
                        article.setId(postSnapshot.getKey()); // Set the Firebase unique key as the article ID
                        allNewsList.add(article); // Add the article to the master list
                    }
                }
                // After fetching all news, call handleIntent to process any incoming category selection
                // This ensures the UI is populated correctly based on the intent after data is available.
                handleIntent(getIntent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data fetching from Firebase
                Log.w(TAG, "Failed to load news from Firebase: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(NewsScreen.this, "Failed to load news: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}