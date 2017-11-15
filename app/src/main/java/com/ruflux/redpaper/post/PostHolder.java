package com.ruflux.redpaper.post;

import android.content.Intent;
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

import java.util.List;

public class PostHolder extends RecyclerView.ViewHolder {

    private final int VARIANT_THUMB = 2;
    private final int VARIANT_PREV = 3;

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

        // Trim domain text
        String dom = mItem.getDomain();
        if (dom.length() > 12)
            dom = dom.substring(0, 6) + "..." + dom.substring(dom.length() - 6, dom.length());
        mBinding.textCardItemDomain.setText(dom);

        mBinding.textCardItemRes.setText(mItem.getPreview().getImages().get(0).getSource().getWidth() +
                "x" + mItem.getPreview().getImages().get(0).getSource().getHeight());

        // https://imgur.com/xyz --> https://i.imgur.com/xyz.jpeg
        String url = item.getUrl();
        if (mItem.getDomain().equals("imgur.com")) {
            mItem.setUrl(url.substring(0, url.indexOf("/") + 2) + "i."
                    + url.substring(url.indexOf("i")) + ".jpeg");
        } else
            mItem.setUrl(url);

        String filename = url.substring(url.lastIndexOf("/") + 1);
        List<Post.Preview.Resolution> resolutions = mItem.getPreview().getImages().get(0).getResolutions();
        int totalRez = resolutions.size();

        Picasso.with(mBinding.getRoot().getContext()).load(resolutions.get(VARIANT_THUMB).getUrl())
                .fit().centerCrop().noFade()
                .placeholder(R.drawable.ic_photo_white_24dp)
                .error(R.drawable.ic_broken_image_white_24dp)
                .into(mBinding.imageCardItemThumb);
        mBinding.imageCardItemThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageViewer.class);
                intent.putExtra(ImageViewer.EXTRA_URL, resolutions
                        .get(totalRez == VARIANT_PREV ? VARIANT_PREV-1 : VARIANT_PREV).getUrl());
                v.getContext().startActivity(intent);
            }
        });

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
                    Downloader downloader = ((RedPaperApplication) v.getContext().getApplicationContext())
                            .getDownloader();

                    if (!downloader.isConnected()) {
                        Toast.makeText(v.getContext(), "No connection",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int status = downloader.queryStatus(downloadManRefId);

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
                                        .downloadImage(item.getUrl(), filename);
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
