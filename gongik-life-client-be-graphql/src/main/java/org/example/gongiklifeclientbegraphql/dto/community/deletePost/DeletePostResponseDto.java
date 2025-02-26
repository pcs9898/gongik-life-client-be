package org.example.gongiklifeclientbegraphql.dto.community.deletePost;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletePostResponseDto {

    private String postId;
    private Boolean success;

    public static DeletePostResponseDto fromDeletePostResponseProto(DeletePostResponse proto, String postId) {
        return DeletePostResponseDto.builder()
                .postId(postId)
                .success(proto.getSuccess())
                .build();
    }

}
