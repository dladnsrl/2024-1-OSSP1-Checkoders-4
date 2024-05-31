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

import zxcv.asdf.DTO.LectureDTO;
import zxcv.asdf.DTO.page1_main;
import zxcv.asdf.DTO.page2_lecture;
import zxcv.asdf.DTO.page4_makeProb;
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
import java.util.stream.Collectors;

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
            response.sendRedirect("http://localhost:3000/main?usertoken=" + token+"&access_token="+accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/{token}/{name}/dummy_user")
    public void dummy_user(@PathVariable String token,@PathVariable String name){
        User user= User.builder()
                .token(token)
                .name(name)
                .build();
        userService.addUser(user);

    }
    @GetMapping("/{token}/mainpage")
    public page1_main mainpage(@PathVariable String token) {
        List<Lecture> lectures = userService.getLecturesByUserToken(token);

        List<LectureDTO> lectureDTOs = lectures.stream()
                .map(lecture -> LectureDTO.builder()
                        .name(lecture.getName())
                        .madeby(lecture.getMadeby())
                        .build())
                .collect(Collectors.toList());

        return page1_main.builder()
                .token(token)
                .name(userService.getUser(token).getName())
                .lectures(lectureDTOs)
                .build();
    }
    @PostMapping("/{token}/createlecture")
    public List<Lecture> createLecture(@PathVariable String token, @RequestParam String lectureName,@RequestParam String course) {
        User user = userService.getUser(token);
        Lecture lecture = Lecture.builder()
                .name(lectureName)
                .course(course)
                .madeby(token)
                .build();
        userService.addLecture(lecture);
        Enrollment enrollment = Enrollment.builder()
                .lecture(lecture)
                .user(user)
                .build();
        userService.addEnrollment(enrollment);

        return userService.getLecturesByUserToken(token);
    }

    @PostMapping("/{token}/getlectures")
    public List<Lecture> getLectures(@PathVariable String token) {
        return userService.getLecturesByUserToken(token);
    }

    @PostMapping("/{token}/participate")
    public page1_main participate(@PathVariable String token, @RequestParam String lectureName) {
        User user = userService.getUser(token);
        Lecture lecture = userService.getLectureByName(lectureName);
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .lecture(lecture)
                .build();
        userService.addEnrollment(enrollment);

        // 참여 후 메인 페이지로 돌아가기 위해서 다시 강의 목록 가져오기
        List<Lecture> lectures = userService.getLecturesByUserToken(token);
        List<LectureDTO> lectureDTOs = lectures.stream()
                .map(lec -> LectureDTO.builder()
                        .name(lec.getName())
                        .madeby(lec.getMadeby())
                        .build())
                .collect(Collectors.toList());

        return page1_main.builder()
                .token(token)
                .name(userService.getUser(token).getName())
                .lectures(lectureDTOs)
                .build();
    }

    @GetMapping("/{token}/{lectureId}/lecturepage")
    public page2_lecture lecturepage(@PathVariable String token, @PathVariable Long lectureId) {
        return userService.getPage2Lecture(token, lectureId);
    }

    @PostMapping("/{token}/{lectureId}/createAssignment")
    public page2_lecture createAssignment(@PathVariable String token,
                                              @PathVariable Long lectureId,
                                              @RequestBody page4_makeProb page4_makeProb) {

        Lecture lecture=userService.getLectureById(lectureId);

        LectureAssignment lectureAssignment=LectureAssignment.builder()
                .title(page4_makeProb.getTitle())
                .description(page4_makeProb.getDescription())
                .lecture(lecture)
                .deadline(page4_makeProb.getDeadline())
                .hwTest1(page4_makeProb.getHwTest1())
                .hwTestAnswer1(page4_makeProb.getHwTestAnswer1())
                .hwTest2(page4_makeProb.getHwTest2())
                .hwTestAnswer2(page4_makeProb.getHwTestAnswer2())
                .hwTest3(page4_makeProb.getHwTest3())
                .hwTestAnswer3(page4_makeProb.getHwTestAnswer3())
                .hwTest4(page4_makeProb.getHwTest4())
                .hwTestAnswer4(page4_makeProb.getHwTestAnswer4())
                .hwTest5(page4_makeProb.getHwTest5())
                .hwTestAnswer5(page4_makeProb.getHwTestAnswer5())
                .build();

        userService.addLectureAssignment(token,lectureAssignment);
        return userService.getPage2Lecture(token, lectureId);
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

