package com.example.projectscarpingvk.telegram.dataVK;

import com.example.projectscarpingvk.telegram.processngData.ProcessingData;
import com.example.projectscarpingvk.tools.WorkWithFiles;
import com.example.projectscarpingvk.vk.VK;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;

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

    public EditMessageMedia getFromFileUser(String domain, String chatId, int messageId){
        ProcessingData processingData = new ProcessingData();
        EditMessageMedia user = processingData.getFromFileUser(domain);
        user.setChatId(chatId);
        user.setMessageId(messageId);
        return user;
    }

    public SendDocument getArchiveWithPhoto(String domain, String chatId){
        SendDocument sendDocument = new SendDocument();
        String path = "./files/"+domain+"/";
        try{
            var user = vkAPI.findUser(domain);
            ProcessingData processingData = new ProcessingData();
            int countFiles = processingData.downloadPhotoOffset(user.getId(), user.getCounters().getPhotos(), vkAPI, domain);
            WorkWithFiles.createArchive(countFiles, domain, path, path+"/photos");
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(new File(path+domain+".zip")));

        }catch (ApiException | ClientException |IOException ex){
            System.out.println(ex.getMessage());
        }

        return sendDocument;
    }
}
