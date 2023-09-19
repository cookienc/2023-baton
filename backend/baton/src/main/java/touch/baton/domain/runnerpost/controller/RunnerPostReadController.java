package touch.baton.domain.runnerpost.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import touch.baton.domain.common.response.PageResponse;
import touch.baton.domain.runnerpost.RunnerPost;
import touch.baton.domain.runnerpost.controller.response.RunnerPostResponse;
import touch.baton.domain.runnerpost.service.RunnerPostReadService;
import touch.baton.domain.runnerpost.service.RunnerPostService;
import touch.baton.domain.runnerpost.vo.ReviewStatus;

import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/runner")
@RestController
public class RunnerPostReadController {

    private final RunnerPostReadService runnerPostReadService;
    private final RunnerPostService runnerPostService;

    @GetMapping("/tags/search")
    public ResponseEntity<PageResponse<RunnerPostResponse.Simple>> readRunnerPostsByTagNamesAndReviewStatus(
            @PageableDefault(sort = "id", direction = DESC) final Pageable pageable,
            @Nullable @RequestParam(required = false) final String tagName,
            @RequestParam final ReviewStatus reviewStatus
    ) {
        final Page<RunnerPost> pageRunnerPosts = getPageRunnerPosts(pageable, tagName, reviewStatus);

        final List<Long> foundRunnerPostIds = pageRunnerPosts.stream()
                .map(RunnerPost::getId)
                .toList();

        final List<Long> foundApplicantCounts = runnerPostReadService.readApplicantCountsByRunnerPostIds(foundRunnerPostIds);
        final List<RunnerPostResponse.Simple> responses = IntStream.range(0, foundApplicantCounts.size())
                .mapToObj(index -> {
                            final RunnerPost current = pageRunnerPosts.getContent().get(index);
                            final Long applicantCount = foundApplicantCounts.get(index);

                            return RunnerPostResponse.Simple.from(current, applicantCount);
                        }
                ).toList();

        final PageImpl<RunnerPostResponse.Simple> pageResponse
                = new PageImpl<>(responses, pageable, pageRunnerPosts.getTotalElements());

        return ResponseEntity.ok(PageResponse.from(pageResponse));
    }

    private Page<RunnerPost> getPageRunnerPosts(final Pageable pageable, final String tagName, final ReviewStatus reviewStatus) {
        if (tagName == null || tagName.isBlank()) {
            return runnerPostService.readRunnerPostsByReviewStatus(pageable, reviewStatus);
        }

        return runnerPostReadService.readRunnerPostByTagNameAndReviewStatus(pageable, tagName, reviewStatus);
    }
}
