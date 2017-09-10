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
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.ruflux.redpaper.databinding.ActivityMainBinding;
import com.ruflux.redpaper.sub.SubFragment;
import com.ruflux.redpaper.sub.SubPresenter;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private ActivityMainBinding mBinding;

    private SubFragment mFragment;
    private SubPresenter mPresenter;
    private int page;
    private ConnectionReceiver mReceiver;
    private Snackbar noConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbarPrimary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        mFragment = new SubFragment();
        getSupportFragmentManager().beginTransaction().add(mBinding.frameContentRoot.getId(), mFragment)
                .commit();
        mPresenter = new SubPresenter(mFragment);

        if(!checkPermission()) {
            requestPermission();
        }

        noConn = Snackbar.make(mBinding.frameContentRoot, "Cannot connect to the Internet",
                Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.YELLOW);

        boolean connection = checkConnection();
        if (!connection) {
            noConn.show();
            mPresenter.isConnected(false);
        }

        if (savedInstanceState != null) {
            mPresenter.loadPage(savedInstanceState.getInt("PAGE"));
            int itemId = mBinding.navViewRoot.getMenu()
                    .getItem(savedInstanceState.getInt("PAGE")).getItemId();
            mBinding.navViewRoot.setCheckedItem(itemId);
            setTitle(itemId);
        } else
            setTitle(mBinding.navViewRoot.getMenu().getItem(0).getTitle());

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
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new ConnectionReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("PAGE", page);
        super.onSaveInstanceState(outState);
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
        switch (item.getItemId()) {
            case R.id.drawer_item_0:
                page = 0;
                break;
            case R.id.drawer_item_1:
                page = 1;
                break;
            case R.id.drawer_item_2:
                page = 2;
                break;
            case R.id.drawer_item_3:
                page = 3;
                break;
            default:
                page = 0;
        }

        mPresenter.loadPage(page);

        setTitle(item.getTitle());
        mBinding.navViewRoot.setCheckedItem(item.getItemId());
        mBinding.drawerRoot.closeDrawers();
    }

    public boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private class ConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean val = checkConnection();
            mPresenter.isConnected(val);
            if (!val)
                noConn.show();
            else
                noConn.dismiss();
        }
    }
}
