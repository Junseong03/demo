# 교내 동아리 플랫폼 API 명세서

## 기본 정보

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **인증 방식**: 현재는 userId를 쿼리 파라미터로 전달 (해커톤용 간단 구현)

## HTTP 상태 코드

- **200 OK**: 성공
- **400 Bad Request**: 잘못된 요청 (파라미터 오류, 유효성 검사 실패 등)
- **404 Not Found**: 리소스를 찾을 수 없음 (존재하지 않는 ID 등)
- **401 Unauthorized**: 인증 필요 (향후 확장 예정)

**예시:**
- 동아리를 찾을 수 없습니다 → `404 Not Found`
- 잘못된 userId → `400 Bad Request`
- (향후) 로그인 필요 → `401 Unauthorized`

---

## 1. 인증 API

### 1.1 회원가입

**POST** `/api/auth/signup`

**Request Body:**
```json
{
  "email": "string (required)",
  "password": "string (required)",
  "name": "string (required)",
  "major": "string (optional)",
  "interestTags": ["string"] (optional)
}
```

**Response:** `200 OK`
```json
{
  "userId": 1,
  "email": "student@university.ac.kr",
  "name": "학생이름",
  "message": "회원가입 성공"
}
```

**에러 응답:**
```json
{
  "message": "이미 존재하는 이메일입니다.",
  "status": "error"
}
```

---

### 1.2 로그인

**POST** `/api/auth/login`

**Request Body:**
```json
{
  "email": "string (required)",
  "password": "string (required)"
}
```

**Response:** `200 OK`
```json
{
  "userId": 1,
  "email": "student1@university.ac.kr",
  "name": "김학생",
  "message": "로그인 성공"
}
```

**에러 응답:**
```json
{
  "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
  "status": "error"
}
```

---

## 2. 내 정보 API

### 2.1 내 정보 조회

**GET** `/api/me`

**Query Parameters:**
- `userId` (required): 사용자 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "student1@university.ac.kr",
  "name": "김학생",
  "major": "컴퓨터공학과",
  "interestTags": ["개발", "프로그래밍"]
}
```

---

### 2.2 내 정보 수정

**PUT** `/api/me`

**Query Parameters:**
- `userId` (required): 사용자 ID

**Request Body:**
```json
{
  "name": "string (optional)",
  "major": "string (optional)",
  "interestTags": ["string"] (optional)
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "student1@university.ac.kr",
  "name": "수정된이름",
  "major": "컴퓨터공학과",
  "interestTags": ["개발", "프로그래밍", "AI"]
}
```

---

## 3. 홈 API

### 3.1 홈 추천

**GET** `/api/home/recommendations`

**Query Parameters:**
- `userId` (optional): 사용자 ID

**Response:** `200 OK`
```json
{
  "recommendedClubs": [
    {
      "id": 1,
      "name": "알고리즘 동아리",
      "type": "CENTRAL",
      "department": "중앙동아리",
      "description": "알고리즘 문제 해결 및 코딩 테스트 준비",
      "imageUrl": "https://example.com/club1.jpg",
      "isRecruiting": true,
      "tags": ["개발", "프로그래밍", "알고리즘"]
    }
  ],
  "recommendedInSchoolActivities": [
    {
      "id": 1,
      "title": "2024 교내 프로그래밍 경진대회",
      "description": "알고리즘 문제 해결 능력을 겨루는 대회",
      "type": "IN_SCHOOL",
      "category": "COMPETITION",
      "organizer": "컴퓨터공학과",
      "deadline": "2024-12-31",
      "link": "https://example.com/contest1",
      "imageUrl": "https://example.com/activity1.jpg",
      "tags": ["개발", "프로그래밍", "대회"]
    }
  ],
  "recommendedOutSchoolActivities": [
    {
      "id": 4,
      "title": "전국 대학생 프로그래밍 대회",
      "description": "전국 대학생들이 참여하는 프로그래밍 대회",
      "type": "OUT_SCHOOL",
      "category": "COMPETITION",
      "organizer": "한국정보과학회",
      "deadline": "2024-12-31",
      "link": "https://example.com/national_contest",
      "imageUrl": "https://example.com/activity4.jpg",
      "tags": ["개발", "프로그래밍", "대회"]
    }
  ],
  "recommendedJobs": [
    {
      "id": 1,
      "companyName": "테크 스타트업 A",
      "position": "백엔드 개발자",
      "description": "Spring Boot 기반 백엔드 개발자 채용",
      "location": "서울",
      "deadline": "2024-12-31",
      "link": "https://example.com/job1",
      "tags": ["백엔드", "Spring", "Java"]
    }
  ]
}
```

---

## 4. 동아리 API

### 4.1 동아리 목록

**GET** `/api/clubs`

**Query Parameters:**
- `type` (optional): `CENTRAL` 또는 `DEPARTMENT`
- `tag` (optional): 태그명 (예: "개발", "디자인")
- `keyword` (optional): 검색 키워드
- `page` (optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (optional, default: 100): 페이지 크기 (해커톤용: 기본값 100으로 사실상 전체 조회)

**Response:** `200 OK` (항상 페이지네이션 객체)
```json
{
  "content": [
    {
      "id": 1,
      "name": "알고리즘 동아리",
      "type": "CENTRAL",
      "department": "중앙동아리",
      "description": "알고리즘 문제 해결 및 코딩 테스트 준비",
      "imageUrl": "https://example.com/club1.jpg",
      "isRecruiting": true,
      "tags": ["개발", "프로그래밍", "알고리즘"]
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 42
}
```

**예시:**
- `/api/clubs?type=CENTRAL` - 중앙 동아리만 (기본 size=100)
- `/api/clubs?type=CENTRAL&page=0&size=10` - 중앙 동아리 (페이지네이션)
- `/api/clubs?type=CENTRAL&tag=개발` - 중앙 동아리 중 개발 태그
- `/api/clubs?keyword=알고리즘` - 키워드 검색

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다 (잘못된 clubId)

---

### 4.2 동아리 상세

**GET** `/api/clubs/{clubId}`

**Path Parameters:**
- `clubId`: 동아리 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "알고리즘 동아리",
  "type": "CENTRAL",
  "department": "중앙동아리",
  "description": "알고리즘 문제 해결 및 코딩 테스트 준비",
  "fullDescription": "알고리즘 문제 해결을 통해 코딩 실력을 향상시키고, 코딩 테스트를 준비하는 동아리입니다.",
  "imageUrl": "https://example.com/club1.jpg",
  "snsLink": "https://instagram.com/algorithm_club",
  "isRecruiting": true,
  "tags": ["개발", "프로그래밍", "알고리즘"]
}
```

---

### 4.3 동아리 문의 보내기

**POST** `/api/clubs/{clubId}/inquiries`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId`: 사용자 ID

**Request Body:**
```json
{
  "message": "string (required, max 1000자)"
}
```

**Response:** `200 OK` (빈 응답)

**에러 응답:**
- `400 Bad Request`: 동아리 회장이나 부원은 문의할 수 없습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 동아리 회장이나 이미 가입된 부원은 문의할 수 없습니다.

---

### 4.4 동아리 생성

**POST** `/api/clubs`

**Request Body:**
```json
{
  "name": "string (required)",
  "type": "CENTRAL | DEPARTMENT (required)",
  "department": "string (required)",
  "description": "string (required, max 500자)",
  "fullDescription": "string (optional, max 1000자)",
  "imageUrl": "string (optional)",
  "snsLink": "string (optional)",
  "tags": ["string"] (optional),
  "isRecruiting": "boolean (optional, default: false)",
  "userId": "number (required) - 동아리 생성자 (회장으로 자동 설정)"
}
```

**Response:** `201 Created`
```json
{
  "id": 5,
  "name": "새로운 동아리",
  "type": "CENTRAL",
  "department": "중앙동아리",
  "description": "새로 만든 동아리입니다",
  "imageUrl": "https://example.com/new-club.jpg",
  "isRecruiting": true,
  "tags": ["개발", "프로그래밍"]
}
```

**에러 응답:**
- `400 Bad Request`: 유효성 검사 실패 (필수 필드 누락, 잘못된 type 값, 특수기호 포함 등)
```json
{
  "message": "동아리 이름은 필수입니다.",
  "status": "error"
}
```

**유효성 검사 규칙:**
- `name`: 영어, 숫자, 한글만 입력 가능 (공백 포함)
- `tags`: 각 태그는 영어, 숫자, 한글만 입력 가능 (공백 제외)
- `department`, `description`, `fullDescription`: 특수문자 포함 가능
- `imageUrl`, `snsLink`: URL이므로 특수기호 허용
- 동아리 이름은 중복될 수 없습니다 (이미 존재하는 이름이면 `400 Bad Request`)

**참고:**
- `tags`는 배열로 전달하거나, 프론트엔드에서 쉼표로 구분된 문자열을 배열로 변환하여 전달할 수 있습니다.
- `type`은 `CENTRAL` (중앙동아리) 또는 `DEPARTMENT` (과동아리)만 가능합니다.
- 동아리 이름 중복 시: `"이미 존재하는 동아리 이름입니다."` 에러 메시지 반환
- `userId`로 지정한 사용자가 동아리 생성 시 자동으로 회장(ADMIN)으로 설정됩니다.

---

### 4.5 동아리 회장 조회

**GET** `/api/clubs/{clubId}/admin`

**Path Parameters:**
- `clubId`: 동아리 ID

**Response:** `200 OK`
```json
{
  "userId": 1,
  "userName": "김학생",
  "userEmail": "student1@university.ac.kr",
  "clubId": 1,
  "clubName": "알고리즘 동아리"
}
```

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

---

### 4.6 동아리 회장 변경

**PUT** `/api/clubs/{clubId}/admin`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId` (required): 새 회장으로 지정할 사용자 ID

**Response:** `200 OK`
```json
{
  "userId": 2,
  "userName": "이학생",
  "userEmail": "student2@university.ac.kr",
  "clubId": 1,
  "clubName": "알고리즘 동아리"
}
```

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다
- `404 Not Found`: 기존 회장을 찾을 수 없습니다

**참고:**
- 기존 회장은 자동으로 관리자(ADMIN)로 변경됩니다.
- 새 회장이 이미 동아리 멤버인 경우 역할만 변경되고, 멤버가 아닌 경우 자동으로 멤버로 추가됩니다.

---

### 4.7 동아리 이미지 조회

**GET** `/api/clubs/{clubId}/image`

**Path Parameters:**
- `clubId`: 동아리 ID

**Response:** `200 OK`
```json
{
  "clubId": 1,
  "clubName": "알고리즘 동아리",
  "imageUrl": "https://example.com/club1.jpg"
}
```

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다

---

### 4.8 동아리 이미지 업데이트

**PUT** `/api/clubs/{clubId}/image`

**Path Parameters:**
- `clubId`: 동아리 ID

**Request Body:**
```json
{
  "imageUrl": "string (required)"
}
```

**Response:** `200 OK`
```json
{
  "clubId": 1,
  "clubName": "알고리즘 동아리",
  "imageUrl": "https://example.com/new-club-image.jpg"
}
```

**에러 응답:**
- `400 Bad Request`: 이미지 URL은 필수입니다
- `404 Not Found`: 동아리를 찾을 수 없습니다

**참고:**
- URL 방식: 이미지 URL을 직접 입력하여 저장할 수 있습니다 (기존 방식).
- 파일 업로드 방식: 실제 이미지 파일을 업로드하여 MinIO에 저장할 수 있습니다 (권장).

---

### 4.9 동아리 이미지 파일 업로드

**POST** `/api/clubs/{clubId}/image/upload`

**Path Parameters:**
- `clubId`: 동아리 ID

**Request:**
- Content-Type: `multipart/form-data`
- `file` (required): 이미지 파일 (PNG, JPEG 등)

**Response:** `200 OK`
```json
{
  "clubId": 1,
  "clubName": "알고리즘 동아리",
  "imageUrl": "http://localhost:9000/club-images/clubs/1/uuid.jpg"
}
```

**에러 응답:**
- `400 Bad Request`: 파일이 비어있습니다
- `400 Bad Request`: 이미지 파일만 업로드 가능합니다
- `404 Not Found`: 동아리를 찾을 수 없습니다

**참고:**
- MinIO를 사용하여 파일을 저장합니다.
- 업로드된 파일은 `clubs/{clubId}/` 폴더에 저장됩니다.
- 파일명은 UUID로 자동 생성됩니다.

---

### 4.10 동아리 이미지 파일 다운로드

**GET** `/api/clubs/{clubId}/image/download`

**Path Parameters:**
- `clubId`: 동아리 ID

**Response:** `200 OK`
- Content-Type: `image/jpeg` 또는 `image/png`
- Body: 이미지 파일 바이너리

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 동아리 이미지가 없습니다

---

### 4.11 동아리 이미지 삭제

**DELETE** `/api/clubs/{clubId}/image`

**Path Parameters:**
- `clubId`: 동아리 ID

**Response:** `200 OK` (빈 응답)

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다

**참고:**
- MinIO에서 파일을 삭제하고, 데이터베이스의 이미지 URL도 삭제합니다.

---

### 4.12 동아리 활동 생성

**POST** `/api/clubs/{clubId}/activities`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId`: 활동을 생성하는 사용자 ID (권한 확인용)

**Request Body:**
```json
{
  "title": "string (required, max 200자)",
  "description": "string (required, max 500자)",
  "content": "string (optional, max 2000자)",
  "tags": ["string"] (optional)
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "2024 알고리즘 대회",
  "description": "동아리 내부 알고리즘 대회",
  "content": "상세 설명 내용",
  "type": "IN_SCHOOL",
  "category": null,
  "organizer": null,
  "deadline": null,
  "startDate": null,
  "link": null,
  "imageUrl": null,
  "images": [],
  "tags": ["알고리즘", "대회"],
  "createdAt": "2024-12-01T10:00:00",
  "createdBy": {
    "userId": 1,
    "name": "김회장"
  }
}
```

**에러 응답:**
- `400 Bad Request`: 회장이나 관리자만 활동을 생성할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT), 부대표(VICE_PRESIDENT), 관리자(ADMIN)만 생성 가능
- 생성 후 사진을 업로드할 수 있습니다

---

### 4.13 동아리 활동 조회

**GET** `/api/clubs/{clubId}/activities`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `page` (optional, default: 0): 페이지 번호
- `size` (optional, default: 100): 페이지 크기

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "title": "2024 알고리즘 대회",
      "description": "동아리 내부 알고리즘 대회",
      "type": "IN_SCHOOL",
      "category": "COMPETITION",
      "organizer": "알고리즘 동아리",
      "deadline": "2024-12-31",
      "link": "https://example.com/activity1",
      "imageUrl": "https://example.com/activity1.jpg",
      "images": [
        {
          "id": 1,
          "url": "http://localhost:9000/club-images/clubs/1/activities/1/image1.jpg",
          "uploadedAt": "2024-12-01T10:00:00"
        }
      ],
      "tags": ["알고리즘", "대회"]
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 1
}
```

**참고:**
- `images`: 활동 사진 배열 (각 이미지의 ID 포함)
- 활동 목록에서도 썸네일 이미지를 표시할 수 있도록 `images` 배열이 포함됩니다

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다

---

### 4.14 동아리 활동 상세 조회

**GET** `/api/clubs/{clubId}/activities/{activityId}`

**Path Parameters:**
- `clubId`: 동아리 ID
- `activityId`: 활동 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "2024 알고리즘 대회",
  "description": "동아리 내부 알고리즘 대회",
  "content": "상세 설명 내용",
  "type": "IN_SCHOOL",
  "category": null,
  "organizer": null,
  "deadline": null,
  "startDate": null,
  "link": null,
  "imageUrl": null,
  "images": [
    {
      "id": 1,
      "url": "http://localhost:9000/club-images/clubs/1/activities/1/image1.jpg",
      "uploadedAt": "2024-12-01T10:00:00"
    }
  ],
  "tags": ["알고리즘", "대회"],
  "createdAt": "2024-12-01T10:00:00",
  "createdBy": {
    "userId": 1,
    "name": "김회장"
  }
}
```

**에러 응답:**
- `400 Bad Request`: 해당 동아리의 활동이 아닙니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 활동을 찾을 수 없습니다

**참고:**
- `images`: 활동 사진 배열 (각 이미지의 ID 포함)
- `createdBy`: 작성자 정보

---

### 4.15 동아리 부원 목록 조회

**GET** `/api/clubs/{clubId}/members`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `page` (optional, default: 0): 페이지 번호
- `size` (optional, default: 100): 페이지 크기

**Response:** `200 OK`
```json
{
  "content": [
    {
      "userId": 1,
      "name": "김회장",
      "email": "president@university.ac.kr",
      "major": "컴퓨터공학과",
      "role": "PRESIDENT",
      "joinedAt": "2024-01-01T00:00:00"
    },
    {
      "userId": 2,
      "name": "이부원",
      "email": "member@university.ac.kr",
      "major": "소프트웨어학과",
      "role": "MEMBER",
      "joinedAt": "2024-02-01T00:00:00"
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 2
}
```

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다

**참고:**
- `role`: `PRESIDENT` (회장), `VICE_PRESIDENT` (부대표), `ADMIN` (관리자), `MEMBER` (일반 부원)
- 회장이 항상 리스트의 첫 번째로 나옵니다 (정렬 기준: PRESIDENT > VICE_PRESIDENT > ADMIN > MEMBER)

---

### 4.16 동아리 가입 신청

**POST** `/api/clubs/{clubId}/applications`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId`: 사용자 ID

**Request Body:**
```json
{
  "message": "string (optional, max 500자) - 가입 신청 메시지"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "clubId": 1,
  "userId": 10,
  "userName": "김학생",
  "userEmail": "student1@university.ac.kr",
  "major": "컴퓨터공학과",
  "message": "동아리에 가입하고 싶습니다.",
  "status": "PENDING",
  "appliedAt": "2024-12-01T10:00:00",
  "approvedAt": null,
  "rejectedAt": null,
  "rejectReason": null
}
```

**에러 응답:**
- `400 Bad Request`: 동아리 회장이나 부원은 가입 신청할 수 없습니다
- `400 Bad Request`: 이미 가입 신청이 있습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 동아리 회장이나 이미 가입된 부원은 가입 신청할 수 없습니다.

---

### 4.17 동아리 가입 신청 목록 조회 (회장용)

**GET** `/api/clubs/{clubId}/applications`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId`: 회장의 사용자 ID (권한 확인용)
- `status` (optional): `PENDING`, `APPROVED`, `REJECTED` (필터링)
- `page` (optional, default: 0): 페이지 번호
- `size` (optional, default: 100): 페이지 크기

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "clubId": 1,
      "userId": 10,
      "userName": "김학생",
      "userEmail": "student1@university.ac.kr",
      "major": "컴퓨터공학과",
      "message": "동아리에 가입하고 싶습니다.",
      "status": "PENDING",
      "appliedAt": "2024-12-01T10:00:00",
      "approvedAt": null,
      "rejectedAt": null,
      "rejectReason": null
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 1
}
```

**에러 응답:**
- `400 Bad Request`: 회장만 가입 신청 목록을 조회할 수 있습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

**참고:**
- 동아리 회장만 조회 가능합니다.

---

### 4.18 동아리 가입 신청 승인 (회장용)

**PUT** `/api/clubs/{clubId}/applications/{applicationId}/approve`

**Path Parameters:**
- `clubId`: 동아리 ID
- `applicationId`: 가입 신청 ID

**Query Parameters:**
- `userId`: 승인하는 회장의 사용자 ID (권한 확인용)

**Response:** `200 OK`
```json
{
  "id": 1,
  "clubId": 1,
  "userId": 10,
  "userName": "김학생",
  "userEmail": "student1@university.ac.kr",
  "major": "컴퓨터공학과",
  "message": "동아리에 가입하고 싶습니다.",
  "status": "APPROVED",
  "appliedAt": "2024-12-01T10:00:00",
  "approvedAt": "2024-12-01T11:00:00",
  "rejectedAt": null,
  "rejectReason": null
}
```

**에러 응답:**
- `400 Bad Request`: 회장만 가입 신청을 승인할 수 있습니다
- `400 Bad Request`: 이미 처리된 신청입니다
- `404 Not Found`: 가입 신청을 찾을 수 없습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

**참고:**
- 승인 시 자동으로 동아리 멤버로 추가됩니다.

---

### 4.19 동아리 가입 신청 거절 (회장용)

**PUT** `/api/clubs/{clubId}/applications/{applicationId}/reject`

**Path Parameters:**
- `clubId`: 동아리 ID
- `applicationId`: 가입 신청 ID

**Query Parameters:**
- `userId`: 거절하는 회장의 사용자 ID (권한 확인용)

**Request Body (optional):**
```json
{
  "reason": "string (optional) - 거절 사유"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "clubId": 1,
  "userId": 10,
  "userName": "김학생",
  "userEmail": "student1@university.ac.kr",
  "major": "컴퓨터공학과",
  "message": "동아리에 가입하고 싶습니다.",
  "status": "REJECTED",
  "appliedAt": "2024-12-01T10:00:00",
  "approvedAt": null,
  "rejectedAt": "2024-12-01T11:00:00",
  "rejectReason": "정원이 마감되었습니다."
}
```

**에러 응답:**
- `400 Bad Request`: 회장만 가입 신청을 거절할 수 있습니다
- `400 Bad Request`: 이미 처리된 신청입니다
- `404 Not Found`: 가입 신청을 찾을 수 없습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

---

### 4.20 현재 사용자의 동아리 멤버십 확인

**GET** `/api/clubs/{clubId}/membership`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId`: 사용자 ID

**Response:** `200 OK`
```json
{
  "isMember": true,
  "role": "PRESIDENT",
  "joinedAt": "2024-01-01T00:00:00"
}
```

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- `role`: `PRESIDENT` (회장), `VICE_PRESIDENT` (부대표), `ADMIN` (관리자), `MEMBER` (일반 부원)
- `isMember`: `false`인 경우 `role`은 `null`

---

### 4.21 동아리 정보 수정

**PUT** `/api/clubs/{clubId}`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId`: 수정하는 사용자 ID (권한 확인용)

**Request Body:**
```json
{
  "description": "string (optional, max 500자)",
  "fullDescription": "string (optional, max 1000자)",
  "snsLink": "string (optional)",
  "isRecruiting": "boolean (optional)",
  "tags": ["string"] (optional)
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "알고리즘 동아리",
  "type": "CENTRAL",
  "department": "중앙동아리",
  "description": "수정된 설명",
  "fullDescription": "수정된 상세 설명",
  "imageUrl": "https://example.com/club1.jpg",
  "snsLink": "https://instagram.com/algorithm_club",
  "isRecruiting": true,
  "tags": ["개발", "프로그래밍", "알고리즘"]
}
```

**에러 응답:**
- `400 Bad Request`: 회장이나 관리자만 동아리 정보를 수정할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT) 또는 관리자(ADMIN)만 수정 가능
- `name`, `type`, `department`는 수정 불가 (동아리 생성 시에만 설정)

---

### 4.23 동아리 부원 권한 변경

**PUT** `/api/clubs/{clubId}/members/{memberUserId}/role`

**Path Parameters:**
- `clubId`: 동아리 ID
- `memberUserId`: 권한을 변경할 부원의 사용자 ID

**Query Parameters:**
- `userId`: 권한을 변경하는 회장의 사용자 ID (권한 확인용)

**Request Body:**
```json
{
  "role": "VICE_PRESIDENT"
}
```

**Response:** `200 OK`
```json
{
  "userId": 2,
  "name": "이부원",
  "email": "member@university.ac.kr",
  "major": "소프트웨어학과",
  "role": "VICE_PRESIDENT",
  "joinedAt": "2024-02-01T00:00:00"
}
```

**에러 응답:**
- `400 Bad Request`: 회장만 부원 권한을 변경할 수 있습니다
- `400 Bad Request`: 잘못된 역할입니다 (PRESIDENT, VICE_PRESIDENT, ADMIN, MEMBER 중 하나)
- `400 Bad Request`: 회장 권한은 변경할 수 없습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 부원을 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT)만 권한 변경 가능
- 회장 자신의 권한은 변경 불가
- `role` 값: `PRESIDENT` (회장), `VICE_PRESIDENT` (부대표), `ADMIN` (관리자), `MEMBER` (일반 부원)

---

### 4.22 동아리 부원 탈퇴 처리

**DELETE** `/api/clubs/{clubId}/members/{memberUserId}`

**Path Parameters:**
- `clubId`: 동아리 ID
- `memberUserId`: 탈퇴시킬 부원의 사용자 ID

**Query Parameters:**
- `userId`: 탈퇴 처리하는 회장의 사용자 ID (권한 확인용)

**Response:** `200 OK` (빈 응답)

**에러 응답:**
- `400 Bad Request`: 회장만 부원을 탈퇴시킬 수 있습니다
- `400 Bad Request`: 회장 자신은 탈퇴시킬 수 없습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 부원을 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT)만 부원 탈퇴 가능
- 회장 자신은 탈퇴 불가 (회장 변경 후 탈퇴 가능)
- 탈퇴된 부원은 동아리에서 제거됩니다

---

### 4.21 동아리 활동 사진 업로드

**POST** `/api/clubs/{clubId}/activities/{activityId}/image/upload`

**Path Parameters:**
- `clubId`: 동아리 ID
- `activityId`: 활동 ID

**Query Parameters:**
- `userId`: 업로드하는 사용자 ID (권한 확인용)

**Request:**
- Content-Type: `multipart/form-data`
- `file` (required): 이미지 파일 (PNG, JPEG 등)

**Response:** `200 OK`
```json
{
  "activityId": 1,
  "imageUrl": "http://localhost:9000/club-images/clubs/1/activities/1/uuid.jpg"
}
```

**에러 응답:**
- `400 Bad Request`: 회장이나 관리자만 활동 사진을 업로드할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `400 Bad Request`: 파일이 비어있습니다
- `400 Bad Request`: 이미지 파일만 업로드 가능합니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 활동을 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT) 또는 관리자(ADMIN)만 업로드 가능
- MinIO를 사용하여 파일을 저장합니다.
- 업로드된 파일은 `club-images/clubs/{clubId}/activities/{activityId}/` 폴더에 저장됩니다.
- 업로드된 이미지는 활동 상세 조회 응답의 `images` 배열에 포함됩니다.

---

### 4.22 동아리 활동 사진 업데이트 (교체)

**PUT** `/api/clubs/{clubId}/activities/{activityId}/images/{imageId}`

**Path Parameters:**
- `clubId`: 동아리 ID
- `activityId`: 활동 ID
- `imageId`: 업데이트할 이미지 ID (활동 상세 조회 응답의 `images` 배열에서 확인)

**Query Parameters:**
- `userId`: 업데이트하는 사용자 ID (권한 확인용)

**Request:**
- Content-Type: `multipart/form-data`
- `file` (required): 새 이미지 파일 (PNG, JPEG 등)

**Response:** `200 OK`
```json
{
  "activityId": 1,
  "imageUrl": "http://localhost:9000/club-images/clubs/1/activities/1/new-uuid.jpg"
}
```

**에러 응답:**
- `400 Bad Request`: 회장이나 관리자만 활동 사진을 업데이트할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `400 Bad Request`: 해당 동아리의 활동이 아닙니다
- `400 Bad Request`: 파일이 비어있습니다
- `400 Bad Request`: 이미지 파일만 업로드 가능합니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 활동을 찾을 수 없습니다
- `404 Not Found`: 활동 사진을 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT) 또는 관리자(ADMIN)만 업데이트 가능
- `imageId`는 활동 상세 조회 응답의 `images` 배열에서 확인할 수 있습니다
- 기존 MinIO 파일은 자동으로 삭제되고, 새 파일이 업로드됩니다
- 이미지 ID와 업로드 시간은 유지되고, URL만 업데이트됩니다

---

### 4.23 동아리 활동 사진 삭제

**DELETE** `/api/clubs/{clubId}/activities/{activityId}/images/{imageId}`

**Path Parameters:**
- `clubId`: 동아리 ID
- `activityId`: 활동 ID
- `imageId`: 삭제할 이미지 ID (활동 상세 조회 응답의 `images` 배열에서 확인)

**Query Parameters:**
- `userId`: 삭제하는 사용자 ID (권한 확인용)

**Response:** `200 OK` (빈 응답)

**에러 응답:**
- `400 Bad Request`: 회장이나 관리자만 활동 사진을 삭제할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `400 Bad Request`: 해당 동아리의 활동이 아닙니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 활동을 찾을 수 없습니다
- `404 Not Found`: 활동 사진을 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT) 또는 관리자(ADMIN)만 삭제 가능
- `imageId`는 활동 상세 조회 응답의 `images` 배열에서 확인할 수 있습니다
- MinIO에서 파일을 삭제하고, 데이터베이스의 이미지 레코드도 삭제합니다

---

## 5. 활동 API

### 5.1 활동 목록

**GET** `/api/activities`

**Query Parameters:**
- `type` (optional): `IN_SCHOOL` 또는 `OUT_SCHOOL`
- `category` (optional): `CONTEST`, `COMPETITION`, `VOLUNTEER`, `OTHER`
- `keyword` (optional): 검색 키워드 (제목, 설명에서 검색)
- `page` (optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (optional, default: 100): 페이지 크기 (해커톤용: 기본값 100으로 사실상 전체 조회)

**Response:** `200 OK` (항상 페이지네이션 객체)
```json
{
  "content": [
    {
      "id": 1,
      "title": "2024 교내 프로그래밍 경진대회",
      "description": "알고리즘 문제 해결 능력을 겨루는 대회",
      "type": "IN_SCHOOL",
      "category": "COMPETITION",
      "organizer": "컴퓨터공학과",
      "deadline": "2024-12-31",
      "link": "https://example.com/contest1",
      "imageUrl": "https://example.com/activity1.jpg",
      "tags": ["개발", "프로그래밍", "대회"]
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 15
}
```

**예시:**
- `/api/activities?type=IN_SCHOOL` - 교내 활동만 (기본 size=100)
- `/api/activities?type=IN_SCHOOL&page=0&size=10` - 교내 활동 (페이지네이션)
- `/api/activities?type=IN_SCHOOL&category=CONTEST` - 교내 공모전만
- `/api/activities?keyword=프로그래밍` - 키워드 검색 (제목/설명에서 검색)
- `/api/activities?type=IN_SCHOOL&keyword=해커톤` - 교내 활동 중 키워드 검색

**에러 응답:**
- `404 Not Found`: 활동을 찾을 수 없습니다 (잘못된 activityId)

---

### 5.2 활동 상세

**GET** `/api/activities/{activityId}`

**Path Parameters:**
- `activityId`: 활동 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "2024 교내 프로그래밍 경진대회",
  "description": "알고리즘 문제 해결 능력을 겨루는 대회",
  "content": "컴퓨터공학과 주최 프로그래밍 경진대회입니다. 다양한 난이도의 문제가 출제됩니다.",
  "type": "IN_SCHOOL",
  "category": "COMPETITION",
  "organizer": "컴퓨터공학과",
  "deadline": "2024-12-31",
  "startDate": "2024-12-31",
  "link": "https://example.com/contest1",
  "imageUrl": "https://example.com/activity1.jpg",
  "images": [
    {
      "id": 1,
      "url": "http://localhost:9000/club-images/clubs/1/activities/1/image1.jpg",
      "uploadedAt": "2024-12-01T10:00:00"
    }
  ],
  "tags": ["개발", "프로그래밍", "대회"],
  "createdAt": "2024-12-01T10:00:00",
  "createdBy": {
    "userId": 1,
    "name": "김회장"
  }
}
```

**참고:**
- `images`: 활동 사진 배열 (각 이미지의 ID 포함)
- `createdBy`: 작성자 정보
- `imageUrl`: 레거시 호환용 (대표 이미지)

---

### 5.3 동아리 활동 수정

**PUT** `/api/clubs/{clubId}/activities/{activityId}`

**Path Parameters:**
- `clubId`: 동아리 ID
- `activityId`: 활동 ID

**Query Parameters:**
- `userId`: 수정하는 사용자 ID (권한 확인용)

**Request Body:**
```json
{
  "title": "string (optional, max 200자)",
  "description": "string (optional, max 500자)",
  "content": "string (optional, max 2000자)",
  "tags": ["string"] (optional)
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "수정된 제목",
  "description": "수정된 설명",
  "content": "수정된 상세 내용",
  "type": "IN_SCHOOL",
  "category": "COMPETITION",
  "organizer": "컴퓨터공학과",
  "deadline": "2024-12-31",
  "startDate": "2024-12-31",
  "link": "https://example.com/contest1",
  "imageUrl": "https://example.com/activity1.jpg",
  "images": [
    {
      "id": 1,
      "url": "http://localhost:9000/club-images/clubs/1/activities/1/image1.jpg",
      "uploadedAt": "2024-12-01T10:00:00"
    }
  ],
  "tags": ["알고리즘", "대회"],
  "createdAt": "2024-12-01T10:00:00",
  "createdBy": {
    "userId": 1,
    "name": "김회장"
  }
}
```

**에러 응답:**
- `400 Bad Request`: 작성자나 관리자만 활동을 수정할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `400 Bad Request`: 해당 동아리의 활동이 아닙니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 활동을 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 작성자(createdBy.userId) 또는 회장(PRESIDENT), 부대표(VICE_PRESIDENT), 관리자(ADMIN)만 수정 가능
- 사진은 별도 API로 추가/삭제

---

### 5.4 동아리 활동 삭제

**DELETE** `/api/clubs/{clubId}/activities/{activityId}`

**Path Parameters:**
- `clubId`: 동아리 ID
- `activityId`: 활동 ID

**Query Parameters:**
- `userId`: 삭제하는 사용자 ID (권한 확인용)

**Response:** `200 OK` (빈 응답)

**에러 응답:**
- `400 Bad Request`: 작성자나 관리자만 활동을 삭제할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `400 Bad Request`: 해당 동아리의 활동이 아닙니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 활동을 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 작성자(createdBy.userId) 또는 회장(PRESIDENT), 부대표(VICE_PRESIDENT), 관리자(ADMIN)만 삭제 가능
- 활동 삭제 시 관련된 모든 사진도 함께 삭제됩니다 (MinIO 및 DB)

---

### 5.5 동아리 활동 사진 삭제

**DELETE** `/api/clubs/{clubId}/activities/{activityId}/images/{imageId}`

**Path Parameters:**
- `clubId`: 동아리 ID
- `activityId`: 활동 ID
- `imageId`: 이미지 ID (활동 상세 조회 응답의 `images` 배열에서 확인)

**Query Parameters:**
- `userId`: 삭제하는 사용자 ID (권한 확인용)

**Response:** `200 OK` (빈 응답)

**에러 응답:**
- `400 Bad Request`: 회장이나 관리자만 활동 사진을 삭제할 수 있습니다
- `400 Bad Request`: 동아리 부원이 아닙니다
- `400 Bad Request`: 해당 동아리의 활동이 아닙니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 활동을 찾을 수 없습니다
- `404 Not Found`: 활동 사진을 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 회장(PRESIDENT) 또는 관리자(ADMIN)만 삭제 가능
- `imageId`는 활동 상세 조회 응답의 `images` 배열에서 확인할 수 있습니다
- MinIO에서 파일을 삭제하고, 데이터베이스의 이미지 레코드도 삭제합니다

---

## 6. 캘린더 API

### 6.1 캘린더 이벤트 조회

**GET** `/api/calendar`

**Query Parameters:**
- `from` (required): 시작 날짜 (YYYY-MM-DD)
- `to` (required): 종료 날짜 (YYYY-MM-DD)

**Response:** `200 OK` (배열)
```json
[
  {
    "id": 1,
    "title": "2024 교내 프로그래밍 경진대회",
    "targetType": "ACTIVITY",
    "targetId": 1,
    "date": "2024-12-31"
  }
]
```

**참고:**
- `targetType`: `ACTIVITY`, `CLUB_NOTICE` 등
- `targetId`: 해당 타입의 ID
- 프론트에서 `targetType === "ACTIVITY"`면 `/activities/{targetId}`로 라우팅

**예시:**
- `/api/calendar?from=2024-01-01&to=2024-12-31`

---

## 7. 취업 정보 API

### 7.1 취업 정보 목록

**GET** `/api/jobs`

**Query Parameters:**
- `page` (optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (optional, default: 100): 페이지 크기 (해커톤용: 기본값 100으로 사실상 전체 조회)

**Response:** `200 OK` (항상 페이지네이션 객체)
```json
{
  "content": [
    {
      "id": 1,
      "companyName": "테크 스타트업 A",
      "position": "백엔드 개발자",
      "description": "Spring Boot 기반 백엔드 개발자 채용",
      "location": "서울",
      "deadline": "2024-12-31",
      "link": "https://example.com/job1",
      "tags": ["백엔드", "Spring", "Java"]
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 25
}
```

**에러 응답:**
- `404 Not Found`: 채용 공고를 찾을 수 없습니다 (잘못된 jobId)

---

### 7.2 취업 정보 상세

**GET** `/api/jobs/{jobId}`

**Path Parameters:**
- `jobId`: 채용 공고 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "companyName": "테크 스타트업 A",
  "position": "백엔드 개발자",
  "description": "Spring Boot 기반 백엔드 개발자 채용",
  "content": "신입 개발자를 채용합니다. Spring Boot, JPA 경험이 있으면 우대합니다.",
  "location": "서울",
  "deadline": "2024-12-31",
  "link": "https://example.com/job1",
  "tags": ["백엔드", "Spring", "Java"]
}
```

---

## 8. 공지방 API

### 8.1 내가 속한 동아리 공지

**GET** `/api/me/notices`

**Query Parameters:**
- `userId` (required): 사용자 ID
- `page` (optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (optional, default: 100): 페이지 크기 (해커톤용: 기본값 100으로 사실상 전체 조회)

**Response:** `200 OK` (항상 페이지네이션 객체)
```json
{
  "content": [
    {
      "id": 1,
      "clubId": 1,
      "clubName": "알고리즘 동아리",
      "title": "알고리즘 스터디 모집",
      "content": "매주 화요일 오후 7시에 알고리즘 문제를 함께 풀어요!",
      "author": "동아리장",
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 8
}
```

**에러 응답:**
- `404 Not Found`: 사용자를 찾을 수 없습니다 (잘못된 userId)

---

### 8.2 특정 동아리 공지 목록

**GET** `/api/clubs/{clubId}/notices`

**Path Parameters:**
- `clubId`: 동아리 ID

**Response:** `200 OK` (배열 - 특정 동아리 공지는 공지 수가 많지 않으므로 배열로 반환)
```json
[
  {
    "id": 1,
    "clubId": 1,
    "clubName": "알고리즘 동아리",
    "title": "알고리즘 스터디 모집",
    "content": "매주 화요일 오후 7시에 알고리즘 문제를 함께 풀어요!",
    "author": "동아리장",
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

**참고:** `/api/me/notices`는 여러 동아리 공지를 합산하므로 페이지네이션을 사용하지만, 특정 동아리 공지는 배열로 반환합니다.

**에러 응답:**
- `404 Not Found`: 동아리를 찾을 수 없습니다 (잘못된 clubId)

---

### 8.3 공지 상세

**GET** `/api/notices/{noticeId}`

**Path Parameters:**
- `noticeId`: 공지 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "clubId": 1,
  "clubName": "알고리즘 동아리",
  "title": "알고리즘 스터디 모집",
  "content": "매주 화요일 오후 7시에 알고리즘 문제를 함께 풀어요!",
  "author": "동아리장",
  "createdAt": "2024-01-01T10:00:00"
}
```

---

## 9. 채팅 API

### 9.1 채팅방 생성/조회

**GET** `/api/clubs/{clubId}/chat-rooms`

**Path Parameters:**
- `clubId`: 동아리 ID

**Query Parameters:**
- `userId`: 사용자 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "clubId": 1,
  "userId": 10,
  "clubName": "알고리즘 동아리",
  "createdAt": "2024-12-01T10:00:00"
}
```

**에러 응답:**
- `400 Bad Request`: 동아리 회장이나 부원은 채팅방을 생성할 수 없습니다
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다

**참고:**
- 기존 채팅방이 있으면 반환, 없으면 새로 생성하여 반환합니다.
- 동아리 회장이나 이미 가입된 부원은 채팅방을 생성할 수 없습니다.

---

### 9.2 채팅방 메시지 조회 (회장용)

**GET** `/api/chat-rooms/{chatRoomId}/messages`

**Path Parameters:**
- `chatRoomId`: 채팅방 ID

**Query Parameters:**
- `userId`: 회장의 사용자 ID (권한 확인용)
- `page` (optional, default: 0): 페이지 번호
- `size` (optional, default: 100): 페이지 크기

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "chatRoomId": 1,
      "senderId": 10,
      "senderName": "김학생",
      "message": "안녕하세요! 동아리에 관심이 있어서 문의드립니다.",
      "timestamp": "2024-12-01T10:00:00"
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 1
}
```

**에러 응답:**
- `400 Bad Request`: 회장만 채팅 메시지를 조회할 수 있습니다
- `404 Not Found`: 채팅방을 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

**참고:**
- 동아리 회장만 조회 가능합니다.

---

### 9.3 채팅 메시지 전송 (회장용)

**POST** `/api/chat-rooms/{chatRoomId}/messages`

**Path Parameters:**
- `chatRoomId`: 채팅방 ID

**Query Parameters:**
- `userId`: 전송하는 회장의 사용자 ID (권한 확인용)

**Request Body:**
```json
{
  "message": "string (required, max 1000자)"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "chatRoomId": 1,
  "senderId": 1,
  "senderName": "김회장",
  "message": "안녕하세요!",
  "timestamp": "2024-12-01T10:00:00"
}
```

**에러 응답:**
- `400 Bad Request`: 회장만 채팅 메시지를 전송할 수 있습니다
- `404 Not Found`: 채팅방을 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다
- `404 Not Found`: 동아리 회장을 찾을 수 없습니다

**참고:**
- 동아리 회장만 전송 가능합니다.

---

## 10. 인재 프로필 API

### 10.1 인재 프로필 목록

**GET** `/api/talents`

**Query Parameters:**
- `page` (optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (optional, default: 100): 페이지 크기 (해커톤용: 기본값 100으로 사실상 전체 조회)

**Response:** `200 OK` (항상 페이지네이션 객체)
```json
{
  "content": [
    {
      "id": 1,
      "userName": "김학생",
      "major": "컴퓨터공학과",
      "introduction": "백엔드 개발에 관심이 많은 컴공과 학생입니다.",
      "skills": ["Java", "Spring Boot", "MySQL", "Redis"],
      "currentAffiliation": "웹 개발 동아리",
      "portfolioLink": "https://github.com/student1",
      "availableProjectTypes": ["웹 개발", "API 개발", "백엔드"]
    }
  ],
  "page": 0,
  "size": 100,
  "totalElements": 12
}
```

**에러 응답:**
- `404 Not Found`: 인재 프로필을 찾을 수 없습니다 (잘못된 talentId)

---

### 10.2 인재 프로필 상세

**GET** `/api/talents/{talentId}`

**Path Parameters:**
- `talentId`: 인재 프로필 ID

**Response:** `200 OK`
```json
{
  "id": 1,
  "userName": "김학생",
  "major": "컴퓨터공학과",
  "introduction": "백엔드 개발에 관심이 많은 컴공과 학생입니다.",
  "skills": ["Java", "Spring Boot", "MySQL", "Redis"],
  "currentAffiliation": "웹 개발 동아리",
  "portfolioLink": "https://github.com/student1",
  "availableProjectTypes": ["웹 개발", "API 개발", "백엔드"]
}
```

---

## 에러 응답 형식

모든 API는 에러 발생 시 다음 형식으로 응답합니다:

```json
{
  "message": "에러 메시지",
  "status": "error"
}
```

**HTTP 상태 코드:**
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청 (유효성 검사 실패, 파라미터 오류 등)
- `404 Not Found`: 리소스를 찾을 수 없음 (존재하지 않는 ID 등)

---

## 더미 데이터 ID 참고

### 사용자
- userId: 1, 2, 3

### 동아리
- clubId: 1, 2, 3, 4

### 활동
- activityId: 1, 2, 3 (교내), 4, 5 (교외)

### 취업 정보
- jobId: 1, 2, 3

### 공지
- noticeId: 1, 2, 3

### 인재 프로필
- talentId: 1, 2, 3

---

## 참고사항

1. **인증**: 현재는 해커톤용으로 간단하게 구현되어 있습니다. 실제 프로덕션에서는 JWT 토큰 등을 사용해야 합니다.

2. **비밀번호**: 현재는 평문으로 저장되어 있습니다. 실제 프로덕션에서는 해싱이 필요합니다.

3. **CORS**: 모든 API는 CORS가 설정되어 있어 프론트엔드에서 바로 호출 가능합니다.

4. **데이터베이스**: H2 인메모리 데이터베이스를 사용하며, 애플리케이션 시작 시 더미 데이터가 자동으로 생성됩니다.

5. **페이지네이션**: 
   - **모든 목록 API는 항상 페이지네이션 객체로 응답합니다** (프론트 개발 편의성).
   - `page`는 0부터 시작합니다.
   - 기본 `size`는 100입니다 (해커톤용: 사실상 전체 조회 가능).
   - 프론트에서 `size`를 크게 설정하여 전체 조회처럼 사용할 수 있습니다.
   - 예외: `/api/clubs/{clubId}/notices`는 배열로 반환 (특정 동아리 공지는 공지 수가 많지 않음).

6. **운영자용 쓰기 API**: 
   - 현재는 조회/문의 중심의 API만 제공됩니다.
   - 동아리/활동/취업공고/공지 등록은 서버 시작 시 더미 데이터(DataInitializer)로 처리됩니다.
   - 운영자용 관리 API는 향후 계획으로 발표 시 언급 가능합니다.

7. **향후 확장 계획** (발표용 설명):
   - **동아리 가입 관리**: 현재는 더미 데이터로 회원-동아리 관계를 판단합니다. 추후 `GET /api/me/clubs` (내가 가입한 동아리 리스트) 및 가입 API 추가 예정.
   - **문의 조회**: 운영진이 문의를 읽는 `GET /api/clubs/{clubId}/inquiries` (운영자 전용) 추가 예정.
   - **인재 프로필 생성/수정**: 학생이 직접 프로필을 등록하는 `POST /api/talents`, `PUT /api/talents/{id}` 추가 예정.

