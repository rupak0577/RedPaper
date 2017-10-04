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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.databinding.ActivityMainBinding;
import com.ruflux.redpaper.databinding.FragmentCardBinding;
import com.ruflux.redpaper.post.PostHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SubContract.View {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String EARTH = "EarthPorn";
    private final String ROAD = "RoadPorn";
    private final String RURAL = "RuralPorn";
    private final String ABANDONED = "AbandonedPorn";

    private ActivityMainBinding mBinding;

    private SubPresenter mPresenter;
    private ConnectionReceiver mReceiver;
    private Snackbar noConn;
    private SubAdapter mAdapter;

    private String SUB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbarPrimary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        if (savedInstanceState != null)
            SUB = savedInstanceState.getString("SAVED_SUB");
        else
            SUB = EARTH;

        mAdapter = new SubAdapter();
        mBinding.recyclerFragment.setAdapter(mAdapter);
        mBinding.recyclerFragment.setLayoutManager(new LinearLayoutManager(this));

        mBinding.swipeFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPosts(true);
            }
        });

        noConn = Snackbar.make(mBinding.drawerRoot, "No Connection",
                Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.YELLOW);

        boolean connection = checkConnection();
        if (!connection)
            noConn.show();

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

        bindPresenter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new ConnectionReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("SAVED_SUB", SUB);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        unbindPresenter();
        super.onStop();
    }

    @Override
    public void bindPresenter() {
        if (mPresenter == null)
            mPresenter = new SubPresenter(this);
    }

    @Override
    public void unbindPresenter() {
        mPresenter.stop();
    }

    @Override
    public void showPosts(List<Post> posts) {
        mAdapter.setItems(posts);
    }

    @Override
    public void startLoadProgress() {
        mBinding.progressFragmentTab.setVisibility(View.VISIBLE);
        mBinding.progressFragmentTab.setIndeterminate(true);
    }

    @Override
    public void stopLoadProgress() {
        if (mBinding.swipeFragment.isRefreshing())
            mBinding.swipeFragment.setRefreshing(false);
        mBinding.progressFragmentTab.setIndeterminate(false);
        mBinding.progressFragmentTab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoadError(String message) {
        Toast.makeText(this, "Could not fetch images : " + message, Toast.LENGTH_SHORT)
                .show();
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
        mPresenter.loadPosts(false);

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

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public String getSelectedSub() {
        return SUB;
    }

    private class ConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean val = checkConnection();
            ((RedPaperApplication) context.getApplicationContext())
                    .getDownloader().setConnection(val);
            if (!val)
                noConn.show();
            else
                noConn.dismiss();
        }
    }

    private static class SubAdapter extends RecyclerView.Adapter<PostHolder> {

        private List<Post> mPosts;

        SubAdapter() {
            this.mPosts = new ArrayList<>();
        }

        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            FragmentCardBinding itemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.fragment_card, parent, false);
            return new PostHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(final PostHolder holder, int position) {
            final Post post = mPosts.get(position);

            holder.bindItem(post);
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        void setItems(List<Post> posts) {
            this.mPosts.clear();
            this.mPosts.addAll(posts);
            notifyDataSetChanged();
        }
    }
}
