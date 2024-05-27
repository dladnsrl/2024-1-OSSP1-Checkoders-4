package zxcv.asdf.controller;

import lombok.RequiredArgsConstructor;

import zxcv.asdf.domain.Team;
import zxcv.asdf.service.RandomTeamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final RandomTeamService randomTeamService;

    @PostMapping("/assign")
    public List<Team> assignTeams(@RequestParam Long lectureId, @RequestParam int teamSize) {
        return randomTeamService.assignTeams(lectureId, teamSize);
    }
}