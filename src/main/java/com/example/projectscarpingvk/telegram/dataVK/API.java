package com.example.projectscarpingvk.telegram.dataVK;

import com.example.projectscarpingvk.telegram.processngData.ProcessingData;
import com.example.projectscarpingvk.tools.DownloadPhoto;
import com.example.projectscarpingvk.tools.WorkWithFiles;
import com.example.projectscarpingvk.vk.VK;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Service
public class API {


    private VK vkAPI;
    @Autowired
    public API(VK vkAPI){
        this.vkAPI = vkAPI;
    }


    public SendPhoto getShortInfoAboutUser(String domain, String chatId, String telegramUser) throws ClientException, ApiException, NotFoundException {
        var user = vkAPI.findUser(domain);
        ProcessingData processingData = new ProcessingData(telegramUser);
        SendPhoto sendPhoto = processingData.getShortUserInfo(user, domain);
        sendPhoto.setChatId(chatId);

        return sendPhoto;
    }

    public EditMessageMedia getFromFileUser(String domain, String chatId, int messageId, String telegramUser){
        ProcessingData processingData = new ProcessingData(telegramUser);
        EditMessageMedia user = processingData.getFromFileUser(domain);
        user.setChatId(chatId);
        user.setMessageId(messageId);
        return user;
    }

    public SendDocument getArchiveWithPhoto(String telegramUser,String domain, String chatId) throws NullPointerException{
        SendDocument sendDocument = new SendDocument();
        String path = "./files/"+telegramUser+"/"+domain+"/";
        try{
            var user = vkAPI.findUser(domain);

            if (user.getCounters().getPhotos()==null)
                throw new NullPointerException("Фото пользователя скрыты или профиль закрыт");

            int countFiles = DownloadPhoto.downloadPhotoOffset(user.getId(), user.getCounters().getPhotos(), vkAPI, telegramUser, domain);
            WorkWithFiles.createArchive(countFiles, domain, path, path+"/photos");
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(new File(path+domain+".zip")));

        }catch (ApiException | ClientException ex){
            System.out.println(ex.getMessage());
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }

        return sendDocument;
    }

    public SendDocument analyzeUserGroups(String telegramUser,String domain, String chatId) throws ClientException, ApiException, NotFoundException, NullPointerException {
        SendDocument sendDocument = null;
        String path = "./files/"+domain+"/";
        var user = vkAPI.findUser(domain);
        ProcessingData processingData = new ProcessingData(telegramUser);
        sendDocument = processingData.processingGroup(vkAPI.getGroupsUser(user.getId()), domain);
        sendDocument.setChatId(chatId);

        return sendDocument;
    }
}
