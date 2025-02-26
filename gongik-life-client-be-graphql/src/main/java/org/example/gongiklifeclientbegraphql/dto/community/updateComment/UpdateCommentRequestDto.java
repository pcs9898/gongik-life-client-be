package org.example.gongiklifeclientbegraphql.dto.community.updateComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequestDto {

    private String userId;

    @NotBlank
    private String commentId;

    @NotBlank
    private String content;

    public UpdateCommentRequest toUpdateCommentRequestProto() {
        return UpdateCommentRequest.newBuilder()
                .setUserId(userId)
                .setCommentId(commentId)
                .setContent(content)
                .build();
    }
}
