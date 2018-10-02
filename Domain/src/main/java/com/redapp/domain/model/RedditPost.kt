package com.redapp.domain.model

class RedditPost(val name: String, val title: String, val score: Int, val created: Long,
                 val thumbnail: String?, val url: String, val downloaded: Boolean)