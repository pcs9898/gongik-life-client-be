package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletePostService {

    private final PostRepository postRepository;

    @Transactional
    public CommunityServiceOuterClass.DeletePostResponse deletePost(CommunityServiceOuterClass.DeletePostRequest request) {
        Post post = postRepository.findByIdAndUserId(
                        UUID.fromString(request.getPostId()), UUID.fromString(request.getUserId()))
                .orElseThrow(
                        () -> Status.NOT_FOUND.withDescription("Post not found, or maybe already deleted")
                                .asRuntimeException());

        post.setDeletedAt(new Date());

        postRepository.save(post);

        return CommunityServiceOuterClass.DeletePostResponse.newBuilder().setSuccess(true).build();
    }
}
