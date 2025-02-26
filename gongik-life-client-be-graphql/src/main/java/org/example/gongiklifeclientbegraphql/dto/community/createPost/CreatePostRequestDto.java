package org.example.gongiklifeclientbegraphql.dto.community.createPost;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CreatePostRequestDto {


    private String userId;


    @Range(min = 1, max = 6, message = "카테고리 아이디는 1~6 사이여야 합니다.")
    private int categoryId;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    public CreatePostRequest toCreatePostRequestProto() {
        return CreatePostRequest.newBuilder()
                .setUserId(userId)
                .setCategoryId(categoryId)
                .setTitle(title)
                .setContent(content)
                .build();
    }
}
