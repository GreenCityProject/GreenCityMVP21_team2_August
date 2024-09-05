package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.newssubscription.NewsSubscriptionDto;
import greencity.entity.NewsSubscription;
import greencity.exception.exceptions.AlreadySubscribedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NewsSubscriptionRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class NewsSubscriptionServiceImpl implements NewsSubscriptionService {
    private NewsSubscriptionRepository repository;
    private ModelMapper modelMapper;

    @Override
    public List<NewsSubscriptionDto> findAll() {
        return repository.findAllDto();
    }

    @Override
    public NewsSubscriptionDto subscribe(final NewsSubscriptionDto newsSubscriptionDto) {
        if (repository.existsByEmail(newsSubscriptionDto.getEmail())) {
            throw new AlreadySubscribedException(ErrorMessage.EMAIL_ALREADY_SUBSCRIBED);
        }
        NewsSubscription newsSubscription = modelMapper.map(newsSubscriptionDto, NewsSubscription.class);
        newsSubscription.setToken(UUID.randomUUID().toString());
        return modelMapper.map(repository.save(newsSubscription), NewsSubscriptionDto.class);
    }

    @Override
    public NewsSubscriptionDto unsubscribe(final String token) {
        final NewsSubscription newsSubscription = repository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NEWS_SUBSCRIPTION_NOT_FOUND_BY_TOKEN));
        repository.delete(newsSubscription);
        return modelMapper.map(newsSubscription, NewsSubscriptionDto.class);
    }

    @Override
    public boolean isSubscribed(final String email) {
        return repository.existsByEmail(email.toLowerCase());
    }

    @Override
    public NewsSubscriptionDto findByToken(final String token) {
        final NewsSubscription newsSubscription = repository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NEWS_SUBSCRIPTION_NOT_FOUND_BY_TOKEN));
        return modelMapper.map(newsSubscription, NewsSubscriptionDto.class);
    }
}
