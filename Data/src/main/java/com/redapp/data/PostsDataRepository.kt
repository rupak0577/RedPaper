package com.redapp.data

import com.redapp.data.mapper.PostMapper
import com.redapp.data.repository.PostsCache
import com.redapp.data.store.PostsDataStoreFactory
import com.redapp.domain.model.RedditPost
import com.redapp.domain.repository.PostsRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class PostsDataRepository @Inject constructor(private val postMapper: PostMapper,
                                              private val postsCache: PostsCache,
                                              private val postsDataStoreFactory: PostsDataStoreFactory) : PostsRepository {
    override fun getPosts(): Observable<List<RedditPost>> {
        return Observable.zip(postsCache.arePostsCached().toObservable(),
                postsCache.isPostsCacheExpired().toObservable(),
                BiFunction<Boolean, Boolean, Pair<Boolean, Boolean>> { areCached, isExpired ->
                    Pair(areCached, isExpired)
                })
                .flatMap {
                    postsDataStoreFactory.getDataStore(it.first, it.second).getPosts()
                }
                .flatMap { posts ->
                    postsDataStoreFactory.getCachedDataStore().savePosts(posts).andThen(Observable.just(posts))
                }
                .map {
                    it.map {
                        postMapper.mapFromEntity(it)
                    }
                }
    }

    override fun downloadPost(postId: String): Completable {
        return postsDataStoreFactory.getCachedDataStore().setPostAsDownloaded(postId)
    }

}