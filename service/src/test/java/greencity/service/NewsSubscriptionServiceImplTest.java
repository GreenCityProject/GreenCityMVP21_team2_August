package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.newssubscription.NewsSubscriptionDto;
import greencity.entity.NewsSubscription;
import greencity.exception.exceptions.AlreadySubscribedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.NewsSubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsSubscriptionServiceImplTest {
    @InjectMocks
    private NewsSubscriptionServiceImpl service;
    @Mock
    private NewsSubscriptionRepository repository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAll_EmptyList_returnsListOfNewsSubscriptionDto() {
        when(repository.findAllDto()).thenReturn(List.of());
        List<NewsSubscriptionDto> actual = service.findAll();
        assertEquals(0, actual.size());
    }

    @Test
    void findAll_NotEmptyList_returnsListOfNewsSubscriptionDto() {
        List<NewsSubscriptionDto> expected = List.of(ModelUtils.getNewsSubscriptionDto());
        when(repository.findAllDto()).thenReturn(expected);
        List<NewsSubscriptionDto> actual = service.findAll();
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void subscribe_IsNotInDatabase_returnsNewsSubscriptionDto() {
        NewsSubscriptionDto subscriptionDto = getNewsSubscriptionDto();
        NewsSubscription newsSubscription = new NewsSubscription();
        newsSubscription.setEmail(subscriptionDto.getEmail());

        when(repository.existsByEmail(subscriptionDto.getEmail())).thenReturn(false);
        when(modelMapper.map(subscriptionDto, NewsSubscription.class)).thenReturn(newsSubscription);
        when(repository.save(newsSubscription)).thenReturn(newsSubscription);
        when(modelMapper.map(any(NewsSubscription.class), eq(NewsSubscriptionDto.class))).thenReturn(subscriptionDto);

        NewsSubscriptionDto actual = service.subscribe(subscriptionDto);

        assertEquals(subscriptionDto.getEmail(), actual.getEmail());
        assertNotEquals(newsSubscription.getToken(), actual.getToken());
        assertNotNull(actual);
    }

    @Test
    void subscribe_alreadySubscribed_throwsAlreadySubscribedException() {
        when(repository.existsByEmail(anyString())).thenReturn(true);
        AlreadySubscribedException exception = assertThrows(AlreadySubscribedException.class,
                () -> service.subscribe(ModelUtils.getNewsSubscriptionDto()));
        assertEquals(ErrorMessage.EMAIL_ALREADY_SUBSCRIBED, exception.getMessage());
    }

    @Test
    void isSubscribed_IsInDatabase_returnsTrue() {
        when(repository.existsByEmail(anyString())).thenReturn(true);
        boolean result = service.isSubscribed("email@gmail.com");
        assertTrue(result);
    }

    @Test
    void isSubscribed_IsNotInDatabase_returnsFalse() {
        when(repository.existsByEmail(anyString())).thenReturn(false);
        boolean result = service.isSubscribed("email@gmail.com");
        assertFalse(result);
    }

    @Test
    void findByToken_IsInDatabase_returnsNewsSubscriptionDto() {
        String parameter = getNewsSubscriptionToken();
        NewsSubscription found = ModelUtils.getNewsSubscription();
        NewsSubscriptionDto expected = ModelUtils.getNewsSubscriptionDto();

        when(repository.findByToken(parameter)).thenReturn(Optional.of(found));
        when(modelMapper.map(found, NewsSubscriptionDto.class)).thenReturn(expected);

        NewsSubscriptionDto actual = service.findByToken(parameter);
        assertEquals(expected, actual);
    }

    @Test
    void findByToken_IsNotInDatabase_throwsNotFoundException() {
        String token = UUID.randomUUID().toString();
        when(repository.findByToken(token)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findByToken(token));
        assertEquals(ErrorMessage.NEWS_SUBSCRIPTION_NOT_FOUND_BY_TOKEN, exception.getMessage());
    }
}
