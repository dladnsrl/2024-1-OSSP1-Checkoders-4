package zxcv.asdf.controller;

import lombok.RequiredArgsConstructor;

import zxcv.asdf.DTO.page5_list;
import zxcv.asdf.DTO.page5_randomTeam_2;
import zxcv.asdf.domain.Team;
import zxcv.asdf.service.RandomTeamService;
import org.springframework.web.bind.annotation.*;
import zxcv.asdf.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final UserService userService;
    private final RandomTeamService randomTeamService;

    @PostMapping("/{lectureId}/assign")
    public List<Team> assignTeams(@PathVariable Long lectureId,
                                  @RequestParam int teamSize) {
        return randomTeamService.assignTeams(lectureId, teamSize);
    }

    @GetMapping("/{token}/{lectureId}/randomTeam")
    public page5_randomTeam_2 getRandomTeam(@PathVariable String token,
                                            @PathVariable Long lectureId) {
        // 필요한 데이터를 UserService에서 가져옵니다.
        List<page5_list> memberList = randomTeamService.getRandomTeamMembers(lectureId);

        // page5_randomTeam_2 DTO 객체를 생성합니다.
        page5_randomTeam_2 randomTeam = page5_randomTeam_2.builder()
                .lecture_id(lectureId)
                .token(token)
                .memberList(memberList)
                .build();

        return randomTeam;
    }
}