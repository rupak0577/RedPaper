package com.redapp.data.repository

import com.redapp.data.model.PostEntity
import io.reactivex.Observable

interface PostsRemote {
    fun getPosts(): Observable<List<PostEntity>>
}