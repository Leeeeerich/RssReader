package com.example.rssreader.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.model.dto.News;

import java.util.List;

public class ItemNewsAdapter extends RecyclerView.Adapter<ItemNewsAdapter.MyViewHolder> {
    private List<News> mNewsList;
    private LayoutInflater mInflater;

    public ItemNewsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = mInflater.inflate(R.layout.item_news, viewGroup, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final News newsItem = mNewsList.get(position);
        final Context context = myViewHolder.itemView.getContext();
        myViewHolder.setTitle(context.getString(R.string.title, newsItem.getTitle()));
        myViewHolder.setUrl(context.getString(R.string.url, newsItem.getUrl()));
        myViewHolder.setSize(context.getString(R.string.size, newsItem.getSize()));
    }

    @Override
    public int getItemCount() {
        return mNewsList != null ? mNewsList.size() : 0;
    }

    public void setNewsList(List<News> newsList) {
        this.mNewsList = newsList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mUrl;
        private TextView mSize;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tvTitle);
            mUrl = itemView.findViewById(R.id.tvUrl);
            mSize = itemView.findViewById(R.id.tvSize);
        }

        public void setTitle(String title) {
            mTitle.setText(title);
        }

        public void setUrl(String url) {
            mUrl.setText(url);
        }

        public void setSize(String size) {
            mSize.setText(size);
        }
    }

}
