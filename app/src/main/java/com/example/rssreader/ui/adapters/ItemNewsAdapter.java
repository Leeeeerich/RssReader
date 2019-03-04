package com.example.rssreader.ui.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rssreader.R;
import com.example.rssreader.databinding.ItemNewsBinding;
import com.example.rssreader.model.dto.News;

import java.util.ArrayList;
import java.util.List;

public class ItemNewsAdapter extends RecyclerView.Adapter<ItemNewsAdapter.MyViewHolder> {
    private List<News> mNewsList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemNewsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()), R.layout.item_news, viewGroup, false);
        return new MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final News newsItem = mNewsList.get(position);
        myViewHolder.setBinding(newsItem);
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

        private ItemNewsBinding mBinding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public void setBinding(News news) {
            mBinding.setNews(news);
        }
    }

}
