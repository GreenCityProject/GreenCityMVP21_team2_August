package greencity.service;


import greencity.dto.newssubscription.NewsSubscriptionDto;

import javax.management.Notification;
import java.util.List;
import java.util.Optional;

public interface NewsSubscriptionService {
    NewsSubscriptionDto subscribe(NewsSubscriptionDto newsSubscriptionDto);

    NewsSubscriptionDto unsubscribe(String token);

    List<NewsSubscriptionDto> findAll();

    boolean isSubscribed(String email);

    NewsSubscriptionDto findByToken(String token);
}
