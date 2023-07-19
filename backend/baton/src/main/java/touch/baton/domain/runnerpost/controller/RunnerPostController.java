package touch.baton.domain.runnerpost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import touch.baton.domain.runnerpost.service.RunnerPostService;
import touch.baton.domain.runnerpost.service.dto.RunnerPostUpdateRequest;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/runner")
@RestController
public class RunnerPostController {

    private final RunnerPostService runnerPostService;

    @PutMapping("/{runnerPostId}")
    public ResponseEntity<Void> update(@PathVariable(name = "runnerPostId") final Long id,
                                       @RequestBody final RunnerPostUpdateRequest request
    ) {
        Long updatedId = runnerPostService.update(id, request);
        return ResponseEntity.created(URI.create("/api/v1/posts/runner/" + updatedId)).build();
    }
}
