package com.redapp.data.store

import com.redapp.data.model.PostEntity
import com.redapp.data.repository.PostsDataStore
import com.redapp.data.repository.PostsRemote
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class PostsRemoteDataStore @Inject constructor(private val remote: PostsRemote) : PostsDataStore {
    override fun getPosts(): Observable<List<PostEntity>> {
        return remote.getPosts()
    }

    override fun clearPosts(): Completable {
        throw UnsupportedOperationException("Clearing posts not supported by remote")
    }

    override fun savePosts(posts: List<PostEntity>): Completable {
        throw UnsupportedOperationException("Saving posts not supported by remote")
    }

    override fun setPostAsDownloaded(postName: String): Completable {
        throw UnsupportedOperationException("Setting post downloaded state not supported by remote")
    }

    override fun setPostAsNotDownloaded(postName: String): Completable {
        throw UnsupportedOperationException("Setting posts downloaded state not supported by remote")
    }
}