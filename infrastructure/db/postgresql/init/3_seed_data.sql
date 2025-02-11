
-- 데이터 삽입 시작

\c gongik_life_client_user_db;

-- 시드 데이터 삽입 스크립트 시작

-- 사용자 기본 정보
-- 사용자 기본 정보 시드 데이터
INSERT INTO users (id, email, is_active, created_at, updated_at, deleted_at)
VALUES
    ('b3e29d4d-5597-476a-bad0-4be38cf75db1', 'user1@example.com', true, now(), now(), NULL),
    ('bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 'user2@example.com', true, now(), now(), NULL),
    ('a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 'user3@example.com', false,  now(), now(), NULL),
    ('fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', 'user4@example.com', true,  now(), now(), NULL),
    ('de12c885-392b-432d-8649-dc8bff4e69b8', 'user5@example.com', false, now(), now(), NULL);

-- 사용자 로그인 기록 시드 데이터
INSERT INTO user_login_histories (id, user_id, last_login_at, ip_address, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'b3e29d4d-5597-476a-bad0-4be38cf75db1', '2023-10-10 08:30:00', '192.168.1.1', now(), now()),
    (gen_random_uuid(), 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', '2023-10-11 12:15:00', '192.168.1.2', now(), now()),
    (gen_random_uuid(), 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', '2023-10-09 22:45:00', '192.168.1.3', now(), now()),
    (gen_random_uuid(), 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', '2023-10-08 18:00:00', '192.168.1.4', now(), now()),
    (gen_random_uuid(), 'de12c885-392b-432d-8649-dc8bff4e69b8', '2023-10-07 14:30:00', '192.168.1.5', now(), now());

-- 사용자 인증 정보 시드 데이터
INSERT INTO user_auths (id, user_id, auth_type_id, password_hash, created_at, updated_at, deleted_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 1, 'hash_password1', now(), now(), NULL),
    ('22222222-2222-2222-2222-222222222222', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 2, NULL, now(), now(), NULL),
    ('33333333-3333-3333-3333-333333333333', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 3, NULL, now(), now(), NULL),
    ('44444444-4444-4444-4444-444444444444', 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', 1, 'hash_password4', now(), now(), NULL),
    ('55555555-5555-5555-5555-555555555555', 'de12c885-392b-432d-8649-dc8bff4e69b8', 4, NULL, now(), now(), NULL);

-- 사용자 프로필 시드 데이터
INSERT INTO user_profiles (id, user_id, institution_id, name, bio, enlistment_date, discharge_date, created_at, updated_at, deleted_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', '11111111-1111-1111-1111-111111111111', 'Alice', 'I love volunteering', '2023-06-01', '2024-05-31', now(), now(), NULL),
    ('22222222-2222-2222-2222-222222222222', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', '22222222-2222-2222-2222-222222222222', 'Bob', 'Team player and hard worker', '2023-05-01', '2024-04-30', now(), now(), NULL),
    ('33333333-3333-3333-3333-333333333333', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', '33333333-3333-3333-3333-333333333333', 'Charlie', 'Tech enthusiast', '2023-07-01', '2024-06-30', now(), now(), NULL),
    ('44444444-4444-4444-4444-444444444444', 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', '44444444-4444-4444-4444-444444444444', 'Diana', 'Outdoor activities lover', '2023-08-01', '2024-07-31', now(), now(), NULL),
    ('55555555-5555-5555-5555-555555555555', 'de12c885-392b-432d-8649-dc8bff4e69b8', '55555555-5555-5555-5555-555555555555', 'Eve', 'Community helper', '2023-09-01', '2024-08-31', now(), now(), NULL);


-- 시드 데이터 삽입 스크립트 끝


\c gongik_life_client_community_db;

-- 시드 데이터 삽입 스크립트 시작

-- 게시글 데이터 시드
INSERT INTO posts (id, user_id, category_id, title, content, like_count, comment_count, created_at, updated_at, deleted_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 1, 'My first post', 'Hello everyone! This is my first post in the community.', 10, 2, now(), now(), NULL),
    ('22222222-2222-2222-2222-222222222222', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 2, 'How to improve productivity?', 'Can anyone share tips to stay focused during work hours?', 25, 3, now(), now(), NULL),
    ('33333333-3333-3333-3333-333333333333', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 3, 'Workplace Experience', 'Sharing my thoughts about the experience at my current workplace.', 15, 4, now(), now(), NULL),
    ('44444444-4444-4444-4444-444444444444', 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', 4, 'Training Center Tips', 'What are some essential items to bring to the training center?', 5, 1, now(), now(), NULL),
    ('55555555-5555-5555-5555-555555555555', 'de12c885-392b-432d-8649-dc8bff4e69b8', 5, 'Efficient time management', 'Sharing some tips and tricks for managing time effectively.', 30, 5, now(), now(), NULL);

-- 댓글 데이터 시드
INSERT INTO comments (id, post_id, parent_comment_id, user_id, content, created_at, updated_at, deleted_at)
VALUES
    -- Comments on Post 1
    ('66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', NULL, 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 'Welcome to the community!', now(), now(), NULL),
    ('77777777-7777-7777-7777-777777777777', '11111111-1111-1111-1111-111111111111', NULL, 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 'Great to have you here!', now(), now(), NULL),

    -- Comments on Post 2
    ('88888888-8888-8888-8888-888888888888', '22222222-2222-2222-2222-222222222222', NULL, 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 'I recommend trying the Pomodoro technique.', now(), now(), NULL),
    ('99999999-9999-9999-9999-999999999999', '22222222-2222-2222-2222-222222222222', NULL, 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', 'Focus on one task at a time.', now(), now(), NULL),

    -- Comments on Post 3
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '33333333-3333-3333-3333-333333333333', NULL, 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 'Thanks for sharing your experience!', now(), now(), NULL),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333', NULL, 'de12c885-392b-432d-8649-dc8bff4e69b8', 'Very insightful!', now(), now(), NULL),

    -- Comments on Post 4
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '44444444-4444-4444-4444-444444444444', NULL, 'de12c885-392b-432d-8649-dc8bff4e69b8', 'Make sure to bring comfortable shoes!', now(), now(), NULL);

-- 좋아요 데이터 시드
INSERT INTO post_likes (post_id, user_id, created_at)
VALUES
    -- Likes for Post 1
    ('11111111-1111-1111-1111-111111111111', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', now()),
    ('11111111-1111-1111-1111-111111111111', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', now()),

    -- Likes for Post 2
    ('22222222-2222-2222-2222-222222222222', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', now()),
    ('22222222-2222-2222-2222-222222222222', 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', now()),

    -- Likes for Post 3
    ('33333333-3333-3333-3333-333333333333', 'de12c885-392b-432d-8649-dc8bff4e69b8', now()),

    -- Likes for Post 4
    ('44444444-4444-4444-4444-444444444444', 'de12c885-392b-432d-8649-dc8bff4e69b8', now()),

    -- Likes for Post 5
    ('55555555-5555-5555-5555-555555555555', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', now());

-- 시드 데이터 삽입 스크립트 끝


\c gongik_life_client_workhours_db;

-- 시드 데이터 삽입 스크립트 시작

-- 업무시간 통계 데이터 삽입
INSERT INTO workhours_statistics (id, statistics_date, social_welfare_workhours, public_organization_workhours, national_agency_workhours, local_government_workhours, total_vote_count, created_at)
VALUES
    (gen_random_uuid(), '2023-01-01', 300, 240, 260, 220, 50, now()),
    (gen_random_uuid(), '2023-01-08', 320, 230, 250, 200, 40, now()),
    (gen_random_uuid(), '2023-01-15', 310, 220, 270, 210, 45, now()),
    (gen_random_uuid(), '2023-01-22', 280, 250, 240, 230, 30, now()),
    (gen_random_uuid(), '2023-01-29', 290, 260, 220, 210, 55, now()),
    (gen_random_uuid(), '2023-02-05', 300, 245, 255, 225, 60, now()),
    (gen_random_uuid(), '2023-02-12', 310, 250, 240, 220, 70, now()),
    (gen_random_uuid(), '2023-02-19', 320, 240, 230, 210, 80, now()),
    (gen_random_uuid(), '2023-02-26', 350, 230, 260, 200, 90, now()),
    (gen_random_uuid(), '2023-03-05', 300, 220, 250, 190, 65, now());

-- 시드 데이터 삽입 스크립트 끝


\c gongik_life_client_notification_db;

   -- 시드 데이터 삽입 스크립트 시작

-- 알림 데이터
-- 알림 데이터
INSERT INTO notifications (id, user_id, notification_type_id, title, content, post_id, target_comment_id, read_at, created_at)
VALUES
    -- 댓글 알림 (COMMENT)
    ('11111111-1111-1111-1111-111111111111', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 1, 'New Comment', 'A user commented on your post.', '22222222-2222-2222-2222-222222222222', NULL, NULL, now()),
    ('22222222-2222-2222-2222-222222222222', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 1, 'New Comment', 'Someone commented on your free post.', '33333333-3333-3333-3333-333333333333', NULL, NULL, now()),

    -- 대댓글 알림 (REPLY)
    ('33333333-3333-3333-3333-333333333333', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 2, 'New Reply', 'A user replied to your comment.', '44444444-4444-4444-4444-444444444444', '66666666-6666-6666-6666-666666666666', NULL, now()),
    ('44444444-4444-4444-4444-444444444444', 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', 2, 'New Reply', 'Someone replied to your comment.', '55555555-5555-5555-5555-555555555555', '77777777-7777-7777-7777-777777777777', NULL, now()),

    -- 공지사항 알림 (NOTICE)
    ('55555555-5555-5555-5555-555555555555', 'de12c885-392b-432d-8649-dc8bff4e69b8', 3, 'System Update', 'We have updated our system. Check out new features!', NULL, NULL, NULL, now()),
    ('66666666-6666-6666-6666-666666666666', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 3, 'Terms of Service Update', 'Our terms of service have been updated. Please review.', NULL, NULL, NULL, now()),

    -- 맞춤형 알림 (TARGETED)
    ('77777777-7777-7777-7777-777777777777', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 4, 'Welcome!', 'Welcome to our platform! Explore and enjoy.', NULL, NULL, NULL, now()),
    ('88888888-8888-8888-8888-888888888888', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 4, 'Personalized Suggestion', 'Check out this post we picked for you!', '99999999-9999-9999-9999-999999999999', NULL, NULL, now());


-- 시드 데이터 삽입 스크립트 끝


\c gongik_life_client_report_db;


   -- 시드 데이터 삽입 스크립트 시작

-- 신고 데이터 삽입
INSERT INTO reports (id, user_id, type_id, system_category_id, title, content, status_id, target_id, created_at, updated_at)
VALUES
    -- 시스템 실패 신고
    ('11111111-1111-1111-1111-111111111111', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 1, 1, 'Login Issue', 'Unable to log in with valid credentials.', 1, NULL, now(), now()),
    ('22222222-2222-2222-2222-222222222222', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 1, 2, 'UI alignment problem', 'Text misaligned on the homepage.', 2, NULL, now(), now()),
    ('33333333-3333-3333-3333-333333333333', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 1, 3, 'Slow Response', 'The system becomes too slow during peak hours.', 3, NULL, now(), now()),

    -- 근무지 신고
    ('44444444-4444-4444-4444-444444444444', 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', 2, NULL, 'Incorrect Address', 'The address provided for Seoul Welfare Center is outdated.', 1, 'ea3ec9d2-5d48-4928-bff9-64237d38553e', now(), now()),
    ('55555555-5555-5555-5555-555555555555', 'de12c885-392b-432d-8649-dc8bff4e69b8', 2, NULL, 'Fake Contact Info', 'The contact number for Busan Public Service is no longer in service.', 3, 'c721ba4f-a28c-42a7-bc60-423b7df5fcc1', now(), now()),

    -- 근무지 리뷰 신고
    ('66666666-6666-6666-6666-666666666666', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 3, NULL, 'Inappropriate Review', 'The review contains sensitive and offensive language.', 2, '555105e5-9283-49a5-8e41-4b385af22ebb', now(), now()),
    ('77777777-7777-7777-7777-777777777777', 'bef7c1af-7c0b-4e14-8451-8c6f9d571e51', 3, NULL, 'Spam Review', 'The review repeats the same content multiple times.', 4, 'f2918a3c-3e00-4dfb-9d76-ffc3d3bdf37b', now(), now()),

    -- 게시글 신고
    ('88888888-8888-8888-8888-888888888888', 'a1cd20ca-2b98-4d3e-9b96-3fb83fa66c95', 4, NULL, 'Offensive Post', 'The content of the post is harmful and in violation of the guidelines.', 1, '65c6d94b-959e-44e5-b943-ed3e8f913baa', now(), now()),
    ('99999999-9999-9999-9999-999999999999', 'fd7a91a9-743b-4d2f-8edf-1b98ff1e27f0', 4, NULL, 'Misleading Information', 'The post contains false information about the service process.', 1, '30dfa32d-49e8-4bfb-9527-13e1c915c1f5', now(), now()),

    -- 댓글 신고
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'de12c885-392b-432d-8649-dc8bff4e69b8', 5, NULL, 'Harassment in Comment', 'The comment contains harassment towards another user.', 1, '097a39b8-8c2b-4897-b4d3-d761e5c837a3', now(), now()),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'b3e29d4d-5597-476a-bad0-4be38cf75db1', 5, NULL, 'Spam Comment', 'This comment repeatedly advertises unrelated products.', 3, 'b2f8ae8b-d890-499e-9002-0ba71cb6823c', now(), now());

-- 시드 데이터 삽입 스크립트 끝