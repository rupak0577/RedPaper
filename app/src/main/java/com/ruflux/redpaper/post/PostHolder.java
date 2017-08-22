package com.ruflux.redpaper.post;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.databinding.FragmentCardBinding;
import com.squareup.picasso.Picasso;

public class PostHolder extends RecyclerView.ViewHolder implements
        PostContract.View {

    private final FragmentCardBinding mBinding;
    private Post mItem;
    private PostContract.Presenter mPresenter;

    public PostHolder(FragmentCardBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }

    public void bindItem(final Post item) {
        mItem = item;

        mBinding.textCardItemTitle.setText(mItem.getTitle().trim());
        mBinding.textCardItemDomain.setText(mItem.getDomain());
        Picasso.with(mBinding.getRoot().getContext()).load(mItem.getThumbnailUrl()).
                fit().into(mBinding.imageCardItemThumb);
        mBinding.fabCardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItem.getDomain().equals("flickr.com"))
                    Toast.makeText(v.getContext(), "Flickr links cannot be saved on mobile",
                            Toast.LENGTH_LONG).show();
                else {
                    mPresenter.downloadPost(item.getUrl());
                }
            }
        });
    }

    @Override
    public void setPresenter(@NonNull PostContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getActivityContext() {
        return mBinding.getRoot().getContext();
    }

    @Override
    public void showDownloadProgress() {

    }

    @Override
    public void markDownloaded() {

    }
}
