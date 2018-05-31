package com.example.android.vaccineguardiannews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    private static final String NEW_LINE = "\n";
    private static final int ZERO = 0;
    private static final int MAX_LENGTH = 11;

    /**
     * Constructs a new {@link ArticleAdapter}.
     *
     * @param context  of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */
    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    /**
     * Returns a list item view that displays information about the article at the given position
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.vaccine_list_item, parent, false);
        }

        // Find the article at the given position in the list of articles
        Article currentArticle = getItem(position);

        // Get the author name
        String authorName = currentArticle.getAuthor();
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        // If in the webpage there is not the author, write author not found, otherwise
        // display it in its TextView
        if (authorName == null) {
            authorView.setText(R.string.Author_not_found);
        } else {
            authorView.setText(authorName);
        }

        // Find the Section TextView in the list_item.xml and set it on its TextView
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        sectionView.setText(currentArticle.getSection());

        // Find the Title TextView in the list_item.xml and set it on its TextView
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentArticle.getTitle());


        // Find the TextView with view ID date
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // Format the date string
        String formattedTime = formatData(currentArticle.getTime());
        // Display the date of the current article in that TextView
        timeView.setText(formattedTime);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    //Split Data in two strings (and two lines).
    private String formatData(String inputDate) {
        // get first 11 chars
        String date = inputDate.substring(ZERO, MAX_LENGTH);
        // get all remained chars
        String time = inputDate.substring(MAX_LENGTH);
        return date + NEW_LINE + time;
    }
}