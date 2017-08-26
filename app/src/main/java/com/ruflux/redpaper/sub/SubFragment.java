package com.ruflux.redpaper.sub;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruflux.redpaper.R;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.databinding.FragmentCardBinding;
import com.ruflux.redpaper.databinding.FragmentSubBinding;
import com.ruflux.redpaper.post.PostHolder;

import java.util.ArrayList;
import java.util.List;

public class SubFragment extends Fragment implements SubContract.View {

    private static final String ARG_PAGE_NUMBER = "page_number";

    private SubContract.Presenter mPresenter;
    private FragmentSubBinding mBinding;
    private SubAdapter mAdapter;

    public SubFragment() {
    }

    public static SubFragment newInstance(int pageNumber) {
        SubFragment fragment = new SubFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mAdapter = new SubAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onStop() {
        mPresenter.stop();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sub, container, false);
        mBinding.recyclerFragment.setAdapter(mAdapter);
        mBinding.recyclerFragment.setLayoutManager(new LinearLayoutManager(getContext()));

        mBinding.swipeFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPosts();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void showPosts(List<Post> posts) {
        mAdapter.setItems(posts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getPage() {
        return getArguments().getInt(ARG_PAGE_NUMBER);
    }

    @Override
    public void startLoadProgress() {
        //mBinding.shimmerFragmentSub.startShimmerAnimation();

        mBinding.progressFragmentTab.setVisibility(View.VISIBLE);
        mBinding.progressFragmentTab.setIndeterminate(true);
    }

    @Override
    public void stopLoadProgress() {
        //mBinding.shimmerFragmentSub.stopShimmerAnimation();

        if (mBinding.swipeFragment.isRefreshing())
            mBinding.swipeFragment.setRefreshing(false);
        mBinding.progressFragmentTab.setIndeterminate(false);
        mBinding.progressFragmentTab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void attachPresenter(SubContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getActivityContext() {
        return getContext();
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

        public void setItems(List<Post> posts) {
            this.mPosts.clear();
            this.mPosts = posts;
            notifyDataSetChanged();
        }
    }
}
