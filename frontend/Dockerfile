# ---- 1단계: 빌드 ---
FROM node:20-alpine AS build
WORKDIR /app

# CI 환경 표시
ENV CI=true

# 의존성 레이어 캐시 최적화
COPY package*.json ./
RUN npm ci   # devDependencies 포함해서 설치

# 소스 복사 및 빌드
COPY . .
RUN npm run build

# ---- 2단계: Nginx로 정적 파일 서빙 ----
FROM nginx:alpine

# 런타임에서만 production 설정
ENV NODE_ENV=production

# 커밋 SHA 주입 (CI에서 build-args로 전달)
ARG GIT_SHA

# Nginx 설정 반영
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 정적 파일 복사
RUN rm -rf /usr/share/nginx/html/* && mkdir -p /usr/share/nginx/html
COPY --from=build /app/build/ /usr/share/nginx/html/

# 버전 파일 생성
RUN echo "${GIT_SHA:-unknown}" > /usr/share/nginx/html/version.txt

EXPOSE 80
