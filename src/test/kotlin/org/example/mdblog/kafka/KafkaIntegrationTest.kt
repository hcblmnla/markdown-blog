package org.example.mdblog.kafka

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.example.mdblog.kafka.consumer.PostConsumer
import org.example.mdblog.kafka.producer.PostProducer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootTest
@TestPropertySource(properties = ["kafka.enabled=true"])
@Testcontainers
class KafkaIntegrationTest {

    companion object {
        @Container
        val kafka = KafkaContainer("apache/kafka-native:3.8.1")

        @JvmStatic
        @DynamicPropertySource
        @Suppress("unused")
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers)
        }

        init {
            kafka.start()
        }
    }

    @Autowired
    private lateinit var postProducer: PostProducer

    @Autowired
    private lateinit var postConsumer: PostConsumer

    @Test
    fun `correctness delivering test`() = runTest {
        repeat(3) {
            postProducer.sendPrivatePost()
            postProducer.sendPublicPost()
        }

        await().atMost(5.seconds.toJavaDuration()).until {
            postConsumer.allPosts.size == 6 && postConsumer.privatePosts.size == 3
        }

        with(postConsumer) {
            assertThat(allPosts)
                .hasSize(6)
                .allSatisfy { post ->
                    assertThat(post.id).isEqualTo(post.topic.id)
                    assertThat(post.topic.author.login).startsWith("alnm_test")
                    assertThat(post.topic.author.name).isEqualTo("Daniil Serov")
                    assertThat(post.title).startsWith("title_test")
                    assertThat(post.content).contains("some text here")
                }

            assertThat(privatePosts)
                .hasSize(3)
                .allSatisfy { post ->
                    assertThat(allPosts).contains(post)
                }

            val allIds = allPosts.map { it.id }
            assertThat(allIds).doesNotHaveDuplicates()
        }
    }
}
