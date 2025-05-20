package org.example.mdblog.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.example.mdblog.entity.*
import org.example.mdblog.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrmAuthorService(private val authorRepository: AuthorRepository) : AuthorService {

    override suspend fun createOrGetAuthor(login: String, name: String): Author {
        return authorRepository.findByLogin(login)
            ?: authorRepository.save(Author(login = login, name = name))
    }

    override suspend fun deleteAuthor(login: String): Boolean {
        val author = authorRepository.findByLogin(login) ?: return false
        authorRepository.delete(author)
        return true
    }
}

@Service
@Transactional
class OrmTopicService(private val topicRepository: TopicRepository) : TopicService {

    override suspend fun createOrGetTopic(author: Author, title: String): Topic {
        return topicRepository.findByAuthorIdAndTitle(author.id!!, title)
            ?: topicRepository.save(Topic(authorId = author.id, title = title))
    }

    @Transactional(readOnly = true)
    override suspend fun getAllTopics(author: Author): Flow<Topic> {
        return topicRepository.findAllByAuthorId(author.id!!)
    }

    override suspend fun deleteTopic(author: Author, title: String): Boolean {
        val topic = topicRepository.findByAuthorIdAndTitle(author.id!!, title)
            ?: return false
        topicRepository.delete(topic)
        return true
    }

    override suspend fun deleteAllTopics(author: Author) {
        topicRepository.deleteAllByAuthorId(author.id!!)
    }
}

@Service
@Transactional
class OrmPostService(
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val postTagRepository: PostTagRepository,
) : PostService {

    override suspend fun createOrGetPost(
        topic: Topic,
        title: String,
        tags: List<String>,
        content: String,
    ): Post {
        val existing = postRepository.findByTopicIdAndTitle(topic.id!!, title)
        if (existing != null) return existing
        val post = postRepository.save(
            Post(
                topicId = topic.id,
                title = title,
                content = content
            )
        )
        tags.forEach { tagName ->
            val tag = createOrGetTag(tagName)
            createPostTag(post.id!!, tag.id!!)
        }
        return post
    }

    @Transactional(readOnly = true)
    override suspend fun getAllPosts(topic: Topic): Flow<Post> {
        return postRepository.findAllByTopicId(topic.id!!)
    }

    @Transactional(readOnly = true)
    override suspend fun getAllPostsByTag(tagName: String): Flow<Post> {
        val tag = createOrGetTag(tagName)
        return getAllPostTags(tag.id!!)
            .map { (postId, _) ->
                postRepository.findById(postId)
            }
            .filterNotNull()
    }

    override suspend fun deletePost(topic: Topic, title: String): Boolean {
        val post = postRepository.findByTopicIdAndTitle(topic.id!!, title)
            ?: return false
        postRepository.delete(post)
        return true
    }

    override suspend fun deleteAllPosts(topic: Topic) {
        postRepository.deleteAllByTopicId(topic.id!!)
    }

    private suspend fun createOrGetTag(name: String): Tag {
        return tagRepository.findByName(name)
            ?: tagRepository.save(Tag(name = name))
    }

    private suspend fun createPostTag(postId: Long, tagId: Long) {
        postTagRepository.save(PostTag(postId = postId, tagId = tagId))
    }

    private suspend fun getAllPostTags(tagId: Long): Flow<PostTag> {
        return postTagRepository.findAllByTagId(tagId)
    }
}
