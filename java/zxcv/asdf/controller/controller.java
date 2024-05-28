package zxcv.asdf.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import zxcv.asdf.DTO.AssignmentListDTO;
import zxcv.asdf.DTO.AssignmentRequestDTO;
import zxcv.asdf.domain.Enrollment;
import zxcv.asdf.domain.Lecture;
import zxcv.asdf.domain.LectureAssignment;
import zxcv.asdf.domain.User;
import zxcv.asdf.service.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class controller {


    @Value("${api.key}")
    private String CLIENT_ID; // REST API 키
    @Value("${redirect.uri}")
    private String REDIRECT_URI;// 리디렉션 URI
    @Value("${token.request.uri}")
    private String TOKEN_REQUEST_URI;

    private final CheckingService checkingService;
    private final UserService userService;
    private final CodeFeedbackService codeFeedbackService;
    private final RandomTeamService randomTeamService;

    @GetMapping("/login")
    public void login(@RequestParam String code, HttpServletResponse response) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(TOKEN_REQUEST_URI, request, String.class);
        String token;
        String name;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(tokenResponse.getBody());
            String accessToken = rootNode.path("access_token").asText();

            // 사용자 정보 요청
            headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);
            ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    userInfoRequest,
                    String.class
            );
            JsonNode userInfoNode = objectMapper.readTree(userInfoResponse.getBody());
            token = userInfoNode.path("id").asText();
            name = userInfoNode.path("properties").path("nickname").asText();
            System.out.println("User ID token: " + token);
            System.out.println("Nickname: " + name);
            User user = User.builder()
                    .token(token)
                    .name(name)
                    .build();
            userService.addUser(user);
            response.sendRedirect("http://localhost:3000/main?userId=" + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/createuser")
    public User createUser(@RequestParam String token,@RequestParam String name) {
        // 임의로 사용자 정보를 설정하여 User 객체를 생성합니다.
        User user = User.builder()
                .token(token)
                .name(name)
                .build();
        return userService.addUser(user);
    }

    @PostMapping("/{token}/createlecture")
    public List<Lecture> createLecture(@PathVariable String token, @RequestParam String lectureName,@RequestParam String course) {
        User user = userService.getUser(token);
        Lecture lecture = Lecture.builder()
                .name(lectureName)
                .course(course)
                .build();
        userService.addLecture(lecture);
        Enrollment enrollment = Enrollment.builder()
                .lecture(lecture)
                .user(user)
                .build();
        userService.addEnrollment(enrollment);

        return userService.getLecturesByUserToken(user.getToken());
    }

    @PostMapping("/{token}/participate")
    public List<Lecture> participate(@PathVariable String token, @RequestParam String lectureName) {
        User user = userService.getUser(token);
        Lecture lecture = userService.getLectureByName(lectureName);
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .lecture(lecture)
                .build();
        userService.addEnrollment(enrollment);
        return userService.getLecturesByUserToken(user.getToken());
    }
    @PostMapping("/{token}/{lectureId}/createAssignment")
    public LectureAssignment createAssignment(@PathVariable String token,
                                              @PathVariable Long lectureId,
                                              @RequestBody AssignmentRequestDTO assignmentRequestDTO) {

        Lecture lecture=userService.getLectureById(lectureId);

        LectureAssignment lectureAssignment = LectureAssignment.builder()
                .lecture(lecture)
                .title(assignmentRequestDTO.getTitle())
                .description(assignmentRequestDTO.getDescription())
                .deadline(assignmentRequestDTO.getDeadline())
                .hwTest1(assignmentRequestDTO.getHwTest1())
                .hwTestAnswer1(assignmentRequestDTO.getHwTestAnswer1())
                .hwTest2(assignmentRequestDTO.getHwTest2())
                .hwTestAnswer2(assignmentRequestDTO.getHwTestAnswer2())
                .hwTest3(assignmentRequestDTO.getHwTest3())
                .hwTestAnswer3(assignmentRequestDTO.getHwTestAnswer3())
                .hwTest4(assignmentRequestDTO.getHwTest4())
                .hwTestAnswer4(assignmentRequestDTO.getHwTestAnswer4())
                .hwTest5(assignmentRequestDTO.getHwTest5())
                .hwTestAnswer5(assignmentRequestDTO.getHwTestAnswer5())
                .build();

        return userService.addLectureAssignment(token,lectureAssignment);
    }
    @GetMapping("/{token}/{lectureId}/getlectureassignment")
    public AssignmentListDTO getLectureAssignment(@PathVariable String token, @PathVariable Long lectureId) {
        return userService.getLectureAssignments(token, lectureId);
    }

    @PostMapping("/submit")
    public String submit(@RequestBody String sourceCode,@RequestParam String args,@RequestParam String xOutput) throws Exception {
        Path tempDir = Files.createTempDirectory("compile_output");
        String code=extractJavaCode(URLDecoder.decode(sourceCode, StandardCharsets.UTF_8.toString()));
        String[] arguments = extractArgs(URLDecoder.decode(args, StandardCharsets.UTF_8.toString())).split("\\s+");
        String expectedOutput = extractXOutput(URLDecoder.decode(xOutput, StandardCharsets.UTF_8.toString()));
        try {
            boolean result = checkingService.compile(code, tempDir);
            if (result) {
                System.out.println("컴파일 성공");
                String output = checkingService.test(tempDir,arguments).trim();
                if (expectedOutput.equals(output)) {
                    System.out.println("출력:"+output+"  정답:"+expectedOutput);
                }
            } else {
                System.out.println("Compilation failed");
            }
        } finally {
            Files.walk(tempDir).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return codeFeedbackService.getCodeFeedback(code).replace("\n", "<br>");
    }

    private String extractJavaCode(String fullCode) {
        int argsIndex = fullCode.indexOf("&args=");
        if (argsIndex != -1) {
            return fullCode.substring("sourceCode=".length(), argsIndex);
        }
        return fullCode.substring("sourceCode=".length());
    }
    public String extractArgs(String args) {
        if (args.startsWith("args=")) {
            return args.substring("args=".length());
        }
        return args; // 이미 형식이 맞는 경우 그대로 반환
    }
    public String extractXOutput(String xOutput) {
        if (xOutput.startsWith("xOutput=")) {
            return xOutput.substring("xOutput=".length());
        }
        return xOutput; // 이미 형식이 맞는 경우 그대로 반환
    }
}

