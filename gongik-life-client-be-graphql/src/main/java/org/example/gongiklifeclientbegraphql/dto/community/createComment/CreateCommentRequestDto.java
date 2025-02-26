package org.example.gongiklifeclientbegraphql.dto.community.createComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequestDto {

    private String userId;

    @NotBlank
    private String postId;


    private String parentCommentId;

    @NotBlank
    private String content;

    public CreateCommentRequest toCreateCommentRequestProto() {
        CreateCommentRequest.Builder builder = CreateCommentRequest.newBuilder()
                .setUserId(userId)
                .setPostId(postId)
                .setContent(content);

        if (parentCommentId != null) {
            builder.setParentCommentId(parentCommentId);
        }

        return builder.build();
    }
}
