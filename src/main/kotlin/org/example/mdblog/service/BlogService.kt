package org.example.mdblog.service

import kotlinx.coroutines.flow.Flow
import org.example.mdblog.entity.Author
import org.example.mdblog.entity.Post
import org.example.mdblog.entity.Topic

interface AuthorService {
    suspend fun createOrGetAuthor(login: String, name: String): Author
    suspend fun deleteAuthor(login: String): Boolean
}

interface TopicService {
    suspend fun createOrGetTopic(author: Author, title: String): Topic
    suspend fun getAllTopics(author: Author): Flow<Topic>
    suspend fun deleteTopic(author: Author, title: String): Boolean
    suspend fun deleteAllTopics(author: Author)
}

interface PostService {
    suspend fun createOrGetPost(
        topic: Topic,
        title: String,
        tags: List<String>,
        content: String,
    ): Post

    suspend fun getAllPosts(topic: Topic): Flow<Post>
    suspend fun getAllPostsByTag(tagName: String): Flow<Post>
    suspend fun deletePost(topic: Topic, title: String): Boolean
    suspend fun deleteAllPosts(topic: Topic)
}
