package org.example.mdblog

import org.example.mdblog.kafka.KafkaConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
@EnableConfigurationProperties(KafkaConfig::class)
class MdBlogApplication

fun main(args: Array<String>) {
    runApplication<MdBlogApplication>(*args)
}
