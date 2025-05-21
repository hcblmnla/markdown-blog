package org.example.mdblog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@SpringBootApplication
@EnableJdbcRepositories
class MdBlogApplication

fun main(args: Array<String>) {
    runApplication<MdBlogApplication>(*args)
}
