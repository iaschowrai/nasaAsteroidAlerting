package com.asteroidalerting.repository;

import com.asteroidalerting.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByEmailSent(boolean b);
}
