package greencity.service;

import greencity.dto.notification.NotificationCreateDto;
import greencity.dto.notification.NotificationReadDto;
import greencity.entity.Notification;
import greencity.entity.Notification_;
import greencity.enums.NotificationType;
import greencity.enums.ProjectName;
import greencity.exception.exceptions.NotFoundException;
import greencity.filters.SearchCriteria;
import greencity.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;

import static greencity.ModelUtils.getNotificationReadDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl notificationService;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ModelMapper modelMapper;
    private Notification emptyNotification;
    private Notification notification;
    private final String initialTitle = "New Event Created";
    private final String initialMessage = "A new event has been created: EVENT";
    private final String englishLanguage = "en";
    private final String ukrainianLanguage = "ua";
    private final String nonExistentLanguage = "xx";

    @BeforeEach
    void setUp() {
        emptyNotification = new Notification();
        notification = Notification.builder()
                .title(initialTitle)
                .message(initialMessage)
                .projectName(ProjectName.GREEN_CITY)
                .type(NotificationType.EVENT_CREATED)
                .build();
    }

    @Test
    void createNotification_Saving_CorrectConversionIntoEnglish() {
        final NotificationCreateDto notificationCreateDto = NotificationCreateDto.builder()
                .titleParams(new String[]{})
                .messageParams(new String[]{"EVENT"})
                .type(NotificationType.EVENT_CREATED)
                .projectName(ProjectName.GREEN_CITY)
                .userId(1L)
                .build();
        final NotificationReadDto notificationReadDto = getNotificationReadDto();

        when(modelMapper.map(notificationCreateDto, Notification.class))
                .thenReturn(notification);
        when(notificationRepository.save(any()))
                .thenReturn(notification);
        when(modelMapper.map(any(Notification.class), eq(NotificationReadDto.class)))
                .thenReturn(notificationReadDto);

        final NotificationReadDto actual = notificationService.createNotification(notificationCreateDto, englishLanguage);
        assertNotNull(notification.getTitle());
        assertNotNull(notification.getMessage());
        assertEquals(actual.getTitle(), initialTitle);
        assertEquals(actual.getMessage(), initialMessage);
    }

    @Test
    void createNotification_Saving_CorrectConversionIntoUkrainian() {
        final NotificationCreateDto notificationCreateDto = NotificationCreateDto.builder()
                .titleParams(new String[]{})
                .messageParams(new String[]{"EVENT"})
                .type(NotificationType.EVENT_CREATED)
                .projectName(ProjectName.GREEN_CITY)
                .userId(1L)
                .build();
        final NotificationReadDto notificationReadDto = getNotificationReadDto();

        when(modelMapper.map(notificationCreateDto, Notification.class))
                .thenReturn(notification);
        when(notificationRepository.save(any()))
                .thenReturn(notification);
        when(modelMapper.map(any(Notification.class), eq(NotificationReadDto.class)))
                .thenReturn(notificationReadDto);

        final NotificationReadDto actual = notificationService.createNotification(notificationCreateDto, ukrainianLanguage);
        assertNotNull(notification.getTitle());
        assertNotNull(notification.getMessage());
        assertNotEquals(actual.getTitle(), initialTitle);
        assertNotEquals(actual.getMessage(), initialMessage);
    }

    @Test
    void getByFilter_Found_ConversionIntoUkrainian() {
        final Pageable pageable = PageRequest.of(0, 10);
        final SearchCriteria searchCriterion = SearchCriteria.builder()
                .key(Notification_.TYPE)
                .value("GREEN_CITY")
                .build();
        final List<SearchCriteria> criteria = new ArrayList<>() {{
            add(searchCriterion);
        }};
        final List<Notification> list = List.of(notification);
        final Page<Notification> notificationPage = new PageImpl<>(list, pageable, 1);

        when(notificationRepository.findAll(ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<Notification>>any(), eq(pageable)))
                .thenReturn(notificationPage);
        when(modelMapper.map(notification, NotificationReadDto.class))
                .thenReturn(getNotificationReadDto());

        final Page<NotificationReadDto> actual = notificationService.getByFilter(1L, pageable, criteria, ukrainianLanguage);

        final NotificationReadDto actualElement = actual.stream().findFirst().get();
        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        assertNotEquals(initialTitle, actualElement.getTitle());
        assertNotEquals(initialMessage, actualElement.getMessage());
    }

    @Test
    void getByFilter_Found_ConversionIntoEnglish() {
        final Pageable pageable = PageRequest.of(0, 10);
        final SearchCriteria searchCriterion = SearchCriteria.builder()
                .key(Notification_.TYPE)
                .value("GREEN_CITY")
                .build();
        final List<SearchCriteria> criteria = new ArrayList<>() {{
            add(searchCriterion);
        }};
        final List<Notification> list = List.of(notification);
        final Page<Notification> notificationPage = new PageImpl<>(list, pageable, 1);

        when(notificationRepository.findAll(ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<Notification>>any(), eq(pageable)))
                .thenReturn(notificationPage);
        when(modelMapper.map(notification, NotificationReadDto.class))
                .thenReturn(getNotificationReadDto());

        final Page<NotificationReadDto> actual = notificationService.getByFilter(1L, pageable, criteria, englishLanguage);

        final NotificationReadDto actualElement = actual.stream().findFirst().get();
        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        assertEquals(initialTitle, actualElement.getTitle());
        assertEquals(initialMessage, actualElement.getMessage());
    }

    @Test
    void getById_ExistentNotificationInUkrainian_Success() {
        final NotificationReadDto dtoForConversion = getNotificationReadDto();
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(emptyNotification));
        when(modelMapper.map(emptyNotification, NotificationReadDto.class))
                .thenReturn(dtoForConversion);

        final NotificationReadDto notificationReadDto = notificationService.getById(1L, ukrainianLanguage);

        assertNotNull(notificationReadDto);
        assertNotEquals(notificationReadDto.getTitle(), initialTitle);
        assertNotEquals(notificationReadDto.getMessage(), initialMessage);
        verify(notificationRepository).findById(anyLong());
        verify(modelMapper).map(any(Notification.class), eq(NotificationReadDto.class));
    }


    @Test
    void getById_ExistentNotificationInEnglish_Success() {
        final NotificationReadDto dtoForConversion = getNotificationReadDto();
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(emptyNotification));
        when(modelMapper.map(emptyNotification, NotificationReadDto.class))
                .thenReturn(dtoForConversion);

        final NotificationReadDto notificationReadDto = notificationService.getById(1L, englishLanguage);

        assertNotNull(notificationReadDto);
        assertEquals(notificationReadDto.getTitle(), initialTitle);
        assertEquals(notificationReadDto.getMessage(), initialMessage);
        verify(notificationRepository).findById(anyLong());
        verify(modelMapper).map(any(Notification.class), eq(NotificationReadDto.class));
    }

    @Test
    void getById_NoSuchNotification_throwsNotFoundException() {
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> notificationService.getById(1L, ukrainianLanguage),
                "Notification not found");
        verify(notificationRepository).findById(anyLong());
    }

    @Test
    void getById_NoSuchLanguage_throwsNotFoundException() {
        final NotificationReadDto dtoForConversion = getNotificationReadDto();
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(emptyNotification));
        when(modelMapper.map(emptyNotification, NotificationReadDto.class))
                .thenReturn(dtoForConversion);

        assertThrows(MissingResourceException.class, () -> notificationService.getById(1L, nonExistentLanguage));
    }

    @Test
    void getFiveUnreadNotifications_ExistentUnreadNotificationsInUkrainian_Success() {
        final NotificationReadDto dtoForConversion = getNotificationReadDto();
        List<Notification> list = List.of(notification);
        when(notificationRepository.findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(anyLong()))
                .thenReturn(list);
        when(modelMapper.map(notification, NotificationReadDto.class))
                .thenReturn(dtoForConversion);

        final List<NotificationReadDto> actual = notificationService.getFiveUnreadNotifications(20L, ukrainianLanguage);

        assertEquals(1, actual.size());
        assertNotEquals(actual.getFirst().getTitle(),
                initialTitle);
        assertNotEquals(actual.getFirst().getMessage(),
                initialMessage);
        verify(notificationRepository).findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(anyLong());
        verify(modelMapper).map(any(Notification.class), eq(NotificationReadDto.class));
    }

    @Test
    void getFiveUnreadNotifications_ExistentUnreadNotificationsInEnglish_Success() {
        final NotificationReadDto dtoForConversion = getNotificationReadDto();
        final List<Notification> list = List.of(notification);
        when(notificationRepository.findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(anyLong()))
                .thenReturn(list);
        when(modelMapper.map(notification, NotificationReadDto.class))
                .thenReturn(dtoForConversion);

        final List<NotificationReadDto> actual = notificationService.getFiveUnreadNotifications(20L, englishLanguage);

        assertEquals(1, actual.size());
        assertEquals(actual.getFirst().getTitle(),
                initialTitle);
        assertEquals(actual.getFirst().getMessage(),
                initialMessage);
        verify(notificationRepository).findTopFiveByUserIdAndViewedFalseOrderByCreatedDateDesc(anyLong());
        verify(modelMapper).map(any(Notification.class), eq(NotificationReadDto.class));
    }

    @Test
    void getAllNotifications_ExistentNotificationsInUkrainian_Success() {
        final List<Notification> list = List.of(notification);
        final NotificationReadDto dtoForConversion = getNotificationReadDto();
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<Notification> page = new PageImpl<>(list, pageable, 1);

        when(notificationRepository.findAllByUserIdOrderByCreatedDateDesc(any(Pageable.class), anyLong()))
                .thenReturn(page);
        when(modelMapper.map(notification, NotificationReadDto.class))
                .thenReturn(dtoForConversion);

        final Page<NotificationReadDto> actual = notificationService.getAllNotifications(pageable, 1L, ukrainianLanguage);

        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        final NotificationReadDto actualNotification = actual.getContent().stream().findFirst()
                .get();
        assertNotEquals(actualNotification.getTitle(), initialTitle);
    }

    @Test
    void getAllNotifications_ExistentNotificationsInEnglish_Success() {
        List<Notification> list = List.of(notification);
        final NotificationReadDto dtoForConversion = getNotificationReadDto();
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<Notification> page = new PageImpl<>(list, pageable, 1);

        when(notificationRepository.findAllByUserIdOrderByCreatedDateDesc(any(Pageable.class), anyLong()))
                .thenReturn(page);
        when(modelMapper.map(notification, NotificationReadDto.class))
                .thenReturn(dtoForConversion);

        final Page<NotificationReadDto> actual = notificationService.getAllNotifications(pageable, 1L, englishLanguage);

        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        final NotificationReadDto actualNotification = actual.getContent().stream().findFirst()
                .get();
        assertEquals(actualNotification.getTitle(), initialTitle);
    }

    @Test
    void markAsViewed_ExistentNotification_Success() {
        when(notificationRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(emptyNotification));

        notificationService.markAsViewed(1L, 1L);

        assertTrue(emptyNotification.isViewed());
        verify(notificationRepository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void markAsViewed_NoSuchNotification_throwsNotFoundException() {
        when(notificationRepository.findByIdAndUserId(anyLong(), anyLong())).
                thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> notificationService.markAsViewed(1L, 1L),
                "Notification not found");
        verify(notificationRepository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void markAsUnviewed_ExistentNotification_Success() {
        when(notificationRepository.findByIdAndUserId(anyLong(), anyLong())).
                thenReturn(Optional.of(emptyNotification));

        notificationService.markAsUnviewed(1L, 1L);

        assertFalse(notification.isViewed());
        verify(notificationRepository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void markAsUnviewed_NoSuchNotification_throwsNotFoundException() {
        when(notificationRepository.findByIdAndUserId(anyLong(), anyLong())).
                thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> notificationService.markAsUnviewed(1L, 1L),
                "Notification not found");
        verify(notificationRepository).findByIdAndUserId(anyLong(), anyLong());
    }
}