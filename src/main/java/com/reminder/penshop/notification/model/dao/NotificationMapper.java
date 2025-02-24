package com.reminder.penshop.notification.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.penshop.notification.model.dto.Notification;

@Mapper
public interface NotificationMapper {

	int insertNotification(Notification notification);
	
	List<Notification> getAllNotificationByUsername(String username);
	
	int updateNotificationReadStatusByUsername(String username);
	
	int updateNotificationDeleteStatusById(String notificationId);

	String checkUsernameByNotificationId(String notificationId);
}
