package com.example.codecastnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.codecastnews.models.NewsArticle;

public class NewsDetailScreen extends AppCompatActivity {

    private static final String TAG = "NewsDetailScreen";
    // Key for passing the NewsArticle object via Intent
    public static final String EXTRA_NEWS_ARTICLE = "extra_news_article";
    // Define a new extra key for the selected category when returning to NewsScreen
    public static final String EXTRA_SELECTED_CATEGORY = "extra_selected_category";


    private ImageView detailNewsImage;
    private TextView detailNewsTitle;
    private TextView detailNewsDate;
    private TextView detailNewsContent;

    // Declare your LinearLayouts for the bottom navigation tabs
    private LinearLayout navAcademic, navSport, navEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_detail); // Make sure this points to your XML

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components from your activity_news_detail.xml
        detailNewsImage = findViewById(R.id.detailNewsImage);
        detailNewsTitle = findViewById(R.id.detailNewsTitle);
        detailNewsDate = findViewById(R.id.detailNewsDate);
        detailNewsContent = findViewById(R.id.detailNewsContent);

        // Initialize bottom navigation tabs by finding their IDs in activity_news_detail.xml
        navAcademic = findViewById(R.id.navAcademic);
        navSport = findViewById(R.id.navSport);
        navEvent = findViewById(R.id.navEvent);

        // Set click listeners for the bottom navigation tabs
        // When a tab is clicked, call navigateToNewsScreen with the corresponding category
        navAcademic.setOnClickListener(v -> navigateToNewsScreen("academic"));
        navSport.setOnClickListener(v -> navigateToNewsScreen("sport"));
        navEvent.setOnClickListener(v -> navigateToNewsScreen("event"));

        // Get the NewsArticle object from the Intent that started this activity
        NewsArticle article = (NewsArticle) getIntent().getSerializableExtra(EXTRA_NEWS_ARTICLE);

        if (article != null) {
            // Populate the UI with the article's data
            detailNewsTitle.setText(article.getTitle());
            detailNewsDate.setText("Date: " + article.getDate()); // Assuming date is a simple string
            detailNewsContent.setText(article.getDescription()); // Use getDescription() for the main content

            // Load the image using Glide. Ensure your image names match drawable resource names.
            int imageResId = getResources().getIdentifier(
                    article.getImageName(), // This should be the *resource name* (e.g., "media", "spectral_20")
                    "drawable",
                    getPackageName()
            );

            if (imageResId != 0) {
                Glide.with(this)
                        .load(imageResId)
                        .placeholder(R.drawable.placeholder_image) // Make sure you have a placeholder_image drawable
                        .error(R.drawable.error_image) // Make sure you have an error_image drawable
                        .into(detailNewsImage);
            } else {
                // Fallback if image resource is not found (e.g., wrong name or not in drawable)
                detailNewsImage.setImageResource(R.drawable.no_image_available); // A generic "image not found" icon
                Log.w(TAG, "Image resource not found for: " + article.getImageName() + ". Using fallback image.");
            }
        } else {
            // Handle the case where no NewsArticle data was passed (e.g., launched incorrectly)
            Log.e(TAG, "No NewsArticle data received! Cannot display details.");
            Toast.makeText(this, "Error: News article data could not be loaded.", Toast.LENGTH_LONG).show();
            // Set default/error text and image
            detailNewsTitle.setText("Error Loading News");
            detailNewsDate.setText("");
            detailNewsContent.setText("The news article could not be loaded. Please return to the previous screen.");
            detailNewsImage.setImageResource(R.drawable.error_image);
        }
    }

    /**
     * Navigates back to NewsScreen, passing the selected category as an extra.
     * This method ensures that NewsScreen is brought to the top of the stack
     * and the correct tab is selected upon return.
     *
     * @param category The selected news category ("academic", "sport", "event").
     */
    private void navigateToNewsScreen(String category) {
        Intent intent = new Intent(NewsDetailScreen.this, NewsScreen.class);
        intent.putExtra(EXTRA_SELECTED_CATEGORY, category);
        // Use FLAG_ACTIVITY_CLEAR_TOP and FLAG_ACTIVITY_SINGLE_TOP to bring NewsScreen
        // to the top of the stack and deliver the new intent to its onNewIntent method.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Finish NewsDetailScreen so it's removed from the back stack
    }
}