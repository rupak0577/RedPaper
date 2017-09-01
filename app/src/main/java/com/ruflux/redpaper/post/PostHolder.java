package com.ruflux.redpaper.post;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.ruflux.redpaper.R;
import com.ruflux.redpaper.data.Downloader;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.databinding.FragmentCardBinding;
import com.squareup.picasso.Picasso;

public class PostHolder extends RecyclerView.ViewHolder {

    private final FragmentCardBinding mBinding;
    private Post mItem;

    public PostHolder(FragmentCardBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }

    public void bindItem(final Post item) {
        mItem = item;

        mBinding.textCardItemTitle.setText(mItem.getTitle().trim());
        mBinding.textCardItemDomain.setText(mItem.getDomain());
        Picasso.with(mBinding.getRoot().getContext()).load(mItem.getThumbnailUrl()).
                fit().centerCrop().noFade().
                placeholder(R.drawable.ic_photo_white_24dp).
                error(R.drawable.ic_broken_image_white_24dp).
                into(mBinding.imageCardItemThumb);

        // Workaround for issue with FAB
        // https://stackoverflow.com/questions/39388978/android-floating-action-button-in-wrong-position
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                mBinding.fabCardItem.getLayoutParams();
        params.anchorGravity = Gravity.BOTTOM | GravityCompat.END;
        params.setAnchorId(mBinding.imageCardItemThumb.getId());
        mBinding.fabCardItem.setLayoutParams(params);
        mBinding.fabCardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItem.getDomain().equals("flickr.com"))
                    Toast.makeText(v.getContext(), "Flickr links cannot be saved on mobile",
                            Toast.LENGTH_LONG).show();
                else {
                    //Downloader.downloadPost(v.getContext(), item.getUrl(), item.getFileName());
                }
            }
        });
    }
}
