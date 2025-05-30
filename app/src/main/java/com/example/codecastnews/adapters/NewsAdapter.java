// File: app/src/main/java/com/example/codecastnews/adapters/NewsAdapter.java
package com.example.codecastnews.adapters;

import android.content.Context; // Import Context for getIdentifier
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.codecastnews.R;
import com.example.codecastnews.models.NewsArticle;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsArticle> newsList;
    private Context context; // Store context to access resources

    public NewsAdapter(Context context, List<NewsArticle> newsList) {
        this.context = context; // Initialize context
        this.newsList = newsList;
    }

    public void updateNewsList(List<NewsArticle> newList) {
        this.newsList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_small_card, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsArticle article = newsList.get(position);

        holder.newsTitle.setText(article.getTitle());
        holder.newsTimeAgo.setText(article.getTimeAgo());

        // --- Load image from local drawable using its string name ---
        if (article.getImageName() != null && !article.getImageName().isEmpty()) {
            // Get the resource ID from the drawable's string name
            int imageResId = context.getResources().getIdentifier(
                    article.getImageName(), // The string name of the drawable (e.g., "media", "content1")
                    "drawable",          // The type of resource (always "drawable" for images)
                    context.getPackageName() // The package name of your application
            );

            if (imageResId != 0) { // Check if a valid resource ID was found
                Glide.with(context)
                        .load(imageResId) // Load using the int resource ID
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(holder.newsImage);
            } else {
                // If image name is invalid or not found, show a "no image" placeholder
                holder.newsImage.setImageResource(R.drawable.no_image_available);
            }
        } else {
            // If imageName is null or empty in the data, show a "no image" placeholder
            holder.newsImage.setImageResource(R.drawable.no_image_available);
        }

        holder.itemView.setOnClickListener(v -> {
            // Handle item click if needed
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView newsTimeAgo;
        ImageView newsImage;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsTimeAgo = itemView.findViewById(R.id.newsTimeAgo);
            newsImage = itemView.findViewById(R.id.newsImage);
        }
    }
}