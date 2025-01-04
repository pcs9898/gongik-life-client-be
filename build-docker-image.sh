#!/bin/bash

# 각 모듈별 Docker 이미지 빌드
docker build -t gongik-life-client-be-discovery:latest -f gongik-life-client-be-discovery/Dockerfile .
docker build -t gongik-life-client-be-gateway:latest -f gongik-life-client-be-gateway/Dockerfile .

docker build -t gongik-life-client-be-auth-service:lastest -f gongik-life-client-be-auth-service/Dockerfile .

