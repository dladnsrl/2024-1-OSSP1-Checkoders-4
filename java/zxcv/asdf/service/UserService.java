package zxcv.asdf.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zxcv.asdf.domain.Enrollment;
import zxcv.asdf.domain.Lecture;
import zxcv.asdf.domain.LectureAssignment;
import zxcv.asdf.domain.User;
import zxcv.asdf.repository.EnrollmentRepository;
import zxcv.asdf.repository.LectureAssignmentRepository;
import zxcv.asdf.repository.LectureRepository;
import zxcv.asdf.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

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
}
