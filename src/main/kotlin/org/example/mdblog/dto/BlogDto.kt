package org.example.mdblog.dto

import java.time.LocalDateTime

data class AuthorDto(
    val id: Long,
    val login: String,
    val name: String
)

data class TopicDto(
    val id: Long,
    val author: AuthorDto,
    val title: String,
)

data class PostDto(
    val id: Long,
    val topic: TopicDto,
    val title: String,
    val tags: List<String>,
    val content: String,
    val updatedAt: LocalDateTime,
)
