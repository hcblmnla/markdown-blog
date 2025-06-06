package org.example.mdblog

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
data class MdBlogConfig(val kafka: KafkaConfig) {

    data class KafkaConfig(val enabled: Boolean)
}
