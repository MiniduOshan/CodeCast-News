package com.example.codecastnews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View; // Import View

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the backButton by its ID
        ImageView backButton = findViewById(R.id.backButton);

        // Set an OnClickListener for the backButton
        backButton.setOnClickListener(v -> {
            // Call finish() to close the current activity and go back to the previous one
            finish();
        });

        // Find the TextViews and ImageView that are part of the profile section
        TextView userNameTextView = findViewById(R.id.userName);
        TextView viewProfileTextView = findViewById(R.id.viewProfile);
        ImageView arrowProfileImageView = findViewById(R.id.arrowProfile);

        // Create an OnClickListener for navigating to UserProfile
        View.OnClickListener profileClickListener = v -> {
            Intent intent = new Intent(SettingScreen.this, UserProfile.class);
            startActivity(intent);
        };

        // Set the click listener on each element of the profile section
        userNameTextView.setOnClickListener(profileClickListener);
        viewProfileTextView.setOnClickListener(profileClickListener);
        arrowProfileImageView.setOnClickListener(profileClickListener);

        // --- NEW CODE FOR ABOUT US SECTION ---

        // Assuming your XML has IDs for these specific elements within the About Us section:
        // You'll need to ensure your XML layout for the "About US" part has IDs for these views.
        // For example, if your TextView "About US" had an ID like @id/aboutUsText,
        // and your Images had IDs like @id/cupIcon and @id/navigateNextIcon.
        // If they don't have IDs, you'll need to add them in your XML for this to work.

        // For demonstration, let's assume you've added IDs in your XML for these elements:
        // For example:
        // <ImageView android:id="@+id/aboutUsCupIcon" ... />
        // <TextView android:id="@+id/aboutUsTextView" ... />
        // <ImageView android:id="@+id/aboutUsNavigateNextIcon" ... />

        // You will need to find the actual IDs you have or add new ones in your XML
        // if they are not already present. For now, I'll use placeholders.
        // If these elements are not directly accessible via an ID, this approach
        // might not be feasible without some XML modification.

        // Since you specified "don't change the XML", this implies these elements
        // might already have IDs or are in a predictable order.
        // However, based on the snippet you provided, they don't have specific IDs.
        // To make them clickable without changing the XML to add a wrapping layout ID,
        // you would ideally need an ID on at least one of these components.

        // If your XML *does not* have IDs for these elements, you won't be able
        // to directly reference them using findViewById.

        // Let's assume you have added IDs like this (you would need to add them if not present):
        // For this to work, you MUST ensure that the `ImageView` with `@drawable/cup`,
        // the `TextView` with "About US", and the `ImageView` with `@drawable/navigate_next`
        // **each have a unique `android:id` attribute** in your `activity_setting_screen.xml`.

        // Example IDs if you add them:
        // ImageView cupIcon = findViewById(R.id.cupIcon); // Assuming you add android:id="@+id/cupIcon"
        // TextView aboutUsText = findViewById(R.id.aboutUsText); // Assuming you add android:id="@+id/aboutUsText"
        // ImageView navigateNextIcon = findViewById(R.id.navigateNextIcon); // Assuming you add android:id="@+id/navigateNextIcon"

        // For the sake of this solution without changing XML, we have a challenge:
        // The XML snippet you provided *does not* have IDs for the `ImageView` and `TextView`
        // in the "About Us" part. Without IDs, `findViewById` cannot locate them.
        // To make them clickable *without changing the XML*, you would typically
        // need to make the parent `ViewGroup` (if it has an ID) clickable,
        // but your request implies not changing the XML, which includes not adding
        // an ID to a parent `LinearLayout` or similar.

        // **Therefore, the only way to make these specific elements clickable without modifying their XML
        // to add IDs is if they were already referenced in your existing XML structure
        // in a way that allows `findViewById` to pick them up, which is unlikely given your snippet.**

        // If your XML looks exactly like the snippet provided and has no IDs for these elements,
        // you *cannot* directly find them using `findViewById` and set a listener.
        // The most robust way to make this section clickable is to wrap it in a LinearLayout
        // and give that LinearLayout an ID, as shown in my previous answer.

        // If you absolutely cannot change the XML, you have a problem, because there's
        // no way to get a reference to those specific `ImageView` and `TextView` objects
        // in your Java code to set click listeners on them.

        // Let's assume, for a moment, that your actual full XML file *does* have IDs for them,
        // even if not shown in your snippet. If it does, you would do this:

        // Example with placeholder IDs (you MUST use the actual IDs from your XML):
        // View cupImageView = findViewById(R.id.your_cup_image_view_id); // Replace with actual ID
        // View aboutUsTextView = findViewById(R.id.your_about_us_text_view_id); // Replace with actual ID
        // View navigateNextImageView = findViewById(R.id.your_navigate_next_image_view_id); // Replace with actual ID

        // If your XML does NOT have IDs, you HAVE to add them for this to work.
        // For example:
        // <ImageView android:id="@+id/aboutUsCup" ... />
        // <TextView android:id="@+id/aboutUsText" ... />
        // <ImageView android:id="@+id/aboutUsArrow" ... />


        // *** Critical: You MUST add these IDs in your activity_setting_screen.xml for this to work. ***
        ImageView aboutUsCup = findViewById(R.id.aboutUsCup);
        TextView aboutUsText = findViewById(R.id.aboutUsText);
        ImageView aboutUsArrow = findViewById(R.id.aboutUsArrow);


        View.OnClickListener aboutUsClickListener = v -> {
            Intent intent = new Intent(SettingScreen.this, DevInfoScreen.class);
            startActivity(intent);
        };

        // Set the click listener on each of the "About US" elements
        aboutUsCup.setOnClickListener(aboutUsClickListener);
        aboutUsText.setOnClickListener(aboutUsClickListener);
        aboutUsArrow.setOnClickListener(aboutUsClickListener);
        // --- END NEW CODE ---
    }
}