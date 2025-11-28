package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ActivityRepository activityRepository;
    private final JobPostRepository jobPostRepository;
    private final ClubNoticeRepository clubNoticeRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final TalentProfileRepository talentProfileRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    @Transactional
    public void initializeData() {
        // 사용자 생성
        User user1 = User.builder()
                .email("student1@university.ac.kr")
                .password("password123")
                .name("김학생")
                .major("컴퓨터공학과")
                .interestTags(List.of("개발", "프로그래밍"))
                .build();

        User user2 = User.builder()
                .email("student2@university.ac.kr")
                .password("password123")
                .name("이학생")
                .major("디자인학과")
                .interestTags(List.of("디자인", "예술"))
                .build();

        User user3 = User.builder()
                .email("student3@university.ac.kr")
                .password("password123")
                .name("박학생")
                .major("컴퓨터공학과")
                .interestTags(List.of("개발", "AI"))
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        // 동아리 생성
        Club club1 = Club.builder()
                .name("알고리즘 동아리")
                .type(Club.ClubType.CENTRAL)
                .department("중앙동아리")
                .description("알고리즘 문제 해결 및 코딩 테스트 준비")
                .fullDescription("알고리즘 문제 해결을 통해 코딩 실력을 향상시키고, 코딩 테스트를 준비하는 동아리입니다.")
                .imageUrl("https://example.com/club1.jpg")
                .snsLink("https://instagram.com/algorithm_club")
                .isRecruiting(true)
                .tags(List.of("개발", "프로그래밍", "알고리즘"))
                .build();

        Club club2 = Club.builder()
                .name("웹 개발 동아리")
                .type(Club.ClubType.CENTRAL)
                .department("중앙동아리")
                .description("웹 개발 프로젝트를 함께 만들어요")
                .fullDescription("프론트엔드와 백엔드 개발을 함께 배우고 프로젝트를 진행하는 동아리입니다.")
                .imageUrl("https://example.com/club2.jpg")
                .snsLink("https://instagram.com/webdev_club")
                .isRecruiting(true)
                .tags(List.of("개발", "웹", "프로젝트"))
                .build();

        Club club3 = Club.builder()
                .name("디자인 스튜디오")
                .type(Club.ClubType.CENTRAL)
                .department("중앙동아리")
                .description("UI/UX 디자인과 그래픽 디자인")
                .fullDescription("UI/UX 디자인과 그래픽 디자인을 배우고 실무 프로젝트에 참여하는 동아리입니다.")
                .imageUrl("https://example.com/club3.jpg")
                .snsLink("https://instagram.com/design_studio")
                .isRecruiting(true)
                .tags(List.of("디자인", "예술", "UI/UX"))
                .build();

        Club club4 = Club.builder()
                .name("컴공과 밴드")
                .type(Club.ClubType.DEPARTMENT)
                .department("컴퓨터공학과")
                .description("컴공과 학생들의 밴드 동아리")
                .fullDescription("음악을 사랑하는 컴공과 학생들이 모여 밴드를 구성하고 공연을 준비합니다.")
                .imageUrl("https://example.com/club4.jpg")
                .isRecruiting(false)
                .tags(List.of("밴드", "음악", "공연"))
                .build();

        club1 = clubRepository.save(club1);
        club2 = clubRepository.save(club2);
        club3 = clubRepository.save(club3);
        club4 = clubRepository.save(club4);

        // 동아리 멤버 생성
        ClubMember member1 = ClubMember.builder()
                .user(user1)
                .club(club1)
                .role(ClubMember.MemberRole.MEMBER)
                .joinedAt(LocalDateTime.now().minusMonths(2))
                .build();

        ClubMember member2 = ClubMember.builder()
                .user(user1)
                .club(club2)
                .role(ClubMember.MemberRole.ADMIN)
                .joinedAt(LocalDateTime.now().minusMonths(6))
                .build();

        ClubMember member3 = ClubMember.builder()
                .user(user2)
                .club(club3)
                .role(ClubMember.MemberRole.MEMBER)
                .joinedAt(LocalDateTime.now().minusMonths(1))
                .build();

        clubMemberRepository.save(member1);
        clubMemberRepository.save(member2);
        clubMemberRepository.save(member3);

        // 교내 활동 생성
        Activity activity1 = Activity.builder()
                .title("2024 교내 프로그래밍 경진대회")
                .description("알고리즘 문제 해결 능력을 겨루는 대회")
                .content("컴퓨터공학과 주최 프로그래밍 경진대회입니다. 다양한 난이도의 문제가 출제됩니다.")
                .type(Activity.ActivityType.IN_SCHOOL)
                .category(Activity.ActivityCategory.COMPETITION)
                .organizer("컴퓨터공학과")
                .deadline(LocalDate.now().plusDays(30))
                .startDate(LocalDate.now().plusDays(30))
                .link("https://example.com/contest1")
                .imageUrl("https://example.com/activity1.jpg")
                .tags(List.of("개발", "프로그래밍", "대회"))
                .build();

        Activity activity2 = Activity.builder()
                .title("웹 개발 해커톤")
                .description("24시간 동안 웹 서비스를 만들어요")
                .content("팀을 구성하여 24시간 동안 웹 서비스를 개발하는 해커톤입니다.")
                .type(Activity.ActivityType.IN_SCHOOL)
                .category(Activity.ActivityCategory.CONTEST)
                .organizer("정보통신대학")
                .deadline(LocalDate.now().plusDays(15))
                .startDate(LocalDate.now().plusDays(20))
                .link("https://example.com/hackathon1")
                .imageUrl("https://example.com/activity2.jpg")
                .tags(List.of("개발", "웹", "해커톤"))
                .build();

        Activity activity3 = Activity.builder()
                .title("디자인 공모전")
                .description("학교 홈페이지 리디자인 공모전")
                .content("학교 홈페이지를 새롭게 디자인하는 공모전입니다.")
                .type(Activity.ActivityType.IN_SCHOOL)
                .category(Activity.ActivityCategory.CONTEST)
                .organizer("총학생회")
                .deadline(LocalDate.now().plusDays(45))
                .startDate(LocalDate.now().plusDays(50))
                .link("https://example.com/design_contest")
                .imageUrl("https://example.com/activity3.jpg")
                .tags(List.of("디자인", "공모전"))
                .build();

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);

        // 교외 활동 생성
        Activity activity4 = Activity.builder()
                .title("전국 대학생 프로그래밍 대회")
                .description("전국 대학생들이 참여하는 프로그래밍 대회")
                .content("ACM-ICPC 스타일의 프로그래밍 대회입니다.")
                .type(Activity.ActivityType.OUT_SCHOOL)
                .category(Activity.ActivityCategory.COMPETITION)
                .organizer("한국정보과학회")
                .deadline(LocalDate.now().plusDays(60))
                .startDate(LocalDate.now().plusDays(70))
                .link("https://example.com/national_contest")
                .imageUrl("https://example.com/activity4.jpg")
                .tags(List.of("개발", "프로그래밍", "대회"))
                .build();

        Activity activity5 = Activity.builder()
                .title("스타트업 해커톤")
                .description("스타트업 아이디어를 실현하는 해커톤")
                .content("창업 아이디어를 가진 팀들이 모여 프로토타입을 만드는 해커톤입니다.")
                .type(Activity.ActivityType.OUT_SCHOOL)
                .category(Activity.ActivityCategory.CONTEST)
                .organizer("스타트업 얼라이언스")
                .deadline(LocalDate.now().plusDays(20))
                .startDate(LocalDate.now().plusDays(25))
                .link("https://example.com/startup_hackathon")
                .imageUrl("https://example.com/activity5.jpg")
                .tags(List.of("개발", "스타트업", "해커톤"))
                .build();

        activityRepository.save(activity4);
        activityRepository.save(activity5);

        // 취업 정보 생성
        JobPost job1 = JobPost.builder()
                .companyName("테크 스타트업 A")
                .position("백엔드 개발자")
                .description("Spring Boot 기반 백엔드 개발자 채용")
                .content("신입 개발자를 채용합니다. Spring Boot, JPA 경험이 있으면 우대합니다.")
                .location("서울")
                .deadline(LocalDate.now().plusDays(30))
                .link("https://example.com/job1")
                .major("컴퓨터공학과")
                .tags(List.of("백엔드", "Spring", "Java"))
                .build();

        JobPost job2 = JobPost.builder()
                .companyName("디자인 에이전시 B")
                .position("UI/UX 디자이너")
                .description("모바일 앱 UI/UX 디자이너 채용")
                .content("모바일 앱 디자인 경험이 있는 디자이너를 채용합니다.")
                .location("서울")
                .deadline(LocalDate.now().plusDays(20))
                .link("https://example.com/job2")
                .major("디자인학과")
                .tags(List.of("디자인", "UI/UX", "모바일"))
                .build();

        JobPost job3 = JobPost.builder()
                .companyName("대기업 C")
                .position("프론트엔드 개발자")
                .description("React 기반 프론트엔드 개발자 채용")
                .content("React, TypeScript 경험이 있는 개발자를 채용합니다.")
                .location("경기")
                .deadline(LocalDate.now().plusDays(40))
                .link("https://example.com/job3")
                .major("컴퓨터공학과")
                .tags(List.of("프론트엔드", "React", "TypeScript"))
                .build();

        jobPostRepository.save(job1);
        jobPostRepository.save(job2);
        jobPostRepository.save(job3);

        // 동아리 공지 생성
        ClubNotice notice1 = ClubNotice.builder()
                .club(club1)
                .title("알고리즘 스터디 모집")
                .content("매주 화요일 오후 7시에 알고리즘 문제를 함께 풀어요!")
                .author("동아리장")
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();

        ClubNotice notice2 = ClubNotice.builder()
                .club(club2)
                .title("프로젝트 팀원 모집")
                .content("웹 서비스 프로젝트를 함께 할 팀원을 모집합니다.")
                .author("운영진")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        ClubNotice notice3 = ClubNotice.builder()
                .club(club2)
                .title("정기 모임 안내")
                .content("이번 주 금요일 오후 6시에 정기 모임이 있습니다.")
                .author("동아리장")
                .createdAt(LocalDateTime.now().minusHours(5))
                .build();

        clubNoticeRepository.save(notice1);
        clubNoticeRepository.save(notice2);
        clubNoticeRepository.save(notice3);

        // 인재 프로필 생성
        TalentProfile talent1 = TalentProfile.builder()
                .user(user1)
                .introduction("백엔드 개발에 관심이 많은 컴공과 학생입니다.")
                .skills(List.of("Java", "Spring Boot", "MySQL", "Redis"))
                .currentAffiliation("웹 개발 동아리")
                .portfolioLink("https://github.com/student1")
                .availableProjectTypes(List.of("웹 개발", "API 개발", "백엔드"))
                .build();

        TalentProfile talent2 = TalentProfile.builder()
                .user(user2)
                .introduction("UI/UX 디자인을 전공하고 있는 디자인학과 학생입니다.")
                .skills(List.of("Figma", "Adobe XD", "Photoshop", "Illustrator"))
                .currentAffiliation("디자인 스튜디오")
                .portfolioLink("https://behance.net/student2")
                .availableProjectTypes(List.of("UI/UX 디자인", "그래픽 디자인", "브랜딩"))
                .build();

        TalentProfile talent3 = TalentProfile.builder()
                .user(user3)
                .introduction("풀스택 개발을 할 수 있는 개발자입니다.")
                .skills(List.of("React", "Node.js", "Spring Boot", "MongoDB"))
                .currentAffiliation("알고리즘 동아리")
                .portfolioLink("https://github.com/student3")
                .availableProjectTypes(List.of("웹 개발", "풀스택", "프로젝트"))
                .build();

        talentProfileRepository.save(talent1);
        talentProfileRepository.save(talent2);
        talentProfileRepository.save(talent3);
    }
}

