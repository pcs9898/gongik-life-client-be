package org.example.gongiklifeclientbegraphql.dto.community.deletePost;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletePostRequestDto {

    private String userId;

    @NotBlank
    private String postId;

    public DeletePostRequest toDeletePostRequestProto() {
        return DeletePostRequest.newBuilder()
                .setUserId(userId)
                .setPostId(postId)
                .build();
    }
}
