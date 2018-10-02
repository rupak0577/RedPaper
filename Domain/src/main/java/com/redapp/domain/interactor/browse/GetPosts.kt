package com.redapp.domain.interactor.browse

import com.redapp.domain.executor.PostExecutionThread
import com.redapp.domain.interactor.ObservableUseCase
import com.redapp.domain.model.RedditPost
import com.redapp.domain.repository.PostsRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetPosts @Inject constructor(private val postsRepository: PostsRepository,
                                   postExecutionThread: PostExecutionThread)
    : ObservableUseCase<List<RedditPost>, Nothing>(postExecutionThread) {

    override fun buildUseCaseObservable(params: Nothing?): Observable<List<RedditPost>> {
        return postsRepository.getPosts()
    }
}