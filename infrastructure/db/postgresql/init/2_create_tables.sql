\c gongik_life_client_user_db;

-- 사용자 기본 정보
CREATE TABLE users
(
    id            UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    email         VARCHAR(255) UNIQUE NOT NULL,
    is_active     BOOLEAN             NOT NULL DEFAULT true,
    created_at    TIMESTAMPTZ                    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ                    DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMPTZ
);


CREATE TABLE user_login_histories
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID NOT NULL REFERENCES users (id),
    last_login_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address    VARCHAR(45), -- IPv4 및 IPv6 주소를 모두 지원
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 인증 유형
CREATE TABLE auth_types (
                            id SERIAL PRIMARY KEY,
                            auth_type_name VARCHAR(50) NOT NULL
);

INSERT INTO auth_types (auth_type_name)
VALUES
    ('EMAIL'),
    ('GOOGLE'),
    ('KAKAO'),
    ('NAVER');


-- 사용자 인증 정보
CREATE TABLE user_auths
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID      NOT NULL REFERENCES users (id),
    auth_type_id  INT       NOT NULL REFERENCES auth_types (id),
    password_hash VARCHAR(255),
    created_at    TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT fk_user_auth UNIQUE (user_id, auth_type_id),
    CONSTRAINT valid_auth_data CHECK (
        (auth_type_id = 1 AND password_hash IS NOT NULL ) OR
        (auth_type_id IN (2, 3, 4) AND  password_hash IS NULL)
        )
);



-- 사용자 프로필
CREATE TABLE user_profiles
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL REFERENCES users (id),
    institution_id UUID,
    name            VARCHAR(30) NOT NULL,
    bio             VARCHAR(30) ,
    enlistment_date DATE ,
    discharge_date  DATE ,
    created_at      TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT fk_user_profile UNIQUE (user_id),
    CONSTRAINT valid_service_date_range CHECK (discharge_date >= enlistment_date),
    CONSTRAINT institution_dates_not_null CHECK (
        institution_id IS NULL OR (enlistment_date IS NOT NULL AND discharge_date IS NOT NULL)
        )
);


-- 휴가 유형
CREATE TABLE vacation_types (
                                id SERIAL PRIMARY KEY,
                                type_name VARCHAR(50) NOT NULL
);

INSERT INTO vacation_types (type_name)
VALUES
    ('ANNUAL_FIRST_YEAR'),  -- 1년차 연가
    ('ANNUAL_SECOND_YEAR'), -- 2년차 연가
    ('SICK'),               -- 병가
    ('PETITION'),           -- 청원휴가
    ('PUBLIC'),             -- 공가
    ('OTHER');              -- 기타

-- 휴가 정책
CREATE TABLE vacation_policies
(
    id SERIAL PRIMARY KEY,
    vacation_type_id          INT NOT NULL REFERENCES vacation_types (id),
    minutes_per_year INT           NOT NULL,
    created_at       TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP
);

-- 휴가 정책 데이터 삽입
INSERT INTO vacation_policies (vacation_type_id, minutes_per_year, created_at, updated_at)
VALUES
    (1, 15 * 8 * 60, now(), now()),  -- 1년차 연가: 15일 * 8시간 * 60분
    (2, 13 * 8 * 60, now(), now()),  -- 2년차 연가: 13일 * 8시간 * 60분
    (3, 30 * 8 * 60, now(), now());  -- 병가: 30일 * 8시간 * 60분



-- 사용자별 휴가 할당량
CREATE TABLE user_vacation_balances
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID NOT NULL REFERENCES users (id),
    vacation_type_id           INT NOT NULL REFERENCES vacation_types (id),
    remaining_minutes INT NOT NULL,
    valid_from_date   DATE NOT NULL,
    valid_until_date  DATE NOT NULL,
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMPTZ,
    UNIQUE (user_id, vacation_type_id),
    CONSTRAINT valid_vacation_balance_range CHECK (valid_until_date >= valid_from_date),
    CONSTRAINT valid_vacation_type_id CHECK (vacation_type_id BETWEEN 1 AND 3)
);


-- 휴가 사용 기록
CREATE TABLE user_vacation_records
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users (id),
    vacation_type_id       INT NOT NULL REFERENCES vacation_types (id),
    start_datetime      TIMESTAMPTZ NOT NULL,
    end_datetime        TIMESTAMPTZ NOT NULL,
    minutes_used        INT NOT NULL,
    transport_allowance BOOLEAN DEFAULT FALSE,
    meal_allowance      BOOLEAN DEFAULT FALSE,
    reason              TEXT,
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMPTZ,
    CONSTRAINT valid_vacation_date_range CHECK (end_datetime >= start_datetime),
    CONSTRAINT valid_minutes_used CHECK (minutes_used > 0)
);


-- Soft Delete를 위한 인덱스
CREATE INDEX idx_user_vacation_records_user_type
    ON user_vacation_records (user_id, vacation_type_id) WHERE deleted_at IS NULL;

CREATE INDEX idx_user_auths_user_id_auth_type_id
    ON user_auths (user_id, auth_type_id) where deleted_at IS NULL;

CREATE INDEX idx_user_vacation_balances_user_id_vacation_type_id
    ON user_vacation_balances (user_id, vacation_type_id) where deleted_at IS NULL;

CREATE INDEX idx_user_vacation_records_user_id_vacation_type_id
    ON user_vacation_records (user_id, vacation_type_id) where deleted_at IS NULL;


\c gongik_life_client_institution_db;


-- 기관 카테고리
CREATE TABLE institution_categories (
                                        id SERIAL PRIMARY KEY,                 -- 고유 ID
                                        category_name VARCHAR(50) NOT NULL   -- 카테고리 이름
);

INSERT INTO institution_categories (category_name)
VALUES
    ('SOCIAL_WELFARE'), -- 사회복지시설
    ('PUBLIC_ORGANIZATION'), -- 공공단체
    ('NATIONAL_AGENCY'), -- 국가기관
    ('LOCAL_GOVERNMENT'); -- 지방자치단체


-- 기관 태그
CREATE TABLE institution_tags (
                                  id SERIAL PRIMARY KEY,                 -- 고유 ID
                                  tag_name VARCHAR(50) NOT NULL       -- 태그 이름
);

INSERT INTO institution_tags (tag_name)
VALUES
    ('ELDERLY_WELFARE_FACILITY'), -- 노인복지시설
    ('CHILD_WELFARE_FACILITY'), -- 아동복지시설
    ('DISABLED_WELFARE_FACILITY'), -- 장애인복지시설
    ('WELFARE_CENTER'), -- 복지관
    ('LOCAL_RESIDENT_FACILITY'), -- 지역주민시설
    ('DAYCARE_CENTER'), -- 보육시설
    ('SELF_SUFFICIENCY_CENTER'), -- 자활시설
    ('MENTAL_HEALTH_FACILITY'), -- 정신보건시설
    ('WOMEN_WELFARE_FACILITY'), -- 여성복지시설
    ('YOUTH_WELFARE_FACILITY'), -- 청소년복지시설
    ('HOMELESS_WELFARE_FACILITY'), -- 노숙인복지시설
    ('TUBERCULOSIS_HANSEN_FACILITY'); -- 결핵한센시설


-- 기관 질병 제한
CREATE TABLE disease_restrictions (
                                      id SERIAL PRIMARY KEY,                 -- 고유 ID
                                      disease_name VARCHAR(50) NOT NULL   -- 질병 이름
);

INSERT INTO disease_restrictions (disease_name)
VALUES
    ('MENTAL_ILLNESS'), -- 정신과질환
    ('SEIZURE'), -- 경련성
    ('TATTOO_SELF_HARM'), -- 문신자해
    ('SPINE_DISEASE'), -- 척추질환
    ('BRONCHIAL_ASTHMA'), -- 기관지천식
    ('ATOPIC_DERMATITIS'); -- 아토피피부염


-- 관할병무청
CREATE TABLE regional_military_offices (
                                           id SERIAL PRIMARY KEY,                 -- 고유 ID
                                           office_name VARCHAR(50) NOT NULL   -- 관할병무청 이름
);

INSERT INTO regional_military_offices (office_name)
VALUES
    ('SEOUL'), -- 서울지방병무청
    ('BUSAN_ULSAN'), -- 부산울산지방병무청
    ('DAEGU_GYEONGBUK'), -- 대구경북지방병무청
    ('GYEONGIN'), -- 경인지방병무청
    ('GWANGJU_JEONNAM'), -- 광주전남지방병무청
    ('DAEJEON_CHUNGNAM'), -- 대전충남지방병무청
    ('GANGWON'), -- 강원지방병무청
    ('CHUNGBUK'), -- 충북지방병무청
    ('JEONBUK'), -- 전북지방병무청
    ('GYEONGNAM'), -- 경남지방병무청
    ('JEJU'), -- 제주지방병무청
    ('INCHEON'), -- 인천병무지청
    ('GYEONGGI_NORTH'), -- 경기북부병무지청
    ('GANGWON_YEONGDONG'); -- 강원영동병무지청




-- 근무형태
CREATE TABLE work_type_rules (
                                 id SERIAL PRIMARY KEY,                 -- 고유 ID
                                 work_type_name VARCHAR(50) NOT NULL    -- 근무형태 이름
);

INSERT INTO work_type_rules (work_type_name)
VALUES
    ('DAY_SHIFT'),               -- 주간 근무
    ('THREE_SHIFT_TWO_TEAM'),    -- 3조2교대
    ('THREE_SHIFT_THREE_TEAM'),  -- 3조3교대
    ('FOUR_SHIFT_TWO_TEAM'),     -- 4조2교대
    ('FOUR_SHIFT_THREE_TEAM'),   -- 4조3교대
    ('RESIDENTIAL_WORK');        -- 합숙 근무


-- 근무복 착용
CREATE TABLE uniform_wearing_rules (
                                       id SERIAL PRIMARY KEY,                 -- 고유 ID
                                       uniform_wearing_name VARCHAR(50) NOT NULL    -- 근무복 착용 이름
);

INSERT INTO uniform_wearing_rules (uniform_wearing_name)
VALUES
    ('ALWAYS'),      -- 상시 착용
    ('NOT_WORN'),    -- 미착용
    ('SITUATIONAL'); -- 상황별 착용


-- 사회복무요원 수
CREATE TABLE social_service_people_count (
                                             id SERIAL PRIMARY KEY,
                                             people_count VARCHAR(50) NOT NULL
);

INSERT INTO social_service_people_count (people_count)
VALUES
    ('ONE'),              -- 1명
    ('TWO'),              -- 2명
    ('THREE_TO_FIVE'),    -- 3~5명
    ('SIX_TO_TEN'),       -- 6~10명
    ('MORE_THAN_TEN');    -- 10명 이상

-- 근무지 정보 테이블
CREATE TABLE institutions (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              name VARCHAR(100) NOT NULL,
                              institution_category_id INT NOT NULL REFERENCES institution_categories (id),
                              address VARCHAR(255) NOT NULL,
                              phone_number VARCHAR(20),
                              tag_id INT REFERENCES institution_tags (id),
                              regional_military_office_id INT NOT NULL REFERENCES regional_military_offices (id),
                              region VARCHAR(100) NOT NULL,
                              parent_institution VARCHAR(100),
                              sexual_criminal_record_restriction BOOLEAN DEFAULT FALSE NOT NULL,
                              average_workhours INT CHECK (average_workhours BETWEEN 0 AND 480),
                              average_rating DECIMAL(2, 1) DEFAULT 0.0 CHECK (average_rating BETWEEN 0.0 AND 5.0),
                            review_count INT DEFAULT 0 NOT NULL ,
                              created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                              deleted_at TIMESTAMPTZ
);


-- 근무지-질병 제한 연결 테이블
CREATE TABLE institution_disease_restrictions (
                                                  institution_id UUID NOT NULL REFERENCES institutions (id),
                                                  disease_id INT NOT NULL REFERENCES disease_restrictions (id),
                                                  PRIMARY KEY (institution_id, disease_id)
);


-- 근무지 리뷰
CREATE TABLE institution_reviews
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    institution_id          UUID          NOT NULL REFERENCES institutions (id),
    user_id                 UUID          NOT NULL,
    rating                  DECIMAL(2, 1) NOT NULL CHECK (rating BETWEEN 1.0 AND 5.0),
    facility_rating         INT NOT NULL CHECK (facility_rating BETWEEN 1 AND 5),
    location_rating         INT NOT NULL CHECK (location_rating BETWEEN 1 AND 5),
    staff_rating            INT NOT NULL CHECK (staff_rating BETWEEN 1 AND 5),
    visitor_rating          INT NOT NULL CHECK (visitor_rating BETWEEN 1 AND 5),
    vacation_freedom_rating INT NOT NULL CHECK (vacation_freedom_rating BETWEEN 1 AND 5),
    main_tasks              TEXT NOT NULL ,
    pros_cons               TEXT NOT NULL ,
    average_workhours       INT NOT NULL CHECK (average_workhours BETWEEN 0 AND 480),
    work_type_rules_id      INT NOT NULL REFERENCES work_type_rules (id),
    uniform_wearing_rules_id INT NOT NULL REFERENCES uniform_wearing_rules (id),
    social_service_people_count_id INT NOT NULL REFERENCES social_service_people_count (id),
    like_count              INT DEFAULT 0,
    created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    deleted_at              TIMESTAMPTZ
);

CREATE TABLE institution_review_likes
(
    institution_review_id    UUID NOT NULL REFERENCES institution_reviews (id),
    user_id    UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (institution_review_id, user_id)
);

-- Soft Delete를 위한 인덱스
CREATE INDEX idx_institution_id_disease_id ON institution_disease_restrictions (institution_id) INCLUDE (disease_id);

CREATE INDEX idx_institutions_by_category
    ON institutions (institution_category_id) WHERE deleted_at IS NULL;

CREATE INDEX idx_institutions_by_name
    ON institutions (name) where deleted_at IS NULL;

CREATE UNIQUE INDEX one_review_per_user_per_institution_idx
    ON institution_reviews (institution_id, user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_institutions_active_by_category
    ON institutions (institution_category_id) WHERE deleted_at IS NULL;

CREATE INDEX idx_institution_reviews_active_by_institution_id
    ON institution_reviews (institution_id) WHERE deleted_at IS NULL;

CREATE INDEX idx_institution_reviews_created_at
    ON institution_reviews (created_at DESC);


\c gongik_life_client_community_db;

-- 게시글 카테고리
CREATE TABLE post_categories (
                                 id SERIAL PRIMARY KEY,
                                 category_name VARCHAR(50) NOT NULL
);

INSERT INTO post_categories (category_name)
VALUES
    ('FREE'),          -- 자유게시판
    ('QUESTION'),      -- 질문게시판
    ('INSTITUTION'),   -- 복무지게시판
    ('TRAINING'),      -- 훈련소게시판
    ('TIPS'),          -- 꿀팁게시판
    ('NOTICE');        -- 공지사항 게시판

-- 게시글 테이블
CREATE TABLE posts
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID          NOT NULL,
    category_id   INT           NOT NULL REFERENCES post_categories (id),
    title         VARCHAR(100)  NOT NULL,
    content       TEXT          NOT NULL,
    like_count    INT              DEFAULT 0,
    comment_count INT              DEFAULT 0,
    created_at    TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMPTZ
);


-- 댓글 테이블
CREATE TABLE comments
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id           UUID NOT NULL REFERENCES posts (id),
    parent_comment_id UUID REFERENCES comments (id),
    user_id           UUID NOT NULL,
    content           TEXT NOT NULL,
    created_at        TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMPTZ,
    CONSTRAINT check_no_self_reply CHECK (id != parent_comment_id
)
    );

-- 좋아요 테이블
CREATE TABLE post_likes
(
    post_id    UUID NOT NULL REFERENCES posts (id),
    user_id    UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id)
);




-- Soft Delete를 위한 인덱스
CREATE INDEX idx_posts_active_by_user
    ON posts (user_id) WHERE deleted_at IS NULL;

CREATE INDEX idx_posts_by_category_id
    ON posts (category_id) WHERE deleted_at IS NULL;

CREATE INDEX idx_comments_top_level
    ON comments (post_id, created_at DESC) WHERE parent_comment_id IS NULL AND deleted_at IS NULL;

CREATE INDEX idx_comments_replies
    ON comments (parent_comment_id, created_at DESC) WHERE deleted_at IS NULL;

CREATE INDEX idx_comments_by_user
    ON comments (user_id, created_at DESC) WHERE deleted_at IS NULL;

CREATE INDEX idx_comments_by_post_user
    ON comments (post_id, user_id, created_at DESC) WHERE deleted_at IS NULL;


\c gongik_life_client_workhours_db;

CREATE TABLE workhours_statistics
(
    id                             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    statistics_date                DATE NOT NULL,
    social_welfare_workhours      INT CHECK (social_welfare_workhours BETWEEN 0 AND 480),
    public_organization_workhours INT CHECK (public_organization_workhours BETWEEN 0 AND 480),
    national_agency_workhours     INT CHECK (national_agency_workhours BETWEEN 0 AND 480),
    local_government_workhours    INT CHECK (local_government_workhours BETWEEN 0 AND 480),
    total_vote_count               INT              DEFAULT 0,
    created_at                     TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (statistics_date)
);

CREATE INDEX idx_workhours_statistics_by_date
    ON workhours_statistics (statistics_date DESC);


\c gongik_life_client_notification_db;


   -- 알림 타입
CREATE TABLE notification_types (
                                    id SERIAL PRIMARY KEY,
                                    type_name VARCHAR(50) NOT NULL
);

INSERT INTO notification_types (type_name)
VALUES
    ('COMMENT'),           -- 댓글 알림
    ('REPLY'),             -- 대댓글 알림
    ('NOTICE'),            -- 공지사항 알림
    ('TARGETED');          -- 맞춤형 알림


-- 알림 테이블
CREATE TABLE notifications
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID              NOT NULL,
    notification_type_id   INT NOT NULL REFERENCES notification_types (id),
    title             VARCHAR(100)      NOT NULL,
    content           TEXT              NOT NULL,
    post_id           UUID,      -- 게시글 ID (COMMENT, REPLY 타입일 때 필수)
    target_comment_id UUID,      -- 스크롤할 댓글 ID (필요시)
    read_at           TIMESTAMPTZ, -- NULL이면 읽지 않음, 값이 있으면 읽은 시간
    created_at        TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP
);


-- 읽지 않은 알림 조회를 위한 인덱스
CREATE INDEX idx_notifications_unread
    ON notifications (user_id, created_at DESC) WHERE read_at IS NULL;


\c gongik_life_client_report_db;

   -- 시스템 신고 카테고리
CREATE TABLE system_report_categories (
                                          id SERIAL PRIMARY KEY,
                                          category_name VARCHAR(50) NOT NULL
);

INSERT INTO system_report_categories (category_name)
VALUES
    ('BUG'),              -- 버그/오류
    ('UI_ISSUE'),         -- UI/UX 문제
    ('PERFORMANCE'),      -- 성능 문제
    ('DATA_ERROR'),       -- 데이터 오류
    ('FEATURE_REQUEST');  -- 기능 개선 요청

-- 신고 타입
CREATE TABLE report_types (
                              id SERIAL PRIMARY KEY,
                              type_name VARCHAR(50) NOT NULL
);

INSERT INTO report_types (type_name)
VALUES
    ('SYSTEM'),           -- 시스템 오류 신고
    ('INSTITUTION'),      -- 근무지 정보 신고
    ('INSTITUTION_REVIEW'), -- 근무지 리뷰 신고
    ('POST'),             -- 게시글 신고
    ('COMMENT');          -- 댓글 신고

-- 신고 상태 ENUM
CREATE TABLE report_statuses (
                                 id SERIAL PRIMARY KEY,
                                 status_name VARCHAR(50) NOT NULL
);

INSERT INTO report_statuses (status_name)
VALUES
    ('PENDING'),   -- 접수됨
    ('REVIEWING'), -- 검토중
    ('RESOLVED'),  -- 해결됨
    ('REJECTED');  -- 거절됨

-- 신고 테이블
CREATE TABLE reports
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL,
    type_id         INT          NOT NULL REFERENCES report_types (id),
    system_category_id INT REFERENCES system_report_categories (id), -- 시스템 신고일 경우에만 사용
    title           VARCHAR(100) NOT NULL,
    content         TEXT         NOT NULL,
    status_id       INT          NOT NULL REFERENCES report_statuses (id),
    target_id       UUID,                   -- 신고 대상 ID (근무지/게시글/댓글 등의 ID)
    created_at      TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT check_system_category CHECK (
        (type_id = 1 AND system_category_id IS NOT NULL) OR
        (type_id != 1 AND system_category_id IS NULL)
        )
);

-- 인덱스
CREATE INDEX idx_reports_by_user_id
    ON reports (user_id, created_at DESC) WHERE deleted_at IS NULL;

