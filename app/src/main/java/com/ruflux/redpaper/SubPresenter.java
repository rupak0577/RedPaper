package com.ruflux.redpaper;

import com.ruflux.redpaper.data.Repository;
import com.ruflux.redpaper.data.model.Post;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubPresenter implements SubContract.Presenter {

    private WeakReference<SubContract.View> mView;
    private Repository mRepository;
    private CompositeDisposable mDisposable;

    public SubPresenter(SubContract.View view) {
        mView = new WeakReference<SubContract.View>(view);
        mRepository = Repository.getInstance();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void loadPosts(boolean refresh) {
        if (refresh)
            mRepository.refreshPosts();
        mView.get().startLoadProgress();
        mDisposable.add(mRepository.getPosts(mView.get().getSelectedSub())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Post>>() {
                    @Override
                    public void accept(List<Post> posts) throws Exception {
                        if (mView.get() != null) {
                            mView.get().stopLoadProgress();
                            mView.get().showPosts(posts);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mView.get() != null) {
                            mView.get().stopLoadProgress();
                            mView.get().showPosts(Collections.<Post>emptyList());
                            mView.get().showLoadError(throwable.getMessage());
                        }
                    }
                }));
    }

    @Override
    public void start() {
        loadPosts(false);
    }

    @Override
    public void stop() {
        mDisposable.clear();
    }
}
