# Tax Workbench

Spring Boot + React 기반의 **세무 업무 관리 시스템(Tax Workbench)**
입니다. 고객사별 세무 업무를 관리하고, 대량 등록 및 CSV Export 기능을
제공하며 **동시 수정 충돌 방지와 감사 로그(Audit Log)** 기능을
포함합니다.

------------------------------------------------------------------------

# Features

### Work Item Dashboard

-   세무 업무 목록 조회
-   페이지네이션
-   필터링
-   정렬

### Inline Editing

-   상태(Status)
-   담당자(Assignee)
-   마감일(Due Date)
-   메모(Memo)

### Work Item Creation

-   고객사 기반 업무 생성
-   업무 유형별 규칙 적용

### Bulk Create

CSV 데이터를 이용한 **대량 업무 등록**

-   batch 저장 처리
-   일부 실패 시 부분 성공 처리
-   오류 row 상세 메시지 반환

### CSV Export

현재 필터링된 업무 목록 전체를 CSV로 다운로드

-   Streaming 기반 Export
-   메모리 사용 최소화

### Audit Log

업무 변경 이력 관리

-   필드 단위 변경 기록
-   이전 값 / 변경 값 저장
-   변경 시각 및 사용자 기록

### Concurrency Control

동시 수정 충돌 방지

-   Optimistic Locking
-   HTTP 409 Conflict 반환
-   프론트 충돌 안내 UI 제공

------------------------------------------------------------------------

# Tech Stack

## Backend

-   Java 21
-   Spring Boot
-   Spring Data JPA
-   H2 Database
-   Gradle

## Frontend

-   React
-   TypeScript
-   Vite
-   TanStack Query

------------------------------------------------------------------------

# Project Structure
```text
api
│  └─ src/main/java/com/taxworkbench/api
│     ├─ domain
│     ├─ application
│     ├─ infrastructure
│     └─ interfaces
└─ web
   └─ src
      ├─ api
      ├─ components
      ├─ hooks
      ├─ pages
      └─ types
```
------------------------------------------------------------------------

# How to Run

## Backend

cd api ./gradlew bootRun

Server: http://localhost:8080

## Frontend

cd web npm install npm run dev

Web: http://localhost:5173

------------------------------------------------------------------------

# API Overview
```text
GET /api/v1/work-items -> 업무 목록 조회
POST /api/v1/work-items -> 업무 생성
PATCH /api/v1/work-items/{id} -> 업무 수정
POST /api/v1/work-items/bulk -> 대량 업무 생성
GET /api/v1/work-items/export -> CSV 다운로드
GET /api/v1/work-items/{id}/audit -> 변경 이력 조회
```
------------------------------------------------------------------------

# Concurrency Control

Optimistic Locking을 사용하여 WorkItem 동시 수정 충돌을 방지했습니다.
```text
@Version
private long version;
```
충돌 시 HTTP 409 Conflict 반환

------------------------------------------------------------------------

# CSV Export

StreamingResponseBody 기반 CSV 다운로드

-   현재 필터와 동일한 조건 적용
-   메모리 사용 최소화

------------------------------------------------------------------------

# Bulk Create

CSV 입력을 통한 대량 WorkItem 생성
```text
clientId,type,assignee,dueDate,memo,tags
1,FILING,Kim,2026-03-31,3월 신고,vat\|march
```
응답 예시
```text
{
   "savedCount": 50,
   "skippedCount": 3,
   "errors": \[ "row 4: 존재하지않는 clientId", "row 7: 비활성 고객사" \]
}
```
------------------------------------------------------------------------

# Audit Log

WorkItem 변경 시 필드 단위 변경 이력을 저장합니다.

------------------------------------------------------------------------

# Validation

Bean Validation 사용
```text
@NotNull
@Positive
@Size(max=100)
```
------------------------------------------------------------------------

# Future Improvements

-   PostgreSQL
-   Redis Cache
-   Async Export
-   Message Queue 기반 Audit

------------------------------------------------------------------------

# AI Collaboration

AI 도구를 활용하여 코드 리뷰 및 구조 개선을 보조적으로 수행했습니다.
