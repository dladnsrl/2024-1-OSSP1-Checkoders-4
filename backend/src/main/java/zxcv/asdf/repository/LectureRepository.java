package zxcv.asdf.repository;

import org.springframework.stereotype.Repository;
import zxcv.asdf.domain.Lecture;

import java.util.HashMap;
import java.util.List;

@Repository
public class LectureRepository {
    private static final HashMap<Long, Lecture> db = new HashMap<>();
    private static long s=0L;

    public void save(Lecture lecture) {
        System.out.println(s);
        lecture.setLecture_id(s++);
        db.put(lecture.getLecture_id(), lecture);
        System.out.println(db.values());
    }
    public Lecture getById(Long id){
        return db.get(id);
    }
    public List<Lecture> getAll(){
        return (List<Lecture>) db.values();
    }

}
