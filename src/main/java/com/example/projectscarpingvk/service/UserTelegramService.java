package com.example.projectscarpingvk.service;

import com.example.projectscarpingvk.exceptions.UserTelegramNotFound;
import com.example.projectscarpingvk.models.Status;
import com.example.projectscarpingvk.models.UserTelegram;

public interface UserTelegramService {
    void registration(Long id);

    UserTelegram getUserById(Long id);

    void changeStatus(Status status, Long Id);

    void changeDomain(String domain, Long id);

    String getStatus(Long id) ;

    String getDomain(Long id);
}
