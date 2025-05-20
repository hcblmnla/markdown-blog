package org.example.mdblog.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("author")
data class Author(
    @Id val id: Long? = null,
    val login: String,
    val name: String
)

@Table("topic")
data class Topic(
    @Id val id: Long? = null,
    @Column("author_id")
    val authorId: Long,
    val title: String
)

@Table("post")
data class Post(
    @Id val id: Long? = null,
    @Column("topic_id")
    val topicId: Long,
    val title: String,
    val content: String,
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Table("tag")
data class Tag(
    @Id val id: Long? = null,
    val name: String
)

@Table("post_tags")
data class PostTag(
    @Column("post_id")
    val postId: Long,
    @Column("tag_id")
    val tagId: Long
)
