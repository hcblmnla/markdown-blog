# Markdown blog

## Simple API

- REST for development speed
- idempotent `POST`
- no migrations, no `N+1` fixes due to laziness

## Async

Using `CoroutineCrudRepository` from Spring Boot Data R2DBC

## TODO

- [ ] markdown rendering (parsing to HTML)
- [x] Kafka supporting (using only [testcontainers](src/test/kotlin/org/example/mdblog/kafka/KafkaIntegrationTest.kt))
- [ ] Kafka streams supporting
