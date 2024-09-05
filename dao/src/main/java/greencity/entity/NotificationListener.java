package greencity.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class NotificationListener {
    @PrePersist
    @PreUpdate
    public void viewDate(Notification notification) {
        if (notification.isViewed()) {
            notification.setViewedDate(LocalDateTime.now());
        } else {
            notification.setViewedDate(null);
        }
    }
}
