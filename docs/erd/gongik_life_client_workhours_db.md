```mermaid
---
title: Gongik Life Client Workhours Database
---
erDiagram
    workhours_statistics {
        uuid id PK
        date statistics_date UK
        integer social_welfare_workhours
        integer public_organization_workhours
        integer national_agency_workhours
        integer local_government_workhours
        integer total_vote_count
        timestamp_with_time_zone created_at
    }
```