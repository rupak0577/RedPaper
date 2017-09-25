package com.ruflux.redpaper.post;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.ruflux.redpaper.MainActivity;
import com.ruflux.redpaper.R;
import com.ruflux.redpaper.RedPaperApplication;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.databinding.FragmentCardBinding;
import com.squareup.picasso.Picasso;

public class PostHolder extends RecyclerView.ViewHolder {

    private final FragmentCardBinding mBinding;
    private Post mItem;
    private long downloadManRefId;

    public PostHolder(FragmentCardBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }

    public void bindItem(final Post item) {
        mItem = item;

        mBinding.textCardItemTitle.setText(mItem.getTitle().trim());
        mBinding.textCardItemDomain.setText(mItem.getDomain());
        mBinding.textCardItemRes.setText(mItem.getPreview().getImages().get(0).getSource()
                .getWidth() + "x" + mItem.getPreview().getImages().get(0).getSource().getHeight());
        Picasso.with(mBinding.getRoot().getContext()).load(mItem.getThumbnailUrl())
                .fit().centerCrop().noFade().
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
                if (((MainActivity) v.getContext()).checkPermission()) {
                    int status = ((RedPaperApplication) v.getContext().getApplicationContext())
                            .getDownloader()
                            .queryStatus(downloadManRefId);

                    switch (status) {
                        case 0:
                            if (mItem.getDomain().equals("flickr.com"))
                                Toast.makeText(v.getContext(), "Flickr links cannot be downloaded on mobile",
                                        Toast.LENGTH_LONG).show();
                            else {
                                Toast.makeText(v.getContext(), "Downloading image",
                                        Toast.LENGTH_SHORT).show();

                                downloadManRefId = ((RedPaperApplication) v.getContext()
                                        .getApplicationContext()).getDownloader()
                                        .downloadImage(item.getUrl(), item.getFilename());
                            }
                            break;
                        case 1:
                            Toast.makeText(v.getContext(), "Download already in progress",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    ((MainActivity) v.getContext()).requestPermission();
                }
            }
        });
    }
}
