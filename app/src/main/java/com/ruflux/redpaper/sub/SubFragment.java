package com.ruflux.redpaper.sub;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruflux.redpaper.R;
import com.ruflux.redpaper.data.model.Post;
import com.ruflux.redpaper.databinding.FragmentSubBinding;

import java.util.List;

public class SubFragment extends Fragment implements SubContract.View {

    private FragmentListener mListener;
    private SubAdapter mAdapter;

    public SubFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new SubAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSubBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sub, container, false);
        mBinding.recyclerFragment.setAdapter(mAdapter);
        mBinding.recyclerFragment.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            mListener = (FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Context fetchContext() {
        return this.getContext();
    }

    @Override
    public void onStartLoadProgress() {
        if (mListener != null) {
            mListener.startLoadProgress();
        }
    }

    @Override
    public void onStopLoadProgress() {
        if (mListener != null) {
            mListener.stopLoadProgress();
        }
    }

    @Override
    public void showPosts(List<Post> posts) {
        mAdapter.setItems(posts);
    }

    @Override
    public void onLoadError(String message) {
        if (mListener != null) {
            mListener.showLoadError(message);
        }
    }


    public interface FragmentListener {
        void startLoadProgress();
        void stopLoadProgress();
        void showLoadError(String message);
    }
}
