# 교내 동아리 플랫폼 API 문서

## 포스트맨 컬렉션 사용 방법

1. 포스트맨을 실행합니다.
2. `Import` 버튼을 클릭합니다.
3. `ClubPlatform.postman_collection.json` 파일을 선택하여 import합니다.
4. 컬렉션이 추가되면 바로 사용할 수 있습니다.

## 환경 변수

- `baseUrl`: 기본값은 `http://localhost:8080`입니다.
- 필요시 포스트맨에서 환경 변수를 수정할 수 있습니다.

## 더미 데이터

애플리케이션 시작 시 자동으로 다음 더미 데이터가 생성됩니다:

### 사용자
- `student1@university.ac.kr` / `password123` (컴퓨터공학과, 개발/프로그래밍 관심)
- `student2@university.ac.kr` / `password123` (디자인학과, 디자인/예술 관심)
- `student3@university.ac.kr` / `password123` (컴퓨터공학과, 개발/AI 관심)

### 동아리
- 알고리즘 동아리 (중앙, 개발/프로그래밍/알고리즘)
- 웹 개발 동아리 (중앙, 개발/웹/프로젝트)
- 디자인 스튜디오 (중앙, 디자인/예술/UI/UX)
- 컴공과 밴드 (학과, 밴드/음악/공연)

### 활동
- 교내: 프로그래밍 경진대회, 웹 개발 해커톤, 디자인 공모전
- 교외: 전국 대학생 프로그래밍 대회, 스타트업 해커톤

### 기타
- 취업 정보 3개
- 동아리 공지 3개
- 인재 프로필 3개

## 주요 API 엔드포인트

### 인증
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인

### 홈
- `GET /api/home/recommendations` - 홈 추천 (옵션: userId)

### 동아리
- `GET /api/clubs` - 동아리 목록 (필터: type, tag, keyword)
- `GET /api/clubs/{clubId}` - 동아리 상세
- `POST /api/clubs/{clubId}/inquiries` - 동아리 문의

### 활동
- `GET /api/activities?type=IN_SCHOOL` - 교내 활동
- `GET /api/activities?type=OUT_SCHOOL` - 교외 활동
- `GET /api/activities/{activityId}` - 활동 상세

### 캘린더
- `GET /api/calendar?from=YYYY-MM-DD&to=YYYY-MM-DD` - 캘린더 이벤트

### 취업 정보
- `GET /api/jobs` - 취업 정보 목록
- `GET /api/jobs/{jobId}` - 취업 정보 상세

### 공지방
- `GET /api/me/notices?userId={userId}` - 내가 속한 동아리 공지
- `GET /api/clubs/{clubId}/notices` - 특정 동아리 공지
- `GET /api/notices/{noticeId}` - 공지 상세

### 인재 프로필
- `GET /api/talents` - 인재 프로필 목록
- `GET /api/talents/{talentId}` - 인재 프로필 상세

## 테스트 팁

1. 먼저 로그인 API로 사용자 인증을 테스트하세요.
2. userId가 필요한 API는 더미 데이터의 userId (1, 2, 3)를 사용하세요.
3. 동아리 ID는 1~4, 활동 ID는 1~5 범위입니다.
4. 모든 API는 CORS가 설정되어 있어 프론트엔드에서 바로 호출 가능합니다.

