package com.example.codecastnews;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.codecastnews.models.NewsArticle; // Ensure this import is correct

public class NewsDetailScreen extends AppCompatActivity {

    private static final String TAG = "NewsDetailScreen";
    // Key for passing the NewsArticle object via Intent
    public static final String EXTRA_NEWS_ARTICLE = "extra_news_article";

    private ImageView detailNewsImage;
    private TextView detailNewsTitle;
    private TextView detailNewsDate;
    private TextView detailNewsContent; // Renamed from detailNewsDescription to match XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components from your activity_news_detail.xml
        detailNewsImage = findViewById(R.id.detailNewsImage);
        detailNewsTitle = findViewById(R.id.detailNewsTitle);
        detailNewsDate = findViewById(R.id.detailNewsDate);
        detailNewsContent = findViewById(R.id.detailNewsContent); // Matches the ID in your XML

        // Get the NewsArticle object from the Intent
        NewsArticle article = (NewsArticle) getIntent().getSerializableExtra(EXTRA_NEWS_ARTICLE);

        if (article != null) {
            // Populate the UI with the article's data
            detailNewsTitle.setText(article.getTitle());
            detailNewsDate.setText("Date: " + article.getDate()); // Assuming date is a simple string
            detailNewsContent.setText(article.getDescription()); // Use getDescription() for the main content

            // Load the image using Glide
            // Your image names (e.g., "media", "Spectral (20).jpg") need to be valid drawable resource names.
            // "Spectral (20).jpg" must be renamed to something like "spectral_20" and placed in `res/drawable`.
            // "media.png" must be "media" and placed in `res/drawable`.
            int imageResId = getResources().getIdentifier(
                    article.getImageName(), // This should be the *resource name* (e.g., "media", "spectral_20")
                    "drawable",
                    getPackageName()
            );

            if (imageResId != 0) {
                Glide.with(this)
                        .load(imageResId)
                        .placeholder(R.drawable.placeholder_image) // Make sure you have a drawable named placeholder_image.xml/png/jpg
                        .error(R.drawable.error_image) // Make sure you have a drawable named error_image.xml/png/jpg
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
            // Set default/error text
            detailNewsTitle.setText("Error Loading News");
            detailNewsDate.setText("");
            detailNewsContent.setText("The news article could not be loaded. Please return to the previous screen.");
            detailNewsImage.setImageResource(R.drawable.error_image); // Show an error image
        }
    }
}