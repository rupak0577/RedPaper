package com.redapp.data.repository

import com.redapp.data.model.PostEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PostsCache {

    fun clearPosts(): Completable

    fun savePosts(posts: List<PostEntity>): Completable

    fun getPosts(): Observable<List<PostEntity>>

    fun setPostAsDownloaded(postName: String): Completable

    fun setPostAsNotDownloaded(postName: String): Completable

    fun arePostsCached(): Single<Boolean>

    fun setLastCacheTime(lastCache: Long): Completable

    fun isPostsCacheExpired(): Single<Boolean>

}