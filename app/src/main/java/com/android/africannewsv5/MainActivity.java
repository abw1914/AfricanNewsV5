package com.android.africannewsv5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<NewsData>> {

    public static int LOADER_ID = 0;
    private NewsAdapter newsAdapter;
    private TextView mEmptyTextView;

    public static final String LOG_TOG = MainActivity.class.getSimpleName();
    public static final String API_LABEL = "&api-key=";
    public static String BASE_NEWS_SEARCH_URL = "https://content.guardianapis.com/search?q=";
    public static final String GUARDIAN_API_KEY = "9998be71-d068-4976-b2a1-c69bcc6ed458&show-tags=contributor&page-size=15";
    public String Africa = "Africa";
    public String Caribbean = "Caribbean";
    public String Brazil = "Brazil";

    public String BASE_NEWS_SEARCH = BASE_NEWS_SEARCH_URL + Africa + API_LABEL + GUARDIAN_API_KEY;


    /**
     * Append the Country name after world
     * Append the api key
     * Append the limit of articles
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsDataListView = findViewById(R.id.list);
        mEmptyTextView = findViewById(R.id.empty_textview);
        newsDataListView.setEmptyView(mEmptyTextView);

        newsAdapter = new NewsAdapter(this, 0, new ArrayList<NewsData>());

        newsDataListView.setAdapter(newsAdapter);

        newsDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsData currentNewsStory = newsAdapter.getItem(i);
                Uri newsDataUri = Uri.parse(currentNewsStory.getWebUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsDataUri);
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Bundle bundleForLoader = null;
            LoaderManager.LoaderCallbacks<ArrayList<NewsData>> callbacks = MainActivity.this;
            getSupportLoaderManager().initLoader(LOADER_ID, bundleForLoader, this).forceLoad();
        } else {
            View loadingIndicator = findViewById(R.id.loadingIndicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.connection_bad);
        }
    }

    @Override
    public Loader<ArrayList<NewsData>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TOG, "Loader OnCreateLoader method call... ");
        return new NewsLoader(MainActivity.this, BASE_NEWS_SEARCH);
    }


    @SuppressLint("ResourceType")
    @Override
    public void onLoadFinished(Loader<ArrayList<NewsData>> loader, ArrayList<NewsData> newsDataArrayList) {
        Log.e(LOG_TOG, "onLoadFinished method call... ");
        View loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyTextView = findViewById(R.string.no_data);
        if (newsDataArrayList != null && !newsDataArrayList.isEmpty()) {
            updateUi(newsDataArrayList);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<NewsData>> loader) {
        Log.e(LOG_TOG, "onLoaderReset method call... ");
        newsAdapter.notifyDataSetChanged();
    }


    public void updateUi(final ArrayList<NewsData> newsDataArrayList) {
        ListView newsListView = findViewById(R.id.list);
        final NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this, 0, newsDataArrayList);
        newsListView.setAdapter(newsAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsData currentNewsStory = newsAdapter.getItem(i);
                Uri newsDataUri = Uri.parse(currentNewsStory.getWebUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsDataUri);
                startActivity(websiteIntent);
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            View loadingIndicator = findViewById(R.id.loadingIndicator);
            loadingIndicator.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.connection_ok, Toast.LENGTH_SHORT).show();
            loadingIndicator.setVisibility(View.GONE);
        } else {
            mEmptyTextView = findViewById(R.id.empty_textview);
            mEmptyTextView.setText("No Data");
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private void reload() {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.African_selected:
                BASE_NEWS_SEARCH = BASE_NEWS_SEARCH_URL + Africa + API_LABEL + GUARDIAN_API_KEY;
                new NewsLoader(this, BASE_NEWS_SEARCH);
                reload();
                return true;
            case R.id.Caribbean_selected:
                BASE_NEWS_SEARCH = BASE_NEWS_SEARCH_URL + Caribbean + API_LABEL + GUARDIAN_API_KEY;
                new NewsLoader(this, BASE_NEWS_SEARCH);
                isNetworkConnected();
                reload();
                return true;
            case R.id.Brazil_selected:
                BASE_NEWS_SEARCH = BASE_NEWS_SEARCH_URL + Brazil + API_LABEL + GUARDIAN_API_KEY;
                new NewsLoader(this, BASE_NEWS_SEARCH);
                isNetworkConnected();
                reload();
                return true;
        }
        return false;


    }
}

