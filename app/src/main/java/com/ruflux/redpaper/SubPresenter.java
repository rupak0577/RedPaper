package com.ruflux.redpaper;

import com.ruflux.redpaper.data.Repository;
import com.ruflux.redpaper.data.model.Post;

import java.lang.ref.WeakReference;
import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SubPresenter implements SubContract.Presenter {

    private WeakReference<SubContract.View> mView;
    @Inject Repository mRepository;
    private CompositeDisposable mDisposable;

    public SubPresenter(SubContract.View view) {
        mView = new WeakReference<>(view);
        ((RedPaperApplication) view.fetchContext().getApplicationContext())
                .getRepositoryComponent().inject(this);
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void loadPosts() {
        mView.get().startLoadProgress();
        mDisposable.add(mRepository.getPosts(mView.get().getSelectedSub())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    if (mView.get() != null) {
                        mView.get().stopLoadProgress();
                        mView.get().showPosts(posts);
                    }
                }, throwable -> {
                    if (mView.get() != null) {
                        mView.get().stopLoadProgress();
                        mView.get().showPosts(Collections.<Post>emptyList());
                        mView.get().showLoadError();
                    }
                }));
    }

    @Override
    public void start() {
        loadPosts();
    }

    @Override
    public void stop() {
        mDisposable.clear();
    }
}
