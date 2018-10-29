package com.redapp.data.store

import com.redapp.data.model.PostEntity
import com.redapp.data.repository.PostsCache
import com.redapp.data.repository.PostsDataStore
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class PostsCacheDataStore @Inject constructor(private val postsCache: PostsCache) : PostsDataStore {
    override fun clearPosts(): Completable {
        return postsCache.clearPosts()
    }

    override fun savePosts(posts: List<PostEntity>): Completable {
        return postsCache.savePosts(posts)
                .andThen(postsCache.setLastCacheTime(System.currentTimeMillis()))
    }

    override fun getPosts(): Observable<List<PostEntity>> {
        return postsCache.getPosts()
    }

    override fun setPostAsDownloaded(postName: String): Completable {
        return postsCache.setPostAsDownloaded(postName)
    }

    override fun setPostAsNotDownloaded(postName: String): Completable {
        return postsCache.setPostAsNotDownloaded(postName)
    }
}