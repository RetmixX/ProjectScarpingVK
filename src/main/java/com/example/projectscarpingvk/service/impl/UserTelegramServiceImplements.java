package com.example.projectscarpingvk.service.impl;

import com.example.projectscarpingvk.exceptions.UserTelegramNotFound;
import com.example.projectscarpingvk.models.Status;
import com.example.projectscarpingvk.models.UserTelegram;
import com.example.projectscarpingvk.repositories.UserTelegramRepository;
import com.example.projectscarpingvk.service.UserTelegramService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTelegramServiceImplements implements UserTelegramService {

    @Autowired
    private UserTelegramRepository userTelegramRepository;

    @Override
    public void registration(Long id) {
        if (!userTelegramRepository.existsById(id))
            userTelegramRepository.save(new UserTelegram(id, Status.FREE, ""));
    }

    @Override
    public UserTelegram getUserById(Long id) {
        return userTelegramRepository.findUserTelegramById(id);
    }

    @Override
    public void changeStatus(Status status, Long id){
        UserTelegram userTelegram = userTelegramRepository.findUserTelegramById(id);
        userTelegram.setStatus(status);
        userTelegramRepository.save(userTelegram);
    }

    @Override
    public void changeDomain(String domain, Long id){
        UserTelegram userTelegram = userTelegramRepository.findUserTelegramById(id);
        userTelegram.setDomain(domain);
        userTelegramRepository.save(userTelegram);
    }

    @Override
    public String getStatus(Long id){
        return userTelegramRepository.findUserTelegramById(id).getStatus().name();
    }

    @Override
    public String getDomain(Long id){
        return userTelegramRepository.findUserTelegramById(id).getDomain();
    }

    private boolean userExist(Long id){
        return userTelegramRepository.existsById(id);
    }
}
