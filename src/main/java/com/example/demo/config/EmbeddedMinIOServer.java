package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class EmbeddedMinIOServer implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {
    
    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;
    
    @Value("${minio.access-key:minioadmin}")
    private String accessKey;
    
    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;
    
    private Process minioProcess;
    private Path minioDataDir;
    
    @Override
    public void run(String... args) {
        try {
            // MinIO 데이터 디렉토리 생성
            minioDataDir = Paths.get(System.getProperty("user.home"), ".minio", "data");
            Files.createDirectories(minioDataDir);
            log.info("MinIO 데이터 디렉토리: {}", minioDataDir);
            
            // MinIO 바이너리 경로 확인
            String os = System.getProperty("os.name").toLowerCase();
            
            // Windows인 경우 MinIO 서버 자동 시작 시도
            if (os.contains("win")) {
                startMinIOOnWindows();
                return;
            }
            
            // Linux/Mac인 경우 MinIO 서버 실행 시도
            String minioBinary = findMinIOBinary();
            if (minioBinary == null) {
                log.warn("MinIO 바이너리를 찾을 수 없습니다. MinIO 서버를 수동으로 실행하세요.");
                log.warn("MinIO 서버가 이미 실행 중인지 확인하세요: {}", endpoint);
                return;
            }
            
            // MinIO 서버 실행
            startMinIOServer(minioBinary);
            
        } catch (Exception e) {
            log.error("MinIO 서버 시작 실패: {}", e.getMessage(), e);
            log.warn("MinIO 서버를 수동으로 실행하거나 이미 실행 중인지 확인하세요: {}", endpoint);
        }
    }
    
    private String findMinIOBinary() {
        String os = System.getProperty("os.name").toLowerCase();
        
        // Windows인 경우
        if (os.contains("win")) {
            Path minioBinary = Paths.get(System.getProperty("user.home"), ".minio", "minio.exe");
            if (Files.exists(minioBinary)) {
                return minioBinary.toString();
            }
        }
        
        // 시스템 PATH에서 minio 명령어 찾기
        String[] paths = System.getenv("PATH").split(File.pathSeparator);
        for (String path : paths) {
            File minioFile = new File(path, "minio");
            if (minioFile.exists() && minioFile.canExecute()) {
                return minioFile.getAbsolutePath();
            }
        }
        return null;
    }
    
    private void startMinIOServer(String minioBinary) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(minioBinary);
        command.add("server");
        command.add(minioDataDir.toString());
        command.add("--address");
        command.add(":9000");
        command.add("--console-address");
        command.add(":9001");
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.environment().put("MINIO_ROOT_USER", accessKey);
        processBuilder.environment().put("MINIO_ROOT_PASSWORD", secretKey);
        processBuilder.redirectErrorStream(true);
        // 출력을 파일로 리다이렉트 (선택사항)
        Path logFile = Paths.get(System.getProperty("user.home"), ".minio", "minio.log");
        processBuilder.redirectOutput(logFile.toFile());
        
        minioProcess = processBuilder.start();
        log.info("MinIO 서버가 시작되었습니다. 엔드포인트: {}", endpoint);
        log.info("MinIO 콘솔: http://localhost:9001");
        log.info("MinIO 로그: {}", logFile);
        
        // 서버가 시작될 때까지 대기
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void startMinIOOnWindows() {
        try {
            log.info("Windows 환경에서 MinIO 서버 자동 시작을 시도합니다...");
            
            // MinIO 바이너리 경로
            Path minioHome = Paths.get(System.getProperty("user.home"), ".minio");
            Path minioBinary = minioHome.resolve("minio.exe");
            
            // MinIO 바이너리가 없으면 다운로드
            if (!Files.exists(minioBinary)) {
                log.info("MinIO 바이너리를 다운로드하는 중...");
                downloadMinIOForWindows(minioHome, minioBinary);
            }
            
            // MinIO 서버 실행
            if (Files.exists(minioBinary)) {
                startMinIOServer(minioBinary.toString());
            } else {
                log.warn("MinIO 바이너리를 찾을 수 없습니다. MinIO 서버를 수동으로 실행하세요.");
                log.warn("MinIO 서버가 이미 실행 중인지 확인: {}", endpoint);
                log.warn("또는 Docker를 사용: docker run -d -p 9000:9000 -p 9001:9001 -e \"MINIO_ROOT_USER=minioadmin\" -e \"MINIO_ROOT_PASSWORD=minioadmin\" minio/minio server /data --console-address \":9001\"");
            }
            
        } catch (Exception e) {
            log.error("MinIO 서버 시작 실패: {}", e.getMessage(), e);
            log.warn("MinIO 서버를 수동으로 실행하거나 이미 실행 중인지 확인하세요: {}", endpoint);
        }
    }
    
    private void downloadMinIOForWindows(Path minioHome, Path minioBinary) {
        try {
            Files.createDirectories(minioHome);
            
            // MinIO Windows 바이너리 다운로드 URL
            String downloadUrl = "https://dl.min.io/server/minio/release/windows-amd64/minio.exe";
            
            log.info("MinIO 다운로드 중: {}", downloadUrl);
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            try (java.io.InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, minioBinary, StandardCopyOption.REPLACE_EXISTING);
                // 실행 권한 부여 (Windows에서는 필요 없지만, 호환성을 위해)
                minioBinary.toFile().setExecutable(true);
                log.info("MinIO 바이너리 다운로드 완료: {}", minioBinary);
            }
        } catch (Exception e) {
            log.error("MinIO 바이너리 다운로드 실패: {}", e.getMessage(), e);
            log.warn("MinIO 서버를 수동으로 다운로드하고 실행하세요: https://min.io/download");
        }
    }
    
    @Override
    public void onApplicationEvent(@org.springframework.lang.NonNull ContextClosedEvent event) {
        if (minioProcess != null && minioProcess.isAlive()) {
            log.info("MinIO 서버를 종료하는 중...");
            minioProcess.destroy();
            try {
                minioProcess.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                minioProcess.destroyForcibly();
            }
            log.info("MinIO 서버가 종료되었습니다.");
        }
    }
}

