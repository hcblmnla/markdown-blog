package org.example.mdblog.kafka.consumer

import org.example.mdblog.dto.PostDto
import org.springframework.kafka.annotation.KafkaListener

class PostConsumer {

    private val consumed: ArrayList<PostDto> = ArrayList()
    private val consumedPrivate: ArrayList<PostDto> = ArrayList()

    val allPosts: List<PostDto>
        get() = consumed

    val privatePosts: List<PostDto>
        get() = consumedPrivate

    @KafkaListener(
        topics = ["\${kafka.topics.all-posts}"],
        groupId = "all-posts-group",
        containerFactory = "postListenerContainerFactory"
    )
    fun consumeAllPosts(post: PostDto) = consumed.add(post)

    @KafkaListener(
        topics = ["\${kafka.topics.private-posts}"],
        groupId = "private-posts-group",
        containerFactory = "postListenerContainerFactory"
    )
    fun consumePrivatePosts(post: PostDto) = consumedPrivate.add(post)
}
