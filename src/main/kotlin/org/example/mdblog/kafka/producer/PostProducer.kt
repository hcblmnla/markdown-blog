package org.example.mdblog.kafka.producer

import org.example.mdblog.dto.PostDto
import org.example.mdblog.kafka.KafkaConfig
import org.example.mdblog.util.DummyPostGenerator
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

class PostProducer(
    private val kafkaTemplate: KafkaTemplate<String, PostDto>,
    private val topics: KafkaConfig.KafkaTopics,
    private val postGenerator: DummyPostGenerator
) {

    private val log = LoggerFactory.getLogger(PostProducer::class.java)

    private suspend fun sendPost(topic: String, loggingName: String) {
        val post = postGenerator.nextPost()
        val stringId = post.id.toString()

        kafkaTemplate.send(topic, stringId, post)
        log.info("{} post with id={} sent", loggingName, stringId)
    }

    suspend fun sendPrivatePost() = sendPost(topics.privatePosts, "Private")

    suspend fun sendPublicPost() = sendPost(topics.publicPosts, "Public")
}
