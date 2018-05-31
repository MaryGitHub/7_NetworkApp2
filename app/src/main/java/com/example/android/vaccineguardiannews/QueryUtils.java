package com.example.android.vaccineguardiannews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// Helper methods related to requesting and receiving articles from The Guardian
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String PROBLEM_HTTP_REQ = "Problem making the HTTP request";
    private static final String PROBLEM_URL = "Problem building the URL";
    private static final String ERR_RESP_CODE = "Error response code: ";
    private static final String PROBLEM_JSON_RES = "Problem retrieving the article JSON results.";
    private static final String PROBLEM_JSON_PARSE = "Problem parsing the article JSON results.";
    public static final int ZERO = 0;

    private QueryUtils() {
    }

    /**
     * Return a list of {@link Article} objects from the query to Guardian Journal
     */
    public static List<Article> fetchArticleData(String requestUrl) {

        // Create url object
        URL url = createUrl(requestUrl);

        // Request HTTP and recerive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, PROBLEM_HTTP_REQ, e);
        }

        List<Article> articles = extractFeatureFromJson(jsonResponse);
        // Return the list of articles
        return articles;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, PROBLEM_URL, e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, ERR_RESP_CODE + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, PROBLEM_JSON_RES, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Article> extractFeatureFromJson(String articleJSON) {
        List<Article> articles = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            // For each article, create an {@link article} object
            for (int i = ZERO; i < results.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentArticle = results.getJSONObject(i);

                String section = currentArticle.getString("sectionName");
                String title = currentArticle.getString("webTitle");
                String author = currentArticle.getJSONArray("tags").getJSONObject(ZERO).getString("webTitle");
                String date = currentArticle.getString("webPublicationDate");
                String url = currentArticle.getString("webUrl");

                // Create a new {@link article} object
                Article article = new Article(section, author, title, date, url);

                // Add the new {@link article} to the list of articles.
                articles.add(article);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", PROBLEM_JSON_PARSE, e);
            return null;
        }

        return articles;
    }
}