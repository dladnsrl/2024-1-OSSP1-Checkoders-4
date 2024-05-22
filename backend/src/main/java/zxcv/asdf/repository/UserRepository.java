package zxcv.asdf.repository;

import org.springframework.stereotype.Repository;
import zxcv.asdf.domain.User;

import java.util.HashMap;

@Repository
public class UserRepository {

    private static final HashMap<Long, User> db = new HashMap<>();
    private static long s=0L;

//    public void save(User user) {
//        user.setId(s++);
//        db.put(user.getId(),user);
//    }

    public User getById(Long id){
        return db.get(id);
    }


}
