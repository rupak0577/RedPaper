package com.redapp.domain.interactor.download

import com.redapp.domain.executor.PostExecutionThread
import com.redapp.domain.interactor.CompletableUseCase
import com.redapp.domain.repository.PostsRepository
import io.reactivex.Completable
import javax.inject.Inject

class DownloadPost @Inject constructor(private val postsRepository: PostsRepository,
                                       postExecutionThread: PostExecutionThread)
    : CompletableUseCase<DownloadPost.Params>(postExecutionThread) {

    override fun buildUseCaseCompletable(params: Params?): Completable {
        if (params == null)
            throw IllegalArgumentException("Params cannot be null")
        else
            return postsRepository.downloadPost(params.postId)
    }

    data class Params constructor(val postId: String) {
        companion object {
            fun forPost(postId: String): Params {
                return Params(postId)
            }
        }
    }
}