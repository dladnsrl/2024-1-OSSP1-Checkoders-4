package zxcv.asdf.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chatting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_token", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_token", nullable = false)
    private User receiver;

    private String content;

    private LocalDateTime timestamp;
}
