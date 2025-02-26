#!/bin/bash

# 각 모듈별 Docker 이미지 빌드
docker build -t gongik-life-client-be-discovery:latest -f gongik-life-client-be-discovery/Dockerfile .
docker build -t gongik-life-client-be-gateway:latest -f gongik-life-client-be-gateway/Dockerfile .
docker build -t gongik-life-client-be-graphql:latest -f gongik-life-client-be-graphql/Dockerfile .
docker build -t gongik-life-client-be-auth-service:latest -f gongik-life-client-be-auth-service/Dockerfile .
docker build -t gongik-life-client-be-user-service:latest -f gongik-life-client-be-user-service/Dockerfile .
docker build -t gongik-life-client-be-institution-service:latest -f gongik-life-client-be-institution-service/Dockerfile .
docker build -t gongik-life-client-be-community-service:latest -f gongik-life-client-be-community-service/Dockerfile .
docker build -t gongik-life-client-be-workhours-service:latest -f gongik-life-client-be-workhours-service/Dockerfile .
docker build -t gongik-life-client-be-notification-service:latest -f gongik-life-client-be-notification-service/Dockerfile .
docker build -t gongik-life-client-be-report-service:latest -f gongik-life-client-be-report-service/Dockerfile .
docker build -t gongik-life-client-be-mail-service:latest -f gongik-life-client-be-mail-service/Dockerfile .


