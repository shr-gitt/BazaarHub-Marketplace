package com.bazaarhub.backend.feature.notification.resource;

import aQute.bnd.annotation.metatype.Meta;
import com.bazaarhub.backend.feature.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto implements Serializable {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private Long referenceId;
    private LocalDateTime createdAt;

}
