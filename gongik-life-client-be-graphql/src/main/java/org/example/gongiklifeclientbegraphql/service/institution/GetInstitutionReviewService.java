package org.example.gongiklifeclientbegraphql.service.institution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetInstitutionReviewService {

    private final InstitutionCacheService institutionCacheService;
    private final InstitutionService institutionService;

    public InstitutionReviewResponseDto institutionReview(InstitutionReviewRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("GetInstitutionReviewService", () -> {
            InstitutionReviewResponseDto institutionReview = institutionCacheService.getInstitutionReview(
                    requestDto.getInstitutionReviewId());

            if (requestDto.getUserId() != null) {

                boolean isLiked = institutionService.isLikedInstitutionReview(
                        requestDto.getInstitutionReviewId(),
                        requestDto.getUserId());
                institutionReview.setIsLiked(isLiked);
            }

            return institutionReview;
        });
    }
}
