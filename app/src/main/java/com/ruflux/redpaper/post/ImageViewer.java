package com.ruflux.redpaper.post;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.ruflux.redpaper.R;
import com.ruflux.redpaper.databinding.ActivityImageviewBinding;
import com.squareup.picasso.Picasso;

public class ImageViewer extends AppCompatActivity {

    public static final String EXTRA_URL = "com.ruflux.redpaper.VIEW_IMAGE";
    private ActivityImageviewBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_imageview);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);

        Picasso.with(this).load(url)
                .fit().centerCrop().noFade()
                .placeholder(R.drawable.ic_photo_white_24dp)
                .error(R.drawable.ic_broken_image_white_24dp)
                .into(mBinding.imageView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            finish();
        }
        return super.dispatchTouchEvent(ev);
    }
}
