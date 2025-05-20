package org.example.mdblog.service

import org.example.mdblog.repository.*
import org.springframework.stereotype.Service

@Service
class BlogService(
    private val authorRepository: AuthorRepository,
    private val topicRepository: TopicRepository,
    private val postRepository: PostRepository,
    private val tagRepository: TagRepository,
    private val postTagRepository: PostTagRepository,
)
