package com.example.ajreader;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView newsList;
    private View loadingView;
    private View errorView;
    private TextView errorText;
    private Button retryButton;

    private List<NewsItem> items = new ArrayList<NewsItem>();
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsList = (ListView) findViewById(R.id.news_list);
        loadingView = findViewById(R.id.loading_view);
        errorView = findViewById(R.id.error_view);
        errorText = (TextView) findViewById(R.id.error_text);
        retryButton = (Button) findViewById(R.id.retry_button);

        adapter = new NewsAdapter(this, items);
        newsList.setAdapter(adapter);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem item = items.get(position);
                if (item.link != null && item.link.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                    intent.putExtra("url", item.link);
                    intent.putExtra("title", item.title);
                    startActivity(intent);
                }
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFeed();
            }
        });

        loadFeed();
    }

    private void loadFeed() {
        showLoading();
        new FetchFeedTask().execute(RssFetcher.FEED_URL);
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        newsList.setVisibility(View.GONE);
    }

    private void showList() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        newsList.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        loadingView.setVisibility(View.GONE);
        newsList.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorText.setText(message);
    }

    private class FetchFeedTask extends AsyncTask<String, Void, List<NewsItem>> {

        private Exception error;

        @Override
        protected List<NewsItem> doInBackground(String... params) {
            try {
                return RssFetcher.fetch(params[0]);
            } catch (Exception e) {
                error = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<NewsItem> result) {
            if (isFinishing()) return;

            if (result == null || error != null) {
                showError(getString(R.string.error_network));
                return;
            }
            if (result.isEmpty()) {
                showError(getString(R.string.error_parse));
                return;
            }

            items.clear();
            items.addAll(result);
            adapter.notifyDataSetChanged();
            showList();
        }
    }
}
