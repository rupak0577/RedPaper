package com.ruflux.redpaper.sub;

import com.ruflux.redpaper.data.Repository;

import java.lang.ref.WeakReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SubPresenter implements SubContract.Presenter {

    private WeakReference<SubContract.View> mView;
    private Repository mRepository;
    private CompositeDisposable mDisposable;

    public SubPresenter(SubContract.View view, Repository repository) {
        mView = new WeakReference<>(view);
        mRepository = repository;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void loadPosts(String sub) {
        mView.get().onStartLoadProgress();
        mDisposable.add(mRepository.fetchPosts(sub)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    if (mView.get() != null) {
                        mView.get().onStopLoadProgress();
                        mView.get().showPosts(posts);
                    }
                }, throwable -> {
                    if (mView.get() != null) {
                        mView.get().onStopLoadProgress();
                        mView.get().onLoadError(throwable.getMessage());
                    }
                }));
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mDisposable.clear();
    }
}
