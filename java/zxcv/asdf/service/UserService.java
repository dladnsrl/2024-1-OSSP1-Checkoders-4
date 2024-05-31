package zxcv.asdf.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zxcv.asdf.DTO.AssignmentDTO;
import zxcv.asdf.DTO.LectureDTO;
import zxcv.asdf.DTO.UserDTO;
import zxcv.asdf.DTO.page2_lecture;
import zxcv.asdf.domain.*;
import zxcv.asdf.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TeamRepository teamRepository;
    private final LectureAssignmentMappingRepository lectureAssignmentMappingRepository;
    private final LectureAssignmentRepository lectureAssignmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    private AssignmentDTO convertToDTO(LectureAssignment assignment) {
        return AssignmentDTO.builder()
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .deadline(assignment.getDeadline())
                .isDone(assignment.isDone())
                .build();
    }
    @Transactional
    public User addUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getToken());
        return existingUser.orElseGet(() -> userRepository.save(user));
    }

    public User getUser(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Lecture addLecture(Lecture lecture) {
        return lectureRepository.save(lecture);
    }

    public Lecture getLectureById(Long id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));
    }

    public Lecture getLectureByName(String name) {
        return lectureRepository.findByName(name);
    }

    public List<Lecture> getAllLectures() {
        return lectureRepository.findAll();
    }

    public page2_lecture getPage2Lecture(String token, Long lectureId) {
        Long teamId = teamRepository.findTeamIdByLectureIdAndToken(lectureId, token);

        List<String> teamMemberTokens = teamRepository.findUserTokensByLectureIdAndTeamId(lectureId, teamId);
        List<User> teamMembers = userRepository.findAllByTokenIn(teamMemberTokens);

        // Convert teamMembers to UserDTO
        List<UserDTO> teamMemberDTOs = teamMembers.stream()
                .map(user -> UserDTO.builder()
                        .token(user.getToken())
                        .name(user.getName())
                        .build())
                .collect(Collectors.toList());

        // Get lecture assignments
        List<LectureAssignmentMapping> allMappings = lectureAssignmentMappingRepository.findByLectureId(lectureId);

        List<Long> userAssignmentIds = allMappings.stream()
                .filter(mapping -> mapping.getUser().getToken().equals(token))
                .map(LectureAssignmentMapping::getLectureAssignmentId)
                .collect(Collectors.toList());

        List<Long> otherAssignmentIds = allMappings.stream()
                .filter(mapping -> !mapping.getUser().getToken().equals(token))
                .map(LectureAssignmentMapping::getLectureAssignmentId)
                .collect(Collectors.toList());

        List<LectureAssignment> userAssignments = lectureAssignmentRepository.findAllById(userAssignmentIds);
        List<LectureAssignment> otherAssignments = lectureAssignmentRepository.findAllById(otherAssignmentIds);

        List<AssignmentDTO> userAssignmentDTOs = userAssignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        List<AssignmentDTO> otherAssignmentDTOs = otherAssignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return page2_lecture.builder()
                .task(userAssignmentDTOs)
                .exercise(otherAssignmentDTOs)
                .teamMembers(teamMemberDTOs)  // Changed to use teamMemberDTOs
                .build();
    }


    public Enrollment addEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Lecture> getLecturesByUserToken(String token) {
        return lectureRepository.findLecturesByUserToken(token);
    }
    public LectureAssignment addLectureAssignment(String token,LectureAssignment lectureAssignment) {
        lectureAssignmentRepository.save(lectureAssignment);
        LectureAssignmentMapping lectureAssignmentMapping= LectureAssignmentMapping.builder()
                .user(userRepository.findByToken(token).orElseThrow(() -> new RuntimeException("User not found")))
                .lecture(lectureAssignment.getLecture())
                .lectureAssignment(lectureAssignment)
                .build();
        lectureAssignmentMappingRepository.save(lectureAssignmentMapping);
        return lectureAssignment;
    }
    public LectureAssignment getLectureAssignmentById(Long lectureAssignmentId) {
        return lectureAssignmentRepository.findById(lectureAssignmentId)
                .orElseThrow(() -> new RuntimeException("Lecture Assignment not found"));
    }

}


