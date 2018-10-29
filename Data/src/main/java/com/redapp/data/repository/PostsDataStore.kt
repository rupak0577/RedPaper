package com.redapp.data.repository

import com.redapp.data.model.PostEntity
import io.reactivex.Completable
import io.reactivex.Observable

interface PostsDataStore {

    fun clearPosts(): Completable

    fun getPosts(): Observable<List<PostEntity>>

    fun savePosts(posts: List<PostEntity>): Completable

    fun setPostAsDownloaded(postName: String): Completable

    fun setPostAsNotDownloaded(postName: String): Completable
}