package com.example.codecastnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * NewsScreen Activity: Displays a list of news articles categorized by type (Academic, Sport, Event).
 * Fetches news data from Firebase Realtime Database.
 * Features a dynamic "featured news" card and a RecyclerView for general news.
 * Includes a bottom navigation bar for switching categories and handling navigation from NewsDetailScreen.
 */
public class NewsScreen extends AppCompatActivity {

    // TAG for logging messages - USE THIS TO FILTER LOGCAT
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
    private ImageView profileIcon;
    private ImageView featuredNewsShare;
    private ImageView searchIcon;
    private EditText searchInput; // New: Search input field
    private TextView dateTextView; // Existing: Used for current date display
    private ImageView closeSearchIcon; // New: Icon to close search

    // Bottom Navigation Tab Layouts and their child views
    private LinearLayout navAcademic, navSport, navEvent;
    private ImageView iconAcademic, iconSport, iconEvent;
    private TextView textAcademic, textSport, textEvent;

    // State Variable: Keeps track of the currently selected news category
    private String currentNewsType = "academic"; // Default selected news category is "academic"
    private boolean isSearchMode = false; // New: State to track if search is active

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
        profileIcon = findViewById(R.id.profileIcon);
        featuredNewsShare = findViewById(R.id.featuredNewsShare);

        // Initialize Search and Date/Profile Bar elements
        searchIcon = findViewById(R.id.searchIcon);
        searchInput = findViewById(R.id.searchInput); // Initialize the EditText
        dateTextView = findViewById(R.id.dateTextView); // Initialize dateTextView
        closeSearchIcon = findViewById(R.id.closeSearchIcon); // Initialize closeSearchIcon

        // Set current date on dateTextView
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        // Set OnClickListener for the profileIcon to open UserProfile
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NewsScreen.this, SettingScreen.class);
            startActivity(intent);
        });

        // Search Icon Click Listener (toggles search mode)
        searchIcon.setOnClickListener(v -> toggleSearchMode(true));

        // Close Search Icon Click Listener (exits search mode)
        closeSearchIcon.setOnClickListener(v -> toggleSearchMode(false));

        // TextWatcher for search input: Filters news as user types
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the news list as text changes
                filterNewsBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        // Handle keyboard search action (e.g., when user presses "Enter" on keyboard)
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterNewsBySearch(v.getText().toString());
                hideKeyboard(); // Hide keyboard after search
                return true;
            }
            return false;
        });


        // Set click listener for share icon
        featuredNewsShare.setOnClickListener(v -> {
            // Find the featured article for the current category
            NewsArticle featuredArticle = null;
            for (NewsArticle article : allNewsList) {
                if (currentNewsType.equalsIgnoreCase(article.getType()) && article.isFeaturedForCategory()) {
                    featuredArticle = article;
                    break;
                }
            }
            if (featuredArticle != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, featuredArticle.getTitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, featuredArticle.getTitle() + "\n\n" + featuredArticle.getDescription());
                try {
                    startActivity(Intent.createChooser(shareIntent, "Share Featured News"));
                } catch (Exception e) {
                    Toast.makeText(this, "Error sharing article", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Share error: " + e.getMessage());
                }
            } else {
                Toast.makeText(this, "No featured article to share", Toast.LENGTH_SHORT).show();
            }
        });

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
     * Toggles the search mode, showing/hiding search input and related UI elements.
     * @param enableSearch true to enable search mode, false to disable.
     */
    private void toggleSearchMode(boolean enableSearch) {
        isSearchMode = enableSearch;
        if (enableSearch) {
            // Enable search mode
            dateTextView.setVisibility(View.GONE);      // Hide date
            profileIcon.setVisibility(View.GONE);      // Hide profile icon
            searchInput.setVisibility(View.VISIBLE);   // Show search input
            closeSearchIcon.setVisibility(View.VISIBLE); // Show close search icon

            // Hide section title and featured card in search mode
            sectionTitleTextView.setVisibility(View.GONE);
            featuredNewsCard.setVisibility(View.GONE);
            // Hide bottom navigation when search is active
            findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);


            searchInput.requestFocus(); // Set focus to search input
            showKeyboard(); // Show keyboard automatically

            // Initially show all articles or clear previous search results
            filterNewsBySearch("");
        } else {
            // Disable search mode
            dateTextView.setVisibility(View.VISIBLE);    // Show date
            profileIcon.setVisibility(View.VISIBLE);     // Show profile icon
            searchInput.setVisibility(View.GONE);      // Hide search input
            closeSearchIcon.setVisibility(View.GONE);  // Hide close search icon
            searchInput.setText(""); // Clear any text in search input

            hideKeyboard(); // Hide keyboard

            // Restore original view (section title, featured card, filtered news)
            sectionTitleTextView.setVisibility(View.VISIBLE);
            featuredNewsCard.setVisibility(View.VISIBLE);
            findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE); // Show bottom navigation again
            selectTab(currentNewsType); // Re-apply the current category filter
        }
    }

    /**
     * Filters the news list based on the search query.
     * This method searches across all news articles for the query, regardless of category.
     * @param query The search string.
     */
    private void filterNewsBySearch(String query) {
        filteredNewsList.clear();
        if (query.isEmpty()) {
            // If the query is empty in search mode, display all non-featured articles
            // This is useful if the user clears the search but remains in search mode.
            for (NewsArticle article : allNewsList) {
                // In an empty search, we can show all non-featured items or just items relevant to current category.
                // For simplicity, when search is active and empty, let's show all non-featured articles from all categories.
                // If you want it to show non-featured of only current category, adjust this logic.
                if (!article.isFeaturedForCategory()) { // Show all non-featured if search is empty
                    filteredNewsList.add(article);
                }
            }
        } else {
            // Filter by query (case-insensitive) across title and description
            String lowerCaseQuery = query.toLowerCase();
            for (NewsArticle article : allNewsList) {
                if (article.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        article.getDescription().toLowerCase().contains(lowerCaseQuery) ||
                        article.getType().toLowerCase().contains(lowerCaseQuery) // Optional: search by category name too
                ) {
                    filteredNewsList.add(article);
                }
            }
        }
        Collections.shuffle(filteredNewsList); // Optional: Shuffle search results for varied display
        newsAdapter.updateNewsList(filteredNewsList); // Update RecyclerView
        Log.d(TAG, "filterNewsBySearch: Filtered " + filteredNewsList.size() + " articles for query: '" + query + "'");
    }

    /**
     * Shows the soft keyboard.
     */
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Hides the soft keyboard.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Set the new intent as the activity's current intent
        handleIntent(intent); // Process the new intent
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Handle intent on resume to ensure correct tab/state, especially after returning from NewsDetailScreen
        handleIntent(getIntent());

        // If the activity resumes and we are in search mode, ensure the keyboard is still visible if input is focused.
        // Or you might choose to exit search mode on resume if that's preferred.
        if (isSearchMode && searchInput.isFocused()) {
            showKeyboard();
        } else if (isSearchMode && !searchInput.isFocused()){
            // If in search mode but somehow focus is lost, re-focus and show keyboard
            searchInput.requestFocus();
            showKeyboard();
        }
    }

    /**
     * Overrides the default back button behavior.
     * If in search mode, pressing back will exit search mode. Otherwise, it behaves normally.
     */
    @Override
    public void onBackPressed() {
        if (isSearchMode) {
            toggleSearchMode(false); // Exit search mode on back press
        } else {
            super.onBackPressed(); // Default back button behavior
        }
    }

    /**
     * Handles the incoming intent to select the correct tab if a category is provided.
     * This method is crucial for navigating back from NewsDetailScreen to a specific tab.
     *
     * @param intent The intent that started or resumed this activity.
     */
    private void handleIntent(Intent intent) {
        // Only process category selection if not in search mode
        if (!isSearchMode) {
            if (intent != null && intent.hasExtra(NewsDetailScreen.EXTRA_SELECTED_CATEGORY)) {
                String category = intent.getStringExtra(NewsDetailScreen.EXTRA_SELECTED_CATEGORY);
                if (category != null && !category.isEmpty()) {
                    Log.d(TAG, "handleIntent: Received category from intent: " + category);
                    selectTab(category); // Select the tab corresponding to the received category
                    // Clear the extra from the intent to prevent it from being re-processed
                    // if the activity is resumed again without a new intent.
                    intent.removeExtra(NewsDetailScreen.EXTRA_SELECTED_CATEGORY);
                }
            } else {
                Log.d(TAG, "handleIntent: No specific category in intent. Selecting default/current: " + currentNewsType);
                // If no specific category is passed, ensure the default or last selected tab is active.
                selectTab(currentNewsType);
            }
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
        if (isSearchMode) { // Prevent tab selection if in search mode
            return;
        }
        currentNewsType = type; // Update the state variable to the newly selected type
        sectionTitleTextView.setText(capitalizeFirstLetter(type)); // Set the section title (e.g., "Academic")
        setSelectedTabColor(type); // Update the colors of the bottom navigation tabs

        // Find and display the featured news article relevant to the selected category
        updateFeaturedNewsCard(type);

        // Filter the main news list and update the RecyclerView with articles
        // that match the selected type and are NOT featured.
        filterNewsByType(type);
        Log.d(TAG, "selectTab: Selected category: " + type);
    }

    /**
     * Finds the single article marked as featured for the given category from `allNewsList`
     * and updates the `featuredNewsCard` UI elements (image, title, date).
     * If no featured article is found for the category, displays a default "No Featured News" message.
     *
     * @param categoryType The category to find the featured article for (e.g., "academic").
     */
    private void updateFeaturedNewsCard(String categoryType) {
        if (isSearchMode) { // Don't update featured card if in search mode
            return;
        }

        NewsArticle featuredArticleForCategory = null;
        for (NewsArticle article : allNewsList) {
            // Check if the article matches the current category AND is marked as featured for that category
            if (categoryType.equalsIgnoreCase(article.getType()) && article.isFeaturedForCategory()) {
                featuredArticleForCategory = article;
                Log.d(TAG, "FEATURED_ARTICLE_SELECTION: Category=" + categoryType +
                        ", Selected Article ID=" + article.getId() +
                        ", Title='" + article.getTitle() + "'" +
                        ", Type=" + article.getType() +
                        ", ImageName (from model)=" + article.getImageName());
                break; // Found the featured article, no need to continue looping
            }
        }

        // Update UI based on whether a featured article was found
        if (featuredArticleForCategory != null) {
            final NewsArticle articleToDisplay = featuredArticleForCategory;

            // Set the title and date from the featured article
            featuredNewsTitle.setText(articleToDisplay.getTitle());
            featuredNewsDate.setText("Date: " + articleToDisplay.getDate());

            // Get the resource ID of the image from the drawable folder using its name
            String imageName = articleToDisplay.getImageName(); // Get the image name from the article object
            int featuredImageResId = getResources().getIdentifier(
                    imageName, // Use the imageName from the article object
                    "drawable",
                    getPackageName()
            );

            Log.d(TAG, "GLIDE_LOADING_DEBUG: Attempting to load image '" + imageName +
                    "' for featured card of category '" + categoryType + "'");

            // Load the image using Glide for efficient image loading
            if (featuredImageResId != 0) {
                Glide.with(NewsScreen.this)
                        .load(featuredImageResId)
                        .placeholder(R.drawable.placeholder_image) // Ensure you have this drawable
                        .error(R.drawable.error_image) // Ensure you have this drawable
                        .into(featuredNewsImage);
                Log.d(TAG, "GLIDE_LOADING_DEBUG: Resource ID found for '" + imageName + "': " + featuredImageResId);
            } else {
                // If the image resource is not found, display a default "no image" placeholder
                featuredNewsImage.setImageResource(R.drawable.no_image_available); // Ensure you have this drawable
                Log.e(TAG, "GLIDE_LOADING_ERROR: Drawable not found for '" + imageName +
                        "' in category '" + categoryType + "'. Showing default image.");
            }

            // Set OnClickListener for the featured news card to open NewsDetailScreen
            featuredNewsCard.setOnClickListener(v -> {
                Intent intent = new Intent(NewsScreen.this, NewsDetailScreen.class);
                intent.putExtra(NewsDetailScreen.EXTRA_NEWS_ARTICLE, articleToDisplay);
                startActivity(intent);
                Log.d(TAG, "Featured card clicked: " + articleToDisplay.getTitle());
            });

        } else {
            // If no featured article is found for the current category, display a default message
            String noFeaturedMessage = "No Featured News for " + capitalizeFirstLetter(categoryType);
            featuredNewsTitle.setText(noFeaturedMessage);
            featuredNewsDate.setText("");
            featuredNewsImage.setImageResource(R.drawable.no_image_available);
            featuredNewsCard.setOnClickListener(null); // Disable click listener
            Log.d(TAG, "updateFeaturedNewsCard: No featured article found for category: " + categoryType);
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
        if (isSearchMode) { // Don't filter by type if in search mode; search filters instead
            return;
        }
        filteredNewsList.clear();
        int nonFeaturedCount = 0;
        // Iterate through all news articles
        for (NewsArticle article : allNewsList) {
            // Add article to filtered list if its type matches the selected type
            // AND it is NOT a featured article for its category
            if (type.equalsIgnoreCase(article.getType()) && !article.isFeaturedForCategory()) {
                filteredNewsList.add(article);
                nonFeaturedCount++;
            }
        }
        Collections.shuffle(filteredNewsList); // Optional: Shuffle the filtered list for varied display
        newsAdapter.updateNewsList(filteredNewsList); // Update the RecyclerView adapter with the new list
        Log.d(TAG, "filterNewsByType: Filtered " + nonFeaturedCount + " non-featured articles for category: " + type);
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
        Log.d(TAG, "setSelectedTabColor: Tab '" + selectedType + "' highlighted.");
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
                Log.d(TAG, "onDataChange: Fetching news from Firebase. DataSnapshot children count: " + dataSnapshot.getChildrenCount());

                // Iterate through each child node (which represents a NewsArticle)
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Deserialize the Firebase dataSnapshot into a NewsArticle object
                    NewsArticle article = postSnapshot.getValue(NewsArticle.class);
                    if (article != null) {
                        article.setId(postSnapshot.getKey()); // Set the Firebase unique key as the article ID
                        allNewsList.add(article); // Add the article to the master list
                        Log.d(TAG, "FIREBASE_DATA_READ: ID=" + article.getId() +
                                ", Title='" + article.getTitle() + "'" +
                                ", Type=" + article.getType() +
                                ", Featured=" + article.isFeaturedForCategory() +
                                ", ImageName=" + article.getImageName());
                    } else {
                        Log.w(TAG, "FIREBASE_DATA_READ: Could not parse article from snapshot: " + postSnapshot.getKey());
                    }
                }
                Log.d(TAG, "onDataChange: Total articles loaded: " + allNewsList.size());

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