package org.example.mdblog.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.example.mdblog.dto.PostDto
import org.example.mdblog.kafka.consumer.PostConsumer
import org.example.mdblog.kafka.producer.PostProducer
import org.example.mdblog.util.DummyPostGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@ConfigurationProperties(prefix = "kafka", ignoreUnknownFields = false)
@ConditionalOnProperty(prefix = "kafka", name = ["enabled"], havingValue = "true")
data class KafkaConfig(
    val enabled: Boolean,
    @get:Bean val topics: KafkaTopics,
    val partitions: Int,
    val replicas: Int,
) {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Autowired
    private lateinit var postGenerator: DummyPostGenerator

    private fun topic(name: String): NewTopic = TopicBuilder
        .name(name)
        .partitions(partitions)
        .replicas(replicas)
        .build()

    @Bean
    fun privatePostsTopic(): NewTopic = topic(topics.privatePosts)

    @Bean
    fun publicPostsTopic(): NewTopic = topic(topics.publicPosts)

    @Bean
    fun producerFactory(): ProducerFactory<String, PostDto> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaPostTemplate(): KafkaTemplate<String, PostDto> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, PostDto> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "org.example.mdblog.kafka",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
        )
        return DefaultKafkaConsumerFactory(
            configProps,
            StringDeserializer(),
            JsonDeserializer(PostDto::class.java)
        )
    }

    @Bean
    fun postListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, PostDto> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, PostDto>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        return factory
    }

    @Bean
    fun postProducer(): PostProducer = PostProducer(kafkaPostTemplate(), topics, postGenerator)

    @Bean
    fun postConsumer(): PostConsumer = PostConsumer()

    class KafkaTopics(
        val privatePosts: String,
        val publicPosts: String,
    )
}
