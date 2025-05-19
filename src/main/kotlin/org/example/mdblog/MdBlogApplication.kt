package org.example.mdblog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MdBlogApplication

fun main(args: Array<String>) {
    runApplication<MdBlogApplication>(*args)
}
