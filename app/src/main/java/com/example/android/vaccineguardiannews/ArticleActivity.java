package com.example.android.vaccineguardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    // URL for vaccine articles from the Guardian
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    private static final String API_KEY = "d5647883-150c-467f-9224-7d178974148a";
    private static final String QUERY_PAGE_SIZE = "page-size";
    private static final String QUERY_API_KEY = "api-key";
    private static final String QUERY_KEYWORD = "q";
    private static final String QUERY_VACCINE = "vaccine";
    private static final String QUERY_SHOW_TAGS = "show-tags";
    private static final String QUERY_CONTRIBUTOR = "contributor";
    private static final String QUERY_ORDER_BY = "orderby";
    public static final String LOG_TAG = ArticleActivity.class.getName();
    private static final int ARTICLE_LOADER_ID = 1;
    private ArticleAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaccine_activity);

        ListView articleListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        //create a new adapter and let it is populated in the user interface
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(mAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        //set an item click listener to sends and intent to the web page on the selected article
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Article currentArticle = mAdapter.getItem(position);
                Uri articleUri = Uri.parse(currentArticle.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            //initialize the loader
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            //otherwise hide loading indicator and display error
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
        if (key.equals(getString(R.string.settings_pagenumb_key)) || key.equals(getString(R.string.settings_orderby_key))){
            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(ARTICLE_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String pageNumb = sharedPrefs.getString(
                getString(R.string.settings_pagenumb_key),
                getString(R.string.settings_pagenumb_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_orderby_key),
                getString(R.string.settings_orderby_default));

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(QUERY_PAGE_SIZE, pageNumb);
        uriBuilder.appendQueryParameter(QUERY_KEYWORD, QUERY_VACCINE);
        uriBuilder.appendQueryParameter(QUERY_SHOW_TAGS, QUERY_CONTRIBUTOR);
        uriBuilder.appendQueryParameter(QUERY_API_KEY, API_KEY);
        uriBuilder.appendQueryParameter(QUERY_ORDER_BY, orderBy);

        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_data);
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}