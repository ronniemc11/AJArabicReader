package com.example.ajreader;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView newsList;
    private View loadingView;
    private View errorView;
    private TextView errorText;
    private Button retryButton;
    private LinearLayout tabRow;

    private List<NewsItem> items = new ArrayList<NewsItem>();
    private NewsAdapter adapter;

    private static final String[] TAB_KEYS = {"home", "sports", "middle_east", "world"};
    private String selectedTab = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsList = (ListView) findViewById(R.id.news_list);
        loadingView = findViewById(R.id.loading_view);
        errorView = findViewById(R.id.error_view);
        errorText = (TextView) findViewById(R.id.error_text);
        retryButton = (Button) findViewById(R.id.retry_button);
        tabRow = (LinearLayout) findViewById(R.id.tab_row);

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

        setupTabs();
        setupBottomNav();

        loadFeed();
    }

    private void setupTabs() {
        String[] labels = {
                getString(R.string.tab_home),
                getString(R.string.tab_sports),
                getString(R.string.tab_middle_east),
                getString(R.string.tab_world)
        };

        LayoutInflater inflater = LayoutInflater.from(this);
        tabRow.removeAllViews();

        for (int i = 0; i < TAB_KEYS.length; i++) {
            final String key = TAB_KEYS[i];
            TextView tab = (TextView) inflater.inflate(R.layout.tab_item, tabRow, false);
            tab.setText(labels[i]);
            updateTabStyle(tab, key.equals(selectedTab));
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTabSelected(key);
                }
            });
            tabRow.addView(tab);
        }
    }

    private void updateTabStyle(TextView tab, boolean selected) {
        if (selected) {
            tab.setTextColor(0xFFD9A441);
            tab.setBackgroundResource(R.drawable.bg_tab_selected);
        } else {
            tab.setTextColor(0xFFAAAAAA);
            tab.setBackgroundColor(0x00000000);
        }
    }

    private void onTabSelected(String key) {
        selectedTab = key;
        for (int i = 0; i < tabRow.getChildCount(); i++) {
            TextView tab = (TextView) tabRow.getChildAt(i);
            updateTabStyle(tab, TAB_KEYS[i].equals(key));
        }

        if ("home".equals(key)) {
            loadFeed();
        } else {
            items.clear();
            adapter.notifyDataSetChanged();
            showError(getString(R.string.feature_not_available));
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.nav_news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        findViewById(R.id.nav_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LiveActivity.class));
            }
        });
        findViewById(R.id.nav_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.feature_not_available, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.feature_not_available, Toast.LENGTH_SHORT).show();
            }
        });
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
    String detail = (error != null && error.getMessage() != null)
            ? error.getClass().getSimpleName() + ": " + error.getMessage()
            : "unknown error";
    showError(getString(R.string.error_network) + "\n\n[DEBUG] " + detail);
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
