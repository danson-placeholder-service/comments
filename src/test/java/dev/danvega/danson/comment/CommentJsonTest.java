package dev.danvega.danson.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentJsonTest {

    @Autowired
    JacksonTester<Comment> jacksonTester;

    @Test
    void shouldSerializeComment() throws IOException {
        var comment = new Comment(1,1,"TEST_NAME","test@gmail.com","TEST_BODY",null);
        var expected = STR."""
                {
                    "id": \{comment.id()},
                    "postId": \{comment.postId()},
                    "name": "\{comment.name()}",
                    "email": "\{comment.email()}",
                    "body": "\{comment.body()}",
                    "version": null
                }
                """;

        assertThat(jacksonTester.write(comment)).isEqualToJson(expected);
    }

    @Test
    void shouldDeserializeComment() throws Exception {
        var comment = new Comment(1,1,"TEST_NAME","test@gmail.com","TEST_BODY",null);
        String response = STR."""
                {
                    "id": \{comment.id()},
                    "postId": \{comment.postId()},
                    "name": "\{comment.name()}",
                    "email": "\{comment.email()}",
                    "body": "\{comment.body()}",
                    "version": null
                }
                """;

        assertThat(jacksonTester.parseObject(response).id()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(response).postId()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(response).name()).isEqualTo("TEST_NAME");
        assertThat(jacksonTester.parseObject(response).email()).isEqualTo("test@gmail.com");
        assertThat(jacksonTester.parseObject(response).body()).isEqualTo("TEST_BODY");
        assertThat(jacksonTester.parseObject(response).version()).isEqualTo(null);
    }

}