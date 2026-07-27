package com.example.ajreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    private final LayoutInflater inflater;
    private final List<NewsItem> items;

    public NewsAdapter(Context context, List<NewsItem> items) {
        super(context, 0, items);
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        TextView headline;
        TextView summary;
        TextView timestamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_news, parent, false);
            holder = new ViewHolder();
            holder.headline = (TextView) convertView.findViewById(R.id.headline);
            holder.summary = (TextView) convertView.findViewById(R.id.summary);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NewsItem item = items.get(position);
        holder.headline.setText(item.title);
        holder.summary.setText(item.description);
        holder.timestamp.setText(RelativeTime.format(item.pubDate));

        return convertView;
    }
}
