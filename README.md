# 반듯서랍 (Bandeutseolap)

> 문서, 업무, 커뮤니케이션을 통합 관리하는 협업 WorkStation 서비스

> ⚡ 사전에 정의된 문서가 아닌, 과정을 보여주는 문서로 지속적인 업데이트 진행 예정입니다.

---

## 📌 프로젝트 개요

### 서비스 정의
반듯서랍(bandeutseolap)은 프로젝트 문서 및 업무 관리 WorkStation입니다.  
문서 · 업무 · 커뮤니케이션을 하나의 플랫폼에서 통합 관리할 수 있도록 설계했습니다.

### 프로젝트 목적
- 문서 중심 협업 시스템 구축
- 서비스 아키텍처 설계 경험 확보
- 확장 가능한 협업 플랫폼 구현

### 배경
- 문서 관리 증가로 인한 업무 피로도 증가
- 문서 버전 관리 표준 필요
- 산재된 문서 통합 관리 필요
- 소통 → 문서 → 업무 프로세스 단절 문제
- 커뮤니케이션과 문서 분리로 인한 오류 발생

---

## 🎯 학습 목표

- 다양한 기술 접목 개발 경험
- 서비스 운영 경험 확보
- Monolith → Modular 설계 경험
- 데이터 아키텍처 설계 경험
- Docker 기반 운영 경험
- Worker 기반 분산 처리 경험

---

## ✨ 주요 기능 (확장 범위 포함)

### 📄 문서 관리
- 문서 생성 (프로젝트별 템플릿 관리 시스템)
- 버전 관리 (문서 버전 스냅샷 관리) & 변경 이력 관리
- 문서 Export 기능

### ✅ 업무 관리
- Task 관리 (담당자, 일정, 공수 등)
- 진행 상태 집계 및 분석 (WBS, Gantt Chart 등)

### 📁 프로젝트 관리
- 프로젝트 관리 (프로젝트 구성원, 권한 등)
- 이슈 관리 Dashboard

### 👤 사용자 관리
- N차 인증 시스템
- 시스템 접근 권한 관리

### 💬 메신저
- 실시간 Chat 시스템
- 내부 메일링 시스템
- 메신저 대화 기반 업무 생성 자동화

### 🤖 AI 기반 자동화
- 메신저 내용 기반 회의록 작성 및 업무 등록
- 프로젝트 주요 문서 전체 요약
- 문서 → 업무 등록 시스템 (요구사항 정의서, 프로젝트 질의서 등)

---

## 🏗 서비스 아키텍처

### 구성 : Core Product + Worker

| 구성 | 설명 |
|------|------|
| **Core Product** | 서비스 동작 전반의 핵심 기능 (사용자, 문서, 업무, 프로젝트, 메신저 등) |
| **Worker** | 핵심 기능 외 부가기능 (배치, AI, 분석·집계, Export 처리) |

### Worker 분리 목적
- 비동기 처리
- Core 성능 보호
- 확장성 확보
- 장애 영향 최소화

### 아키텍처 선택 이유
- 초기 개발 부담 감소
- 확장 가능한 구조 확보

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Spring Boot, JPA |
| Frontend | Vue |
| Database | MySQL, MongoDB `(Planned)`, ClickHouse `(Planned)` |
| Cache | Redis |
| Message Queue | Kafka |
| Infra | Docker, Kubernetes `(Planned)` |
| AI | LLM API `(Planned)` |
| Auth | JWT (Access / Refresh Token) |

---

## 🔐 인증 구조

- **Access Token** : 유효기간 1시간, Authorization 헤더로 전달
- **Refresh Token** : 유효기간 14일, Redis에 저장 (`RT:{username}`)
- 로그아웃 시 Access Token은 Redis 블랙리스트에 등록
- 로그인 이력은 Kafka 토픽(`auth.login.event`)을 통해 비동기 기록 → `user_logs` 테이블 적재

---

## 📋 Architecture Decision Record

| # | 결정 내용 |
|---|----------|
| 1 | 다양한 기술스택 경험을 위한 MSA 아키텍처 도입 (Kafka 도입 계획) |
| 2 | 사용자 Session 관리를 위한 IMDG 도입 (Redis 도입 계획) |
| 3 | Kafka 통한 데이터 버스를 활용한 각 데이터 영역 구분 (MongoDB, ClickHouse 도입 계획) |
| 4 | 빠른 개발 진척을 위한 Core Product + Worker 아키텍처 변경, 기존 기술 스택 유지 |

---

## 📂 프로젝트 관리 문서

- 프로젝트 개요 문서
- 기능 정의서
- 데이터 정의서 & ERD
- API 명세서
- UI/UX 정의서
- Domain 정의서
- Class Diagram
- Architecture Diagram
- Naming Rule

---

## ⚙️ 환경 설정

`.env.example`을 복사해서 `.env`를 생성하고 값을 채워주세요.

```bash
cp .env.example .env
```

**`.env.example`**

```env
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=
DB_USERNAME=
DB_PASSWORD=

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=1209600000

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

> 실제 값은 팀 내부 채널을 통해 공유합니다.

---

## 🚀 실행 방법

### 로컬 개발 환경

```bash
# 인프라 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun
```

### 배포 환경

> 서버는 VPN 환경에서 운영됩니다.

---

## 📨 Kafka 토픽 컨벤션

```
{domain}.{action}.{event}
```

| 토픽 | 설명 |
|------|------|
| `auth.login.event` | 로그인 이력 기록 |

- GroupId: `bandeutseolap-{domain}-{role}`

---


## 📝 관련 링크

- GitHub Organization: [github.com/bandeutseolap](https://github.com/bandeutseolap)
