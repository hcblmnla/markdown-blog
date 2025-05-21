package org.example.mdblog.dto

import org.example.mdblog.entity.Author
import org.example.mdblog.entity.Post
import org.example.mdblog.entity.Topic

fun asAuthorDto(author: Author) = AuthorDto(
    id = author.id!!,
    login = author.login,
    name = author.name,
)

fun asTopicDto(topic: Topic, authorDto: AuthorDto) = TopicDto(
    id = topic.id!!,
    author = authorDto,
    title = topic.title,
)

fun asPostDto(post: Post, topicDto: TopicDto, tags: List<String>) = PostDto(
    id = post.id!!,
    topic = topicDto,
    title = post.title,
    tags = tags,
    content = post.content,
    updatedAt = post.updatedAt,
)
