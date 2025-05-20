package org.example.mdblog.repository

import kotlinx.coroutines.flow.Flow
import org.example.mdblog.entity.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : CoroutineCrudRepository<Author, Long> {
    suspend fun findByLogin(login: String): Author?
}

@Repository
interface TopicRepository : CoroutineCrudRepository<Topic, Long> {
    suspend fun findByAuthorIdAndTitle(authorId: Long, title: String): Topic?
    suspend fun findAllByAuthorId(authorId: Long): Flow<Topic>
    suspend fun deleteAllByAuthorId(authorId: Long)
}

@Repository
interface PostRepository : CoroutineCrudRepository<Post, Long> {
    suspend fun findByTopicIdAndTitle(topicId: Long, title: String): Post?
    suspend fun findAllByTopicId(topicId: Long): Flow<Post>
    suspend fun deleteAllByTopicId(topicId: Long)
}

@Repository
interface TagRepository : CoroutineCrudRepository<Tag, Long> {
    suspend fun findByName(name: String): Tag?
}

@Repository
interface PostTagRepository : CoroutineCrudRepository<PostTag, Long> {
    suspend fun findAllByTagId(tagId: Long): Flow<PostTag>
}
