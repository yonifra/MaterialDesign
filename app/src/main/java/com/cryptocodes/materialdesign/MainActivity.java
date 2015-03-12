package com.cryptocodes.materialdesign;

import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.melnykov.fab.FloatingActionButton;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

// Twitter
import android.content.Intent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class MainActivity extends ActionBarActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "4zCGSRgYnCXoHOMKpbhtFfER2";
    private static final String TWITTER_SECRET = "Z1ull1oTsBZBUPgZ2bRnqlH85vOuUCHmPa1WAyeTncQ3570qRs";
    private static final String TAG = "MATERIAL_DESIGN";
    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TwitterLoginButton loginButton;

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                loginButton.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);

        /*Downloading data from below url*/
        final String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";
        new AsyncHttpTask().execute(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;

            try {
                /* forming th java.net.URL object */
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode ==  200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    parseResult(response.toString());
                    result = 1; // Successful
                }else{
                    result = 0; //"Failed to fetch data!";
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            setProgressBarIndeterminateVisibility(false);
            /* Download complete. Lets update UI */
            if (result == 1) {
                adapter = new MyRecyclerAdapter(MainActivity.this, feedItemList);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getBaseContext(), "Failed to fetch data", Toast.LENGTH_LONG);
                Log.e(TAG, "Failed to fetch data!");
            }
        }
    }

    private void refreshItems() {
        // Load items
        // ...
        Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();

        // Load complete
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        Toast.makeText(getApplicationContext(), "Refresh Complete!", Toast.LENGTH_SHORT).show();

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");

            /*Initialize array if null*/
            if (null == feedItemList) {
                feedItemList = new ArrayList<FeedItem>();
            }

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                FeedItem item = new FeedItem();
                item.setHeader(post.optString("title"));
                item.setTitle(post.optString("excerpt"));
                item.setThumbnail(post.optString("thumbnail"));
                feedItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
