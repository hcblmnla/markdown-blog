package org.example.mdblog.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.example.mdblog.dto.*
import org.example.mdblog.entity.*
import org.example.mdblog.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrmAuthorService(private val authorRepository: AuthorRepository) : AuthorService {

    override suspend fun createOrGetAuthor(login: String, name: String): AuthorDto {
        val author = authorRepository.findByLogin(login)
            ?: authorRepository.save(Author(login = login, name = name))
        return asAuthorDto(author)
    }

    @Transactional(readOnly = true)
    override suspend fun getAuthor(login: String): AuthorDto? {
        return authorRepository.findByLogin(login)?.let { asAuthorDto(it) }
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

    override suspend fun createOrGetTopic(author: AuthorDto, title: String): TopicDto {
        val topic = topicRepository.findByAuthorIdAndTitle(author.id, title)
            ?: topicRepository.save(Topic(authorId = author.id, title = title))
        return asTopicDto(topic, author)
    }

    @Transactional(readOnly = true)
    override suspend fun getAllTopics(author: AuthorDto): Flow<TopicDto> {
        return topicRepository.findAllByAuthorId(author.id)
            .map { asTopicDto(it, author) }
    }

    override suspend fun deleteTopic(author: AuthorDto, title: String): Boolean {
        val topic = topicRepository.findByAuthorIdAndTitle(author.id, title)
            ?: return false
        topicRepository.delete(topic)
        return true
    }

    override suspend fun deleteAllTopics(author: AuthorDto) {
        topicRepository.deleteAllByAuthorId(author.id)
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
        topic: TopicDto,
        title: String,
        tags: List<String>,
        content: String,
    ): PostDto {
        val existing = postRepository.findByTopicIdAndTitle(topic.id, title)
        if (existing != null) return asPostDto(existing, topic, tags)
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
        return asPostDto(post, topic, tags)
    }

    @Transactional(readOnly = true)
    override suspend fun getPost(topic: TopicDto, title: String): PostDto? {
        val post = postRepository.findByTopicIdAndTitle(topic.id, title) ?: return null
        val tags = getAllTagsByPostId(post.id!!)
        return asPostDto(post, topic, tags)
    }

    private suspend fun getAllTagsByPostId(postId: Long): List<String> {
        return postTagRepository.findAllByPostId(postId)
            .map { (_, tagId) ->
                tagRepository.findById(tagId)
            }
            .filterNotNull()
            .map { it.name }
            .toList()
    }

    @Transactional(readOnly = true)
    override suspend fun getAllPosts(topic: TopicDto): Flow<PostDto> {
        return postRepository.findAllByTopicId(topic.id)
            .map { asPostDto(it, topic, getAllTagsByPostId(it.id!!)) }
    }

    @Transactional(readOnly = true)
    override suspend fun getAllPostsByTag(topic: TopicDto, tagName: String): Flow<PostDto> {
        val tag = createOrGetTag(tagName)
        return getAllPostTags(tag.id!!)
            .map { (postId, _) ->
                postRepository.findById(postId)
            }
            .filterNotNull()
            .map { asPostDto(it, topic, getAllTagsByPostId(it.id!!)) }
    }

    override suspend fun deletePost(topic: TopicDto, title: String): Boolean {
        val post = postRepository.findByTopicIdAndTitle(topic.id, title)
            ?: return false
        postRepository.delete(post)
        return true
    }

    override suspend fun deleteAllPosts(topic: TopicDto) {
        postRepository.deleteAllByTopicId(topic.id)
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
