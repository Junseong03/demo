# MinIO 설정 가이드

## MinIO란?

MinIO는 S3 호환 오브젝트 스토리지 서버입니다. 이미지 파일을 저장하고 관리하기 위해 사용됩니다.

## 설치 및 실행

### Docker를 사용하는 경우 (권장)

```bash
docker run -d \
  -p 9000:9000 \
  -p 9001:9001 \
  --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data --console-address ":9001"
```

### 직접 설치하는 경우

1. [MinIO 공식 사이트](https://min.io/download)에서 다운로드
2. 실행:
   ```bash
   minio server /data --console-address ":9001"
   ```

## 접속 정보

- **API 엔드포인트**: `http://localhost:9000`
- **콘솔**: `http://localhost:9001`
- **기본 사용자명**: `minioadmin`
- **기본 비밀번호**: `minioadmin`

## 애플리케이션 설정

`application.yml`에서 MinIO 설정을 변경할 수 있습니다:

```yaml
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: club-images
```

## 버킷 생성

애플리케이션을 실행하면 자동으로 `club-images` 버킷이 생성됩니다.

또는 MinIO 콘솔(`http://localhost:9001`)에서 수동으로 생성할 수도 있습니다.

## 파일 저장 구조

업로드된 이미지는 다음 구조로 저장됩니다:

```
club-images/
  └── clubs/
      └── {clubId}/
          └── {uuid}.{extension}
```

예: `club-images/clubs/1/550e8400-e29b-41d4-a716-446655440000.jpg`

## 주의사항

- 해커톤용이므로 기본 설정을 사용합니다.
- 프로덕션 환경에서는 보안 설정을 강화해야 합니다.
- MinIO 서버가 실행 중이어야 파일 업로드/다운로드가 작동합니다.

