```mermaid
---
title: Gongik Life Client Notification Database
---
erDiagram
    notification_types {
        integer id PK
        character_varying type_name
    }
    notifications {
        uuid id PK
        uuid user_id
        integer notification_type_id FK
        character_varying title
        text content
        uuid post_id
        uuid target_comment_id
        timestamp_with_time_zone read_at
        timestamp_with_time_zone created_at
    }

    notifications ||--o| notification_types: has
```