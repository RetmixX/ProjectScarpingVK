package com.example.projectscarpingvk.telegram.dataVK;

import com.example.projectscarpingvk.telegram.processngData.ProcessingData;
import com.example.projectscarpingvk.vk.VK;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Service
public class API {


    private VK vkAPI;
    @Autowired
    public API(VK vkAPI){
        this.vkAPI = vkAPI;
    }


    public SendPhoto getShortInfoAboutUser(String domain, String chatId){
        SendPhoto sendPhoto = null;
        try{
            var user = vkAPI.findUser(domain);
            ProcessingData processingData = new ProcessingData();
            sendPhoto = processingData.getShortUserInfo(user, domain);
            sendPhoto.setChatId(chatId);

        }catch (ApiException | ClientException ex){
            System.out.println(ex.getMessage());
        }

        return sendPhoto;
    }
}
