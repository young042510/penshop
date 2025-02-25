package com.reminder.penshop.notification.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

	private String notificationId;
	private String receiver; //id
	private String content;
	private String notificationType;
	private String url;
	private char readYn;
	private char deletedYn;
	private String createdAt;
	private String updatedAt;
	
	public static Notification of(Notification notification) {
		return Notification.builder()
				.notificationId(notification.getNotificationId())
				.receiver(notification.getReceiver())
				.content(notification.getContent())
				.notificationType(notification.getNotificationType())
				.url(notification.getUrl())
				.readYn(notification.getReadYn())
				.deletedYn(notification.getDeletedYn())
				.createdAt(notification.getCreatedAt())
				.updatedAt(notification.getUpdatedAt())
				.build();
	}
}
