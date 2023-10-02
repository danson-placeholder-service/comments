package dev.danvega.danson.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CommentDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CommentDataLoader.class);
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;

    public CommentDataLoader(ObjectMapper objectMapper, CommentRepository commentRepository) {
        this.objectMapper = objectMapper;
        this.commentRepository = commentRepository;
    }

    @Override
    public void run(String... args) {
        if(commentRepository.count() == 0){
            String COMMENTS_JSON = "/data/comments.json";
            log.info("Loading posts into database from JSON: {}", COMMENTS_JSON);
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(COMMENTS_JSON)) {
                Comments response = objectMapper.readValue(inputStream, Comments.class);
                commentRepository.saveAll(response.comments());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        }
    }

}
