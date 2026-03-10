# Tax Workbench

Spring Boot + React 기반의 세무 업무 관리 시스템입니다. 고객사별 세무 업무를 관리하고, 대량 등록과 CSV Export를 지원하며, 동시 수정 충돌 방지와 감사 로그 기능을 포함합니다.

## Features

### Work Item Dashboard
- 세무 업무 목록 조회
- 페이지네이션
- 필터링
- 정렬

### Inline Editing
- 상태(Status)
- 담당자(Assignee)
- 마감일(Due Date)
- 메모(Memo)

### Work Item Creation
- 고객사 기반 업무 생성
- 업무 유형별 규칙 적용

### Bulk Create
- CSV 데이터를 이용한 대량 업무 등록
- Batch 저장 처리
- 일부 실패 시 부분 성공 처리
- 오류 row 상세 메시지 반환

### CSV Export
- 현재 필터링된 업무 목록 전체 다운로드
- Streaming 기반 export
- 메모리 사용 최소화

### Audit Log
- 업무 변경 이력 관리
- 필드 단위 변경 기록
- 이전 값 / 변경 값 저장
- 변경 시각 및 사용자 기록

### Concurrency Control
- Optimistic Locking
- HTTP 409 Conflict 반환
- 프론트 충돌 안내 UI 제공

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- H2 Database
- Gradle

### Frontend
- React
- TypeScript
- Vite
- TanStack Query

## Project Structure

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

## How to Run

### Backend

```bash
cd api
./gradlew bootRun
```

Server: `http://localhost:8080`

### Frontend

```bash
cd web
npm install
npm run dev
```

Web: `http://localhost:5173`

## API Overview

```text
GET    /api/v1/clients               -> 고객사 목록 조회
POST   /api/v1/clients               -> 고객사 생성
GET    /api/v1/work-items            -> 업무 목록 조회
POST   /api/v1/work-items            -> 업무 생성
PATCH  /api/v1/work-items/{id}       -> 업무 수정
POST   /api/v1/work-items/bulk       -> 대량 업무 생성
GET    /api/v1/work-items/export     -> CSV 다운로드
GET    /api/v1/work-items/{id}/audit -> 변경 이력 조회
```

## API Response Contract

모든 JSON API는 아래 공통 envelope를 사용합니다.

성공 응답

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

실패 응답

```json
{
  "success": false,
  "data": {
    "code": "VALIDATION_ERROR",
    "details": {}
  },
  "message": "입력값이 올바르지 않습니다."
}
```

대표 에러 코드

- `VALIDATION_ERROR`: Bean Validation 실패
- `INVALID_ARGUMENT`: 잘못된 요청 값
- `INVALID_STATE`: 비즈니스 규칙 위반
- `INVALID_PARAMETER`: 쿼리 파라미터 형식 오류
- `OPTIMISTIC_LOCK_CONFLICT`: 동시 수정 충돌
- `INTERNAL_SERVER_ERROR`: 서버 내부 오류

## API Examples

### GET /api/v1/clients

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": 1,
        "name": "Acme Tax",
        "bizNo": "123-45-67890",
        "type": "CORPORATE",
        "status": "ACTIVE",
        "tier": "VIP",
        "createdAt": "2026-03-10T09:00:00",
        "updatedAt": "2026-03-10T09:00:00"
      }
    ],
    "count": 1
  },
  "message": null
}
```

### GET /api/v1/work-items

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 10,
        "clientId": 1,
        "clientName": "Acme Tax",
        "bizNo": "123-45-67890",
        "type": "FILING",
        "status": "TODO",
        "assignee": "Kim",
        "dueDate": "2026-03-31",
        "tags": ["vat"],
        "memo": "3월 신고",
        "createdAt": "2026-03-10T09:00:00",
        "updatedAt": "2026-03-10T09:00:00",
        "version": 0
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "size": 20
  },
  "message": null
}
```

### PATCH /api/v1/work-items/{id} conflict example

```json
{
  "success": false,
  "data": {
    "code": "OPTIMISTIC_LOCK_CONFLICT",
    "details": {
      "currentVersion": 5,
      "expectedVersion": 4,
      "currentData": {
        "id": 10,
        "clientId": 1,
        "clientName": "Acme Tax",
        "bizNo": "123-45-67890",
        "type": "FILING",
        "status": "IN_PROGRESS",
        "assignee": "Lee",
        "dueDate": "2026-03-31",
        "tags": ["vat"],
        "memo": "수정됨",
        "createdAt": "2026-03-10T09:00:00",
        "updatedAt": "2026-03-10T10:00:00",
        "version": 5
      }
    }
  },
  "message": "다른 사용자가 이미 수정했습니다. 최신 데이터를 확인해주세요."
}
```

## Concurrency Control

Optimistic Locking을 사용하여 WorkItem 동시 수정 충돌을 방지했습니다.

```java
@Version
private long version;
```

충돌 시 HTTP `409 Conflict`를 반환합니다.

## CSV Export

StreamingResponseBody 기반 CSV 다운로드를 사용합니다.

- 현재 필터와 동일한 조건 적용
- 메모리 사용 최소화

## Bulk Create

CSV 입력을 통한 대량 WorkItem 생성 예시입니다.

```text
clientId,type,assignee,dueDate,memo,tags
1,FILING,Kim,2026-03-31,3월 신고,vat|march
```

응답 예시

```json
{
  "success": true,
  "data": {
    "savedCount": 50,
    "skippedCount": 3,
    "errors": [
      "row 4: 존재하지 않는 clientId 입니다. (99)",
      "row 7: 비활성 고객사입니다. (15)"
    ]
  },
  "message": null
}
```

## Audit Log

WorkItem 변경 시 필드 단위 변경 이력을 저장합니다.

## Validation

Bean Validation을 사용합니다.

```java
@NotNull
@Positive
@Size(max = 100)
```

## Future Improvements

- PostgreSQL
- Redis Cache
- Async Export
- Message Queue 기반 Audit

## AI Collaboration

AI 도구를 활용하여 코드 리뷰와 구조 개선을 보조적으로 수행했습니다.
