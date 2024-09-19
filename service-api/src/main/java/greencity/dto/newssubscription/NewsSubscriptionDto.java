package greencity.dto.newssubscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import greencity.constant.AppConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class NewsSubscriptionDto {
    @NotNull
    @Email(regexp = AppConstant.VALID_EMAIL)
    @JsonDeserialize(using = LowerCaseDeserializer.class)
    private String email;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;
}
