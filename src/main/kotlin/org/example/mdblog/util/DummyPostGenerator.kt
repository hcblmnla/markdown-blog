package org.example.mdblog.util

import org.example.mdblog.dto.AuthorDto
import org.example.mdblog.dto.PostDto
import org.example.mdblog.dto.TopicDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

@Service
class DummyPostGenerator {
    private val counter = AtomicInteger()

    suspend fun nextPost(): PostDto {
        val id = counter.incrementAndGet().toLong()
        return PostDto(
            id = id,
            TopicDto(
                id = id,
                author = AuthorDto(
                    id,
                    "alnm_test${id}",
                    "Daniil Serov"
                ),
                title = "title"
            ),
            title = "title_test${id}",
            tags = listOf(),
            content = "abc abc some text here test${id}",
            updatedAt = LocalDateTime.now(),
        )
    }
}
