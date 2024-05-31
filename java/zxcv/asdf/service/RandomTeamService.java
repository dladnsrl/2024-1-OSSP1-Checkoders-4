package zxcv.asdf.service;

import lombok.RequiredArgsConstructor;
import zxcv.asdf.DTO.UserDTO;
import zxcv.asdf.DTO.page5_list;
import zxcv.asdf.domain.Team;
import zxcv.asdf.domain.Enrollment;
import zxcv.asdf.domain.User;
import zxcv.asdf.repository.EnrollmentRepository;
import zxcv.asdf.repository.TeamRepository;
import zxcv.asdf.repository.UserRepository;
import zxcv.asdf.repository.LectureRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RandomTeamService {

    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public List<Team> assignTeams(Long lectureId, int teamSize) {
        List<Enrollment> enrollments = enrollmentRepository.findByLectureId(lectureId);
        List<User> users = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            users.add(enrollment.getUser());
        }

        Collections.shuffle(users);

        List<Team> teams = new ArrayList<>();
        int teamCount = users.size() / teamSize;
        int remainder = users.size() % teamSize;

        // 임시 팀 목록 생성
        List<List<User>> tempTeams = new ArrayList<>();
        for (int i = 0; i < teamCount; i++) {
            tempTeams.add(new ArrayList<>());
        }

        // 유저들을 임시 팀에 할당
        int index = 0;
        for (int i = 0; i < teamCount; i++) {
            for (int j = 0; j < teamSize; j++) {
                tempTeams.get(i).add(users.get(index++));
            }
        }

        // 남은 인원을 각 팀에 한 명씩 배정
        for (int i = 0; i < remainder; i++) {
            tempTeams.get(i).add(users.get(index++));
        }

        // 각 임시 팀을 실제 팀으로 변환하여 저장
        for (int i = 0; i < tempTeams.size(); i++) {
            for (User user : tempTeams.get(i)) {
                Team team = Team.builder()
                        .user(user)
                        .team_id((long) i) // team_id -> teamId로 수정됨
                        .lecture(lectureRepository.findById(lectureId).orElseThrow(() -> new RuntimeException("Lecture not found")))
                        .build();
                teams.add(teamRepository.save(team));
            }
        }

        return teams;
    }

    public List<page5_list> getRandomTeamMembers(Long lectureId) {
        // Fetch teams by lectureId
        List<Team> teams = teamRepository.findByLectureId(lectureId);

        // Generate UserDTO list for each team
        List<page5_list> memberList = teams.stream().map(team -> {
            List<UserDTO> userDTOList = userRepository.findByToken(team.getUser().getToken())
                    .stream().map(user ->
                            UserDTO.builder()
                                    .token(user.getToken())
                                    .name(user.getName())
                                    .build()
                    ).collect(Collectors.toList());

            return page5_list.builder()
                    .team_id(team.getTeam_id())
                    .userDTOList(userDTOList)
                    .build();
        }).collect(Collectors.toList());

        return memberList;
    }

}
