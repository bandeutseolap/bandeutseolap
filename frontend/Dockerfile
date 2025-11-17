# ---- 1단계: 빌드 ---
FROM node:20-alpine AS build
WORKDIR /app

# devDependencies 포함 설치
COPY package*.json ./
RUN npm ci

# 소스 복사 및 빌드
COPY . .
RUN npm run build

# ---- 2단계: Nginx로 정적 파일 서빙 ----
FROM nginx:alpine

ARG GIT_SHA

# Nginx 설정 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Vue 빌드 결과(dist) 복사
RUN rm -rf /usr/share/nginx/html/* && mkdir -p /usr/share/nginx/html
COPY --from=build /app/dist/ /usr/share/nginx/html/

# 버전 표시 파일
RUN echo "${GIT_SHA:-unknown}" > /usr/share/nginx/html/version.txt

EXPOSE 80
