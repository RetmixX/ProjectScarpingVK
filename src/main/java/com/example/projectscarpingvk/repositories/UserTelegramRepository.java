package com.example.projectscarpingvk.repositories;

import com.example.projectscarpingvk.models.UserTelegram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTelegramRepository extends JpaRepository<UserTelegram, Long> {

    UserTelegram findUserTelegramById(Long id);
}
