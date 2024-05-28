package zxcv.asdf.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zxcv.asdf.DTO.AssignmentDTO;
import zxcv.asdf.DTO.AssignmentListDTO;
import zxcv.asdf.domain.*;
import zxcv.asdf.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final LectureAssignmentMappingRepository lectureAssignmentMappingRepository;
    private final LectureAssignmentRepository lectureAssignmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public User addUser(User user) {
        Optional<User> existingUser = userRepository.findByToken(user.getToken());
        return existingUser.orElseGet(() -> userRepository.save(user));
    }

    public User getUser(String token) {
        return userRepository.findById(token).orElse(null);
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

    public Enrollment addEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Lecture> getLecturesByUserToken(String token) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserToken(token);
        return enrollments.stream()
                .map(Enrollment::getLecture)
                .collect(Collectors.toList());
    }
    public LectureAssignment addLectureAssignment(String token,LectureAssignment lectureAssignment) {
        lectureAssignmentRepository.save(lectureAssignment);
        LectureAssignmentMapping lectureAssignmentMapping= LectureAssignmentMapping.builder()
                .user(userRepository.findById(token).orElseThrow(() -> new RuntimeException("User not found")))
                .lecture(lectureAssignment.getLecture())
                .lectureAssignment(lectureAssignment)
                .build();
        lectureAssignmentMappingRepository.save(lectureAssignmentMapping);
        return lectureAssignment;
    }
    public AssignmentListDTO getLectureAssignments(String token, Long lectureId) {
        List<LectureAssignmentMapping> allMappings = lectureAssignmentMappingRepository.findByLectureId(lectureId);

        List<Long> userAssignmentIds = allMappings.stream()
                .filter(mapping -> mapping.getUser().getToken().equals(token))
                .map(LectureAssignmentMapping::getId)
                .collect(Collectors.toList());

        List<Long> otherAssignmentIds = allMappings.stream()
                .filter(mapping -> !mapping.getUser().getToken().equals(token))
                .map(LectureAssignmentMapping::getId)
                .collect(Collectors.toList());

        List<LectureAssignment> userAssignments = lectureAssignmentRepository.findAllById(userAssignmentIds);
        List<LectureAssignment> otherAssignments = lectureAssignmentRepository.findAllById(otherAssignmentIds);

        List<AssignmentDTO> userAssignmentDTOs = userAssignments.stream()
                .map(this::convertToAssignmentDTO)
                .collect(Collectors.toList());

        List<AssignmentDTO> otherAssignmentDTOs = otherAssignments.stream()
                .map(this::convertToAssignmentDTO)
                .collect(Collectors.toList());

        return AssignmentListDTO.builder()
                .task(userAssignmentDTOs)
                .exercise(otherAssignmentDTOs)
                .build();
    }
    private AssignmentDTO convertToAssignmentDTO(LectureAssignment assignment) {
        return AssignmentDTO.builder()
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .deadline(assignment.getDeadline())
                .isDone(assignment.isDone()) // assuming `isDone` is a method in `LectureAssignment`
                .build();
    }
}
