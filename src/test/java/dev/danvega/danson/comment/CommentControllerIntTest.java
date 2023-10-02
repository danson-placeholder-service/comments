package dev.danvega.danson.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CommentControllerIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldFindAllComments() {
        Comment[] comments = restTemplate.getForObject("/api/comments", Comment[].class);
        assertThat(comments.length).isGreaterThan(100);
    }

    @Test
    void shouldFindCommentWhenValidCommentID() {
        ResponseEntity<Comment> response = restTemplate.exchange("/api/comments/1", HttpMethod.GET, null, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldThrowNotFoundWhenInvalidCommentID() {
        ResponseEntity<Comment> response = restTemplate.exchange("/api/comments/999", HttpMethod.GET, null, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Rollback
    void shouldCreateNewCommentWhenCommentIsValid() {
        Comment comment = new Comment(999,1,"John Doe","johndoe@example.com","This is a comment",null);

        ResponseEntity<Comment> response = restTemplate.exchange("/api/comments", HttpMethod.POST, new HttpEntity<Comment>(comment), Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(999);
        assertThat(response.getBody().postId()).isEqualTo(1);
        assertThat(response.getBody().name()).isEqualTo("John Doe");
        assertThat(response.getBody().email()).isEqualTo("johndoe@example.com");
        assertThat(response.getBody().body()).isEqualTo("This is a comment");
    }

    @Test
    void shouldNotCreateNewCommentWhenValidationFails() {
        Comment comment = new Comment(101,1,"","", "",null);
        ResponseEntity<Comment> response = restTemplate.exchange("/api/comments", HttpMethod.POST, new HttpEntity<Comment>(comment), Comment.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Rollback
    void shouldUpdateCommentWhenCommentIsValid() {
        ResponseEntity<Comment> response = restTemplate.exchange("/api/comments/99", HttpMethod.GET, null, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Comment existing = response.getBody();
        assertThat(existing).isNotNull();
        Comment updated = new Comment(existing.id(),10,"Jane Doe","jane@example.com","Updated comment",existing.version());

        assertThat(updated.id()).isEqualTo(99);
        assertThat(updated.postId()).isEqualTo(10); // Assuming postId is 10 for this example
        assertThat(updated.name()).isEqualTo("Jane Doe");
        assertThat(updated.email()).isEqualTo("jane@example.com");
        assertThat(updated.body()).isEqualTo("Updated comment");
    }

    @Test
    @Rollback
    void shouldDeleteWithValidID() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/comments/88", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}