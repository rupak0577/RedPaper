package com.ruflux.redpaper.sub;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ruflux.redpaper.R;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.databinding.FragmentCardBinding;
import com.ruflux.redpaper.post.PostHolder;

import java.util.ArrayList;
import java.util.List;

class SubAdapter extends RecyclerView.Adapter<PostHolder> {

    private List<Post> mPosts;

    SubAdapter() {
        this.mPosts = new ArrayList<>();
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FragmentCardBinding itemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.fragment_card, parent, false);
        return new PostHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final PostHolder holder, int position) {
        final Post post = mPosts.get(position);

        holder.bindItem(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    void setItems(List<Post> posts) {
        this.mPosts.clear();
        this.mPosts.addAll(posts);
        notifyDataSetChanged();
    }
}