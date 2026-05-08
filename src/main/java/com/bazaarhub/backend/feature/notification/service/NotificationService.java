package com.bazaarhub.backend.feature.notification.service;

import com.bazaarhub.backend.feature.notification.enums.NotificationType;
import com.bazaarhub.backend.feature.notification.resource.NotificationResponseDto;
import com.bazaarhub.backend.feature.user.entity.User;

import java.util.List;

public interface NotificationService {
    NotificationResponseDto createNotification(
            User recipient,
            String title,
            String message,
            NotificationType type,
            Long referenceId
    );

    List<NotificationResponseDto> getMyNotifications(Long userId);

    long getUnreadCount(Long userId);

    NotificationResponseDto markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
