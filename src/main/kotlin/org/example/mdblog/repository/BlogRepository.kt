package org.example.mdblog.repository

import org.example.mdblog.entity.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : CoroutineCrudRepository<Author, Long>

@Repository
interface TopicRepository : CoroutineCrudRepository<Topic, Long>

@Repository
interface PostRepository : CoroutineCrudRepository<Post, Long>

@Repository
interface TagRepository : CoroutineCrudRepository<Tag, Long>

@Repository
interface PostTagRepository : CoroutineCrudRepository<PostTag, Long>
