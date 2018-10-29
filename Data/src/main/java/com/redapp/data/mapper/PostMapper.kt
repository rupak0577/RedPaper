package com.redapp.data.mapper

import com.redapp.data.model.PostEntity
import com.redapp.domain.model.RedditPost
import javax.inject.Inject

class PostMapper @Inject constructor() : EntityMapper<PostEntity, RedditPost> {

    override fun mapToEntity(domain: RedditPost): PostEntity {
        return PostEntity(domain.name, domain.title, domain.score, domain.created, domain.thumbnail,
                domain.url, domain.downloaded)
    }

    override fun mapFromEntity(entity: PostEntity): RedditPost {
        return RedditPost(entity.name, entity.title, entity.score, entity.created, entity.thumbnail,
                entity.url, entity.downloaded)
    }

}