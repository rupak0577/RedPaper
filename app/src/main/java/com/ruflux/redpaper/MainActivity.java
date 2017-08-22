package com.ruflux.redpaper;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.ruflux.redpaper.databinding.ActivityMainBinding;
import com.ruflux.redpaper.sub.SubFragment;
import com.ruflux.redpaper.sub.SubPresenter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbarPrimary);

        final SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        binding.viewpagerPrimary.setAdapter(adapter);
        binding.tablayoutPrimary.setupWithViewPager(binding.viewpagerPrimary);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public SubFragment getItem(int position) {
            SubFragment fragment = SubFragment.newInstance(0);
            new SubPresenter(fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "EARTH";
                case 1:
                    return "ROAD";
                case 2:
                    return "RURAL";
                case 3:
                    return "ABANDONED";
            }
            return null;
        }
    }
}
