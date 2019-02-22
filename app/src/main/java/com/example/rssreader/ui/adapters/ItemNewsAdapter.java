package com.example.rssreader.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.model.NewsItem;

import java.util.List;

public class ItemNewsAdapter extends RecyclerView.Adapter<ItemNewsAdapter.MyViewHolder> {

    private List<NewsItem> mNewsList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_news, viewGroup, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final NewsItem newsItem = mNewsList.get(position);

        //TODO Insert text
        myViewHolder.mTitle.setText("");
        myViewHolder.mUrl.setText("");
        myViewHolder.mSize.setText("");
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public void setNewsList(List newsList) {
        this.mNewsList = newsList;
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
    }
}
