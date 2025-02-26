package org.example.gongiklifeclientbegraphql.dto.community.deleteComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeleteCommentRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCommentRequestDto {

    private String userId;

    @NotBlank
    private String commentId;

    public DeleteCommentRequest toDeleteCommentRequestProto() {
        return DeleteCommentRequest.newBuilder()
                .setUserId(userId)
                .setCommentId(commentId)
                .build();
    }
}
