# 교내 동아리 플랫폼 API 명세서

## 기본 정보

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **인증 방식**: 현재는 userId를 쿼리 파라미터로 전달 (해커톤용 간단 구현)

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
- `size` (optional, default: 10): 페이지 크기

**Response (페이지네이션 없음):** `200 OK`
```json
[
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
]
```

**Response (페이지네이션):** `200 OK`
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
  "size": 10,
  "totalElements": 42
}
```

**예시:**
- `/api/clubs?type=CENTRAL` - 중앙 동아리만 (기본 배열 반환)
- `/api/clubs?type=CENTRAL&page=0&size=10` - 중앙 동아리 (페이지네이션)
- `/api/clubs?type=CENTRAL&tag=개발` - 중앙 동아리 중 개발 태그
- `/api/clubs?keyword=알고리즘` - 키워드 검색

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
```json
{
  "message": "동아리를 찾을 수 없습니다.",
  "status": "error"
}
```

---

## 5. 활동 API

### 5.1 활동 목록

**GET** `/api/activities`

**Query Parameters:**
- `type` (optional): `IN_SCHOOL` 또는 `OUT_SCHOOL`
- `category` (optional): `CONTEST`, `COMPETITION`, `VOLUNTEER`, `OTHER`
- `page` (optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (optional, default: 10): 페이지 크기

**Response (페이지네이션 없음):** `200 OK`
```json
[
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
]
```

**Response (페이지네이션):** `200 OK`
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 15
}
```

**예시:**
- `/api/activities?type=IN_SCHOOL` - 교내 활동만 (기본 배열 반환)
- `/api/activities?type=IN_SCHOOL&page=0&size=10` - 교내 활동 (페이지네이션)
- `/api/activities?type=IN_SCHOOL&category=CONTEST` - 교내 공모전만

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

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "2024 교내 프로그래밍 경진대회",
    "type": "ACTIVITY",
    "date": "2024-12-31",
    "link": "/api/activities/1"
  }
]
```

**예시:**
- `/api/calendar?from=2024-01-01&to=2024-12-31`

---

## 7. 취업 정보 API

### 7.1 취업 정보 목록

**GET** `/api/jobs`

**Query Parameters:**
- `page` (optional, default: 0): 페이지 번호 (0부터 시작)
- `size` (optional, default: 10): 페이지 크기

**Response (페이지네이션 없음):** `200 OK`
```json
[
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
```

**Response (페이지네이션):** `200 OK`
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 25
}
```

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
- `size` (optional, default: 10): 페이지 크기

**Response (페이지네이션 없음):** `200 OK`
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

**Response (페이지네이션):** `200 OK`
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 8
}
```

---

### 8.2 특정 동아리 공지 목록

**GET** `/api/clubs/{clubId}/notices`

**Path Parameters:**
- `clubId`: 동아리 ID

**Response:** `200 OK`
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
- `size` (optional, default: 10): 페이지 크기

**Response (페이지네이션 없음):** `200 OK`
```json
[
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
]
```

**Response (페이지네이션):** `200 OK`
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 12
}
```

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
- `400 Bad Request`: 잘못된 요청 (유효성 검사 실패, 리소스 없음 등)

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
   - `page`와 `size` 파라미터를 전달하면 페이지네이션 응답 형식으로 반환됩니다.
   - 파라미터를 전달하지 않으면 기본적으로 배열 형식으로 반환됩니다 (하위 호환성).
   - `page`는 0부터 시작합니다.
   - 기본 `size`는 10입니다.

6. **운영자용 쓰기 API**: 
   - 현재는 조회/문의 중심의 API만 제공됩니다.
   - 동아리/활동/취업공고/공지 등록은 서버 시작 시 더미 데이터(DataInitializer)로 처리됩니다.
   - 운영자용 관리 API는 향후 계획으로 발표 시 언급 가능합니다.

