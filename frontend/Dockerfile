# ---- 1단계: 빌드 ----
FROM node:20-alpine AS build
ENV NODE_ENV=production
WORKDIR /app

# 의존성 레이어 캐시 최적화
COPY package*.json ./
RUN npm ci

# 소스 복사 및 빌드
COPY . .
RUN npm run build

# ---- 2단계: Nginx로 정적 파일 서빙 ----
FROM nginx:alpine

# Nginx 설정 반영
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 정적 파일 복사
RUN rm -rf /usr/share/nginx/html/* && mkdir -p /usr/share/nginx/html
COPY --from=build /app/build/ /usr/share/nginx/html/

EXPOSE 80