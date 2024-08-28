package greencity.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "news_subscriptions")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class NewsSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String token;
}
