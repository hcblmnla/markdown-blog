package org.example.mdblog.kafka.consumer

import org.example.mdblog.dto.PostDto
import org.springframework.kafka.annotation.KafkaListener

class PostConsumer {

    companion object {
        const val PRIVATE = "local.private.posts"
        const val PUBLIC = "local.public.posts"
    }

    private val consumed: ArrayList<PostDto> = ArrayList()
    private val consumedPrivate: ArrayList<PostDto> = ArrayList()

    val allPosts: List<PostDto>
        get() = consumed

    val privatePosts: List<PostDto>
        get() = consumedPrivate

    @KafkaListener(
        topics = [PRIVATE, PUBLIC],
        groupId = "all-posts-group",
        containerFactory = "postListenerContainerFactory"
    )
    fun consumeAllPosts(post: PostDto) = consumed.add(post)

    @KafkaListener(
        topics = [PRIVATE],
        groupId = "private-posts-group",
        containerFactory = "postListenerContainerFactory"
    )
    fun consumePrivatePosts(post: PostDto) = consumedPrivate.add(post)
}
