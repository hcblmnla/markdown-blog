package org.example.mdblog.service

import kotlinx.coroutines.flow.Flow
import org.example.mdblog.dto.AuthorDto
import org.example.mdblog.dto.PostDto
import org.example.mdblog.dto.TopicDto

interface AuthorService {
    suspend fun createOrGetAuthor(login: String, name: String): AuthorDto
    suspend fun getAuthor(login: String): AuthorDto?
    suspend fun deleteAuthor(login: String): Boolean
}

interface TopicService {
    suspend fun createOrGetTopic(author: AuthorDto, title: String): TopicDto
    suspend fun getAllTopics(author: AuthorDto): Flow<TopicDto>
    suspend fun deleteTopic(author: AuthorDto, title: String): Boolean
    suspend fun deleteAllTopics(author: AuthorDto)
}

interface PostService {
    suspend fun createOrGetPost(
        topic: TopicDto,
        title: String,
        tags: List<String>,
        content: String,
    ): PostDto

    suspend fun getPost(topic: TopicDto, title: String): PostDto?
    suspend fun getAllPosts(topic: TopicDto): Flow<PostDto>
    suspend fun getAllPostsByTag(topic: TopicDto, tagName: String): Flow<PostDto>
    suspend fun deletePost(topic: TopicDto, title: String): Boolean
    suspend fun deleteAllPosts(topic: TopicDto)
}
