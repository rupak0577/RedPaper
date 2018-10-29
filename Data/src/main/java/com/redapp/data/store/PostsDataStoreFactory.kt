package com.redapp.data.store

import com.redapp.data.repository.PostsDataStore
import javax.inject.Inject

class PostsDataStoreFactory @Inject constructor(private val cacheDataStore: PostsCacheDataStore,
                                                private val remoteDataStore: PostsRemoteDataStore) {
    fun getDataStore(postsCached: Boolean, cacheExpired: Boolean): PostsDataStore {
        return if (postsCached && !cacheExpired)
            cacheDataStore
        else
            remoteDataStore
    }

    fun getCachedDataStore(): PostsCacheDataStore {
        return cacheDataStore
    }
}