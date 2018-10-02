package com.redapp.domain.repository

import com.redapp.domain.model.RedditPost
import io.reactivex.Completable
import io.reactivex.Observable

interface PostsRepository {

    fun getPosts(): Observable<List<RedditPost>>

    fun downloadPost(postId: String): Completable
}