package com.ruflux.redpaper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ruflux.redpaper.databinding.ActivityMainBinding;
import com.ruflux.redpaper.sub.SubFragment;
import com.ruflux.redpaper.sub.SubPresenter;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

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
                        hideInfoText();
                        selectDrawerItem(item);
                        return false;
                    }
                }
        );

        if(!checkPermission()) {
            requestPermission();
        }

        checkConnection();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied. Write permission required to save images",
                            Toast.LENGTH_LONG).show();
                }
        }
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

        selectFragment(pos);

        item.setChecked(true);
        setTitle(item.getTitle());

        mBinding.drawerRoot.closeDrawers();
    }

    private void selectFragment(int position) {
        SubFragment fragment;

        if (fragmentList.get(position) == null) {
            fragment = new SubFragment();
            fragmentList.append(position, fragment);

            if (mPresenter == null)
                mPresenter = new SubPresenter();
        } else {
            fragment = fragmentList.get(position);
        }

        mPresenter.setContentPage(position);
        mPresenter.attachTo(fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(mBinding.frameContentRoot.getId(), fragment).
                commit();
    }

    public void checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (!(activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting())) {
            Snackbar.make(mBinding.frameContentRoot, "Cannot connect to the Internet",
                    Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.YELLOW).show();
        }
    }

    private void hideInfoText() {
        if (mBinding.textInfo.getVisibility() == View.VISIBLE)
            mBinding.textInfo.setVisibility(View.GONE);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }
}
