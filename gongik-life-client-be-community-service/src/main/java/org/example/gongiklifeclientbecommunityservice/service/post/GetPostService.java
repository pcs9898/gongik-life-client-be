package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public CommunityServiceOuterClass.GetPostResponse getPost(CommunityServiceOuterClass.GetPostRequest request) {
        Post post = postRepository.findById(UUID.fromString(request.getPostId()))
                .orElseThrow(() -> Status.NOT_FOUND.withDescription("Post not found").asRuntimeException());

        String userName = userService.getUserNameById(post.getUserId().toString());

        return post.toGetPostResponseProto(userName);
    }
}
