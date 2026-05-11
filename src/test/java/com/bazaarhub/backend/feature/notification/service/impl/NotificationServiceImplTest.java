package com.bazaarhub.backend.feature.notification.service.impl;

import com.bazaarhub.backend.feature.notification.entity.Notification;
import com.bazaarhub.backend.feature.notification.enums.NotificationType;
import com.bazaarhub.backend.feature.notification.repository.NotificationRepository;
import com.bazaarhub.backend.feature.notification.resource.NotificationResponseDto;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");

        notification = new Notification();
        ReflectionTestUtils.setField(notification, "id", 10L);
        notification.setRecipient(user);
        notification.setTitle("Order placed");
        notification.setMessage("Your order has been placed successfully.");
        notification.setType(NotificationType.ORDER_PLACED);
        notification.setReferenceId(100L);
        notification.setIsRead(false);
    }

    @Test
    void createNotification_shouldCreateAndSendNotification() {
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        NotificationResponseDto result =
                notificationService.createNotification(
                        user,
                        "Order placed",
                        "Your order has been placed successfully.",
                        NotificationType.ORDER_PLACED,
                        100L
                );

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Order placed", result.getTitle());
        assertEquals("Your order has been placed successfully.", result.getMessage());
        assertEquals(NotificationType.ORDER_PLACED, result.getType());
        assertFalse(result.getIsRead());
        assertEquals(100L, result.getReferenceId());

        verify(notificationRepository, times(1))
                .save(any(Notification.class));

        verify(simpMessagingTemplate, times(1))
                .convertAndSendToUser(
                        eq("user@gmail.com"),
                        eq("/queue/notifications"),
                        any(NotificationResponseDto.class)
                );
    }

    @Test
    void getMyNotifications_shouldReturnUserNotifications() {
        Notification notification2 = new Notification();
        ReflectionTestUtils.setField(notification2, "id", 11L);
        notification2.setRecipient(user);
        notification2.setTitle("Payment successful");
        notification2.setMessage("Your payment has been completed successfully.");
        notification2.setType(NotificationType.PAYMENT_SUCCESS);
        notification2.setReferenceId(101L);
        notification2.setIsRead(false);

        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification, notification2));

        List<NotificationResponseDto> result =
                notificationService.getMyNotifications(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Order placed", result.get(0).getTitle());
        assertEquals("Payment successful", result.get(1).getTitle());

        verify(notificationRepository, times(1))
                .findByRecipientIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getUnreadCount_shouldReturnUnreadCount() {
        when(notificationRepository.countByRecipientIdAndIsReadFalse(1L))
                .thenReturn(3L);

        long result = notificationService.getUnreadCount(1L);

        assertEquals(3L, result);

        verify(notificationRepository, times(1))
                .countByRecipientIdAndIsReadFalse(1L);
    }

    @Test
    void markAsRead_shouldMarkNotificationAsRead_whenOwnerMatches() {
        when(notificationRepository.findById(10L))
                .thenReturn(Optional.of(notification));

        notification.setIsRead(true);

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        NotificationResponseDto result =
                notificationService.markAsRead(10L, 1L);

        assertNotNull(result);
        assertTrue(result.getIsRead());

        verify(notificationRepository, times(1))
                .findById(10L);

        verify(notificationRepository, times(1))
                .save(notification);
    }

    @Test
    void markAsRead_shouldThrowException_whenNotificationNotFound() {
        when(notificationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAsRead(99L, 1L)
        );

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    @Test
    void markAsRead_shouldThrowException_whenUserDoesNotOwnNotification() {
        when(notificationRepository.findById(10L))
                .thenReturn(Optional.of(notification));

        assertThrows(
                RuntimeException.class,
                () -> notificationService.markAsRead(10L, 99L)
        );

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    @Test
    void markAllAsRead_shouldMarkAllUnreadNotificationsAsRead() {
        Notification notification2 = new Notification();
        ReflectionTestUtils.setField(notification2, "id", 11L);
        notification2.setRecipient(user);
        notification2.setTitle("Payment failed");
        notification2.setMessage("Your payment has failed.");
        notification2.setType(NotificationType.PAYMENT_FAILED);
        notification2.setReferenceId(102L);
        notification2.setIsRead(false);

        List<Notification> unreadNotifications =
                List.of(notification, notification2);

        when(notificationRepository.findByRecipientIdAndIsReadFalse(1L))
                .thenReturn(unreadNotifications);

        when(notificationRepository.saveAll(unreadNotifications))
                .thenReturn(unreadNotifications);

        notificationService.markAllAsRead(1L);

        assertTrue(notification.getIsRead());
        assertTrue(notification2.getIsRead());

        verify(notificationRepository, times(1))
                .findByRecipientIdAndIsReadFalse(1L);

        verify(notificationRepository, times(1))
                .saveAll(unreadNotifications);
    }

}