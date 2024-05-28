package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zxcv.asdf.domain.Chatting;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findBySenderToken(String sender);
    List<Chatting> findByReceiverToken(String receiver);
    List<Chatting> findBySenderTokenAndReceiverToken(String sender, String receiver);
}
