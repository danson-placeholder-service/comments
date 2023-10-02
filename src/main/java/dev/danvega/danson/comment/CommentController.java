package dev.danvega.danson.comment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository repository;

    public CommentController(CommentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    List<Comment> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Comment> findById(@PathVariable Integer id) {
        return Optional.ofNullable(repository.findById(id).orElseThrow(CommentNotFoundException::new));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Comment save(@RequestBody @Valid Comment comment) {
        return repository.save(comment);
    }

    @PutMapping("/{id}")
    Comment update(@PathVariable Integer id, @RequestBody Comment comment) {
        Optional<Comment> existing = repository.findById(id);
        if(existing.isPresent()) {
            Comment updated = new Comment(
                    id,
                    comment.postId(),
                    comment.name(),
                    comment.email(),
                    comment.body(),
                    comment.version());
            return repository.save(updated);
        } else {
            throw new CommentNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }

}
