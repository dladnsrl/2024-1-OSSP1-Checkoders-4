package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zxcv.asdf.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import zxcv.asdf.domain.User;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByLectureId(Long lectureId);

    @Query("SELECT t.team_id FROM Team t WHERE t.lecture.id = :lectureId AND t.user.token = :token")
    Long findTeamIdByLectureIdAndToken(@Param("lectureId") Long lectureId, @Param("token") String token);

    @Query("SELECT t.user.token FROM Team t WHERE t.lecture.id = :lectureId AND t.team_id = :teamId")
    List<String> findUserTokensByLectureIdAndTeamId(@Param("lectureId") Long lectureId, @Param("teamId") Long teamId);
}
