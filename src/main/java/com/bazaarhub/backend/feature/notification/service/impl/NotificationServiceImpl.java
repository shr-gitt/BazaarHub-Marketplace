package com.bazaarhub.backend.feature.notification.service.impl;

import com.bazaarhub.backend.feature.notification.entity.Notification;
import com.bazaarhub.backend.feature.notification.enums.NotificationType;
import com.bazaarhub.backend.feature.notification.repository.NotificationRepository;
import com.bazaarhub.backend.feature.notification.resource.NotificationResponseDto;
import com.bazaarhub.backend.feature.notification.service.NotificationService;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public NotificationResponseDto createNotification(User recipient, String title, String message, NotificationType type, Long referenceId) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);

        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponseDto responseDto = mapToDto(savedNotification);

        simpMessagingTemplate.convertAndSendToUser(
                recipient.getEmail(),
                "/queue/notifications",
                responseDto
        );
        return responseDto;
    }

    @Override
    public List<NotificationResponseDto> getMyNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Override
    public NotificationResponseDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this notification");
        }

        notification.setIsRead(true);

        return mapToDto(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications =
                notificationRepository.findByRecipientIdAndIsReadFalse(userId);

        unreadNotifications.forEach(notification -> notification.setIsRead(true));

        notificationRepository.saveAll(unreadNotifications);
    }

    private NotificationResponseDto mapToDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
