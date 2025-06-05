package org.example.mdblog.controller

import kotlinx.coroutines.flow.Flow
import org.example.mdblog.dto.AuthorDto
import org.example.mdblog.dto.PostDto
import org.example.mdblog.dto.TopicDto
import org.example.mdblog.service.AuthorService
import org.example.mdblog.service.PostService
import org.example.mdblog.service.TopicService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/{login}")
class AuthorController(private val authorService: AuthorService) {

    @GetMapping
    suspend fun getAuthor(
        @PathVariable login: String
    ): AuthorDto =
        authorService.getAuthor(login) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Author with login $login not found"
        )

    @PostMapping
    suspend fun createAuthor(
        @PathVariable login: String,
        @RequestParam name: String
    ): AuthorDto = authorService.createOrGetAuthor(login, name)

    @DeleteMapping
    suspend fun deleteAuthor(
        @PathVariable login: String
    ): Boolean = authorService.deleteAuthor(login)
}

@RestController
@RequestMapping("/{login}/topics")
class TopicController(
    private val authorController: AuthorController,
    private val topicService: TopicService
) {

    @GetMapping
    suspend fun getAllTopics(
        @PathVariable login: String
    ): Flow<TopicDto> =
        authorController.getAuthor(login).let { topicService.getAllTopics(it) }

    @PostMapping
    suspend fun createTopic(
        @PathVariable login: String,
        @RequestParam title: String
    ): TopicDto = authorController.getAuthor(login).let {
        topicService.createOrGetTopic(it, title)
    }

    @DeleteMapping("/{title}")
    suspend fun deleteTopic(
        @PathVariable login: String,
        @PathVariable title: String
    ): Boolean = authorController.getAuthor(login).let {
        topicService.deleteTopic(it, title)
    }

    @DeleteMapping
    suspend fun deleteAllTopics(
        @PathVariable login: String
    ) {
        authorController.getAuthor(login).let {
            topicService.deleteAllTopics(it)
        }
    }
}

@RestController
@RequestMapping("/{login}/{title}/posts")
class PostController(
    private val topicController: TopicController,
    private val postService: PostService
) {

    @GetMapping
    suspend fun getAllPosts(
        @PathVariable login: String,
        @PathVariable title: String,
        @RequestParam tag: String?
    ): Flow<PostDto> =
        topicController.createTopic(login, title).let { topic ->
            tag?.let {
                postService.getAllPostsByTag(topic, it)
            } ?: postService.getAllPosts(topic)
        }

    @GetMapping("/{postTitle}")
    suspend fun getPost(
        @PathVariable login: String,
        @PathVariable title: String,
        @PathVariable postTitle: String
    ): PostDto =
        topicController.createTopic(login, title).let { topic ->
            postService.getPost(topic, postTitle) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Post with title $postTitle not found"
            )
        }

    @PostMapping
    suspend fun createPost(
        @PathVariable login: String,
        @PathVariable title: String,
        @RequestParam postTitle: String,
        @RequestParam tags: List<String>,
        @RequestBody content: String
    ): PostDto =
        topicController.createTopic(login, title).let { topic ->
            postService.createOrGetPost(topic, postTitle, tags, content)
        }

    @DeleteMapping("/{postTitle}")
    suspend fun deletePost(
        @PathVariable login: String,
        @PathVariable title: String,
        @PathVariable postTitle: String
    ): Boolean =
        topicController.createTopic(login, title).let { topic ->
            postService.deletePost(topic, postTitle)
        }

    @DeleteMapping
    suspend fun deleteAllPosts(
        @PathVariable login: String,
        @PathVariable title: String
    ) {
        topicController.createTopic(login, title).let { topic ->
            postService.deleteAllPosts(topic)
        }
    }
}
