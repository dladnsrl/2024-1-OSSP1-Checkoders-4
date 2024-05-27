package zxcv.asdf.service;

import lombok.RequiredArgsConstructor;
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
        int teamCount = (int) Math.ceil((double) users.size() / teamSize);
        for (int i = 0; i < teamCount; i++) {
            int start = i * teamSize;
            int end = Math.min(start + teamSize, users.size());
            List<User> teamMembers = users.subList(start, end);

            for (User user : teamMembers) {
                Team team = Team.builder()
                        .user(user)
                        .team_id((long)i)
                        .lecture(lectureRepository.findById(lectureId).orElseThrow(() -> new RuntimeException("Lecture not found")))
                        .build();
                teams.add(teamRepository.save(team));
            }
        }

        return teams;
    }
}