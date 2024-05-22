package zxcv.asdf.domain;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//@Entity
@Data
public class User {

    //Long id;
   // @Column
    String id_token;

    //@Column
    String nickname;

    //@Column
    List<Lecture> lectures;

    @Builder
    public User(String id_token, String nickname) {
        this.id_token = id_token;
        this.nickname = nickname;
        this.lectures = new ArrayList<>();
    }
}
