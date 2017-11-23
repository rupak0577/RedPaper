package com.ruflux.redpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.ruflux.redpaper.databinding.ActivityMainBinding;
import com.ruflux.redpaper.di.AppModule;
import com.ruflux.redpaper.di.sub.DaggerSubComponent;
import com.ruflux.redpaper.di.sub.SubContractViewModule;
import com.ruflux.redpaper.sub.SubFragment;
import com.ruflux.redpaper.sub.SubPresenter;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements SubFragment.FragmentListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String EARTH = "EarthPorn";
    private final String ROAD = "RoadPorn";
    private final String RURAL = "RuralPorn";
    private final String ABANDONED = "AbandonedPorn";
    private String SUB = EARTH;

    private ActivityMainBinding mBinding;

    private ConnectionReceiver mReceiver;
    private Snackbar noConn;
    private SubFragment mFragment;

    @Inject SubPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.swipeFragment.setOnRefreshListener(() -> mPresenter.loadPosts(SUB));
        mBinding.navViewRoot.setNavigationItemSelectedListener(
                item -> {
                    selectDrawerItem(item);
                    return false;
                }
        );
        noConn = Snackbar.make(mBinding.drawerRoot, R.string.no_connection,
                Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.YELLOW);

        setSupportActionBar(mBinding.toolbarPrimary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        if (!isConnected())
            noConn.show();

        mFragment = new SubFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragment)
                .commit();

        DaggerSubComponent.builder()
                .appModule(new AppModule(this.getApplication()))
                .subContractViewModule(new SubContractViewModule(mFragment))
                .build().inject(this);

        if (savedInstanceState != null)
            SUB = savedInstanceState.getString("SAVED");
        mPresenter.loadPosts(SUB);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("SAVED", SUB);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isPermissionGranted())
            requestPermission();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new ConnectionReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        mPresenter.stop();
        super.onStop();
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
                    Toast.makeText(this, R.string.permission_granted,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.permission_denied,
                            Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }

    @Override
    public void startLoadProgress() {
        mBinding.swipeFragment.setRefreshing(true);
    }

    @Override
    public void stopLoadProgress() {
        mBinding.swipeFragment.setRefreshing(false);
    }

    @Override
    public void showLoadError(String message) {
        Toast.makeText(this, getString(R.string.load_error) + message, Toast.LENGTH_SHORT).show();
    }

    public boolean isPermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }

    private void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_0:
                SUB = EARTH;
                break;
            case R.id.drawer_item_1:
                SUB = ROAD;
                break;
            case R.id.drawer_item_2:
                SUB = RURAL;
                break;
            case R.id.drawer_item_3:
                SUB = ABANDONED;
        }

        mPresenter.stop();
        mPresenter.loadPosts(SUB);

        setTitle(item.getTitle());
        mBinding.navViewRoot.setCheckedItem(item.getItemId());
        mBinding.drawerRoot.closeDrawers();
    }

    private class ConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean val = isConnected();
            ((RedPaperApplication) context.getApplicationContext()).setConnected(val);
            if (!val)
                noConn.show();
            else
                noConn.dismiss();
        }
    }
}
