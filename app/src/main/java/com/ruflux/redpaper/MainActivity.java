package com.ruflux.redpaper;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;

import com.ruflux.redpaper.databinding.ActivityMainBinding;
import com.ruflux.redpaper.sub.SubFragment;
import com.ruflux.redpaper.sub.SubPresenter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    private SparseArray<SubFragment> fragmentList;
    private SubPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbarPrimary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        fragmentList = new SparseArray<>();

        mBinding.navViewRoot.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return false;
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mBinding.drawerRoot.openDrawer(Gravity.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectDrawerItem(MenuItem item) {
        int pos;

        switch (item.getItemId()) {
            case R.id.drawer_item_0:
                pos = 0;
                break;
            case R.id.drawer_item_1:
                pos = 1;
                break;
            case R.id.drawer_item_2:
                pos = 2;
                break;
            case R.id.drawer_item_3:
                pos = 3;
                break;
            default:
                pos = 0;
        }

        SubFragment fragment;

        if (fragmentList.get(pos) == null) {
            fragment = SubFragment.newInstance(pos);
            fragmentList.append(pos, fragment);

            mPresenter = new SubPresenter();
        } else {
            fragment = fragmentList.get(pos);
        }

        mPresenter.attachTo(fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(mBinding.frameContentRoot.getId(), fragment).
                commit();

        item.setChecked(true);
        setTitle(item.getTitle());

        mBinding.drawerRoot.closeDrawers();
    }
}
