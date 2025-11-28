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
- `404 Not Found`: 동아리를 찾을 수 없습니다
- `404 Not Found`: 사용자를 찾을 수 없습니다
```json
{
  "message": "동아리를 찾을 수 없습니다.",
  "status": "error"
}
```

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
- 기존 회장은 자동으로 일반 멤버(MEMBER)로 변경됩니다.
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
- 해커톤용으로 이미지 URL만 저장합니다. 실제 파일 업로드는 향후 구현 예정입니다.

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
  "tags": ["개발", "프로그래밍", "대회"]
}
```

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

## 9. 인재 프로필 API

### 9.1 인재 프로필 목록

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

### 9.2 인재 프로필 상세

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

