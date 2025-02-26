package org.example.gongiklifeclientbecommunityservice.service.post;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.example.gongiklifeclientbecommunityservice.respository.PostRepository;
import org.example.gongiklifeclientbecommunityservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public CommunityServiceOuterClass.CreatePostResponse createPost(CommunityServiceOuterClass.CreatePostRequest request) {

        String userName = userService.getUserNameById(request.getUserId());

        return postRepository.save(Post.fromCreatePostRequestProto(request))
                .toCreatePostResponseProto(userName);
    }
}
