package com.ruflux.redpaper.sub;

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

    private int contentPage;
    private String SUB;

    public SubPresenter(SubFragment subFragment) {
        mView = new WeakReference<SubContract.View>(subFragment);
        mView.get().attachPresenter(this);
        mRepository = Repository.getInstance();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void loadPosts(boolean refresh) {
        switch (contentPage) {
            case 0:
                SUB = "EarthPorn";
                break;
            case 1:
                SUB = "RoadPorn";
                break;
            case 2:
                SUB = "RuralPorn";
                break;
            case 3:
                SUB = "AbandonedPorn";
        }
        mView.get().startLoadProgress();
        mDisposable.add(mRepository.getPosts(SUB)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mView.get() != null) {
                            mView.get().stopLoadProgress();
                            mView.get().showPosts(Collections.<Post>emptyList());
                            mView.get().showLoadError();
                        }
                    }
                })
                .subscribe(new Consumer<List<Post>>() {
                    @Override
                    public void accept(List<Post> posts) throws Exception {
                        if (mView.get() != null) {
                            mView.get().stopLoadProgress();
                            mView.get().showPosts(posts);
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

    public void setSub(int sub) {
        this.contentPage = sub;
    }

    public void isConnected(boolean value) {
        mRepository.isConnected(value);
    }
}
