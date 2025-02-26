package org.example.gongiklifeclientbegraphql.dto.community.comments;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsRequestDto {

    @NotBlank
    private String postId;

    public CommentsRequest toCommentsRequestProto() {
        return CommentsRequest.newBuilder()
                .setPostId(postId)
                .build();
    }
}
