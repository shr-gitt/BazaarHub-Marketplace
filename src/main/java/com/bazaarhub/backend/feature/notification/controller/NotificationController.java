package com.bazaarhub.backend.feature.notification.controller;

import com.bazaarhub.backend.feature.notification.resource.NotificationResponseDto;
import com.bazaarhub.backend.feature.notification.service.NotificationService;
import com.bazaarhub.backend.shared.aop.LogExecutionTime;
import com.bazaarhub.backend.shared.enums.ResponseStatus;
import com.bazaarhub.backend.shared.resource.ApiResponseDto;
import com.bazaarhub.backend.shared.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @LogExecutionTime
    public ApiResponseDto<List<NotificationResponseDto>> getMyNotifications(Principal principal) {
        Long userId = getCurrentUserIdSomehow();

        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Notifications fetched successfully.",
                notificationService.getMyNotifications(userId)
        );
    }

    @GetMapping("/unread-count")
    @LogExecutionTime
    public ApiResponseDto<Long> getUnreadCount(Principal principal) {
        Long userId = getCurrentUserIdSomehow();

        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Unread notification count fetched successfully.",
                notificationService.getUnreadCount(userId)
        );
    }

    @PatchMapping("/{notificationId}/read")
    @LogExecutionTime
    public ApiResponseDto<NotificationResponseDto> markAsRead(
            @PathVariable Long notificationId,
            Principal principal
    ) {
        Long userId = getCurrentUserIdSomehow();

        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "Notification marked as read.",
                notificationService.markAsRead(notificationId, userId)
        );
    }

    @PatchMapping("/read-all")
    @LogExecutionTime
    public ApiResponseDto<Void> markAllAsRead(Principal principal) {
        Long userId = getCurrentUserIdSomehow();

        notificationService.markAllAsRead(userId);

        return new ApiResponseDto<>(
                ResponseStatus.SUCCESS.value,
                "All notifications marked as read.",
                null
        );
    }

    private Long getCurrentUserIdSomehow() {

        return AuthUtil.getCurrentUserId();
    }
}
