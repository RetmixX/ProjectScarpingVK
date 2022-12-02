package com.example.projectscarpingvk.service.telegram;

import com.example.projectscarpingvk.config.AppConfig;
import com.example.projectscarpingvk.models.Status;
import com.example.projectscarpingvk.models.UserTelegram;
import com.example.projectscarpingvk.service.UserTelegramService;
import com.example.projectscarpingvk.service.telegram.dataVK.API;
import com.example.projectscarpingvk.service.telegram.helper.PhotoThroughInputStream;
import com.example.projectscarpingvk.service.telegram.keyboard.ButtonID;
import com.example.projectscarpingvk.service.telegram.keyboard.Keyboard;
import com.example.projectscarpingvk.tools.WorkWithFiles;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final AppConfig appConfig;

    private final UserTelegramService userTelegramService;
    private final com.example.projectscarpingvk.service.telegram.dataVK.API API;

    @Autowired
    public TelegramBot(AppConfig appConfig, UserTelegramService userTelegramService, API API){
        this.appConfig = appConfig;
        this.userTelegramService = userTelegramService;
        this.API = API;
    }

    @Override
    public String getBotUsername() {
        return appConfig.getNameBot();
    }

    @Override
    public String getBotToken() {
        return appConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            String message = update.getMessage().getText();
            if (message.startsWith("/"))
                textCommands(message, update);

            else processingSendChatText(update);

        } else if (update.hasCallbackQuery()) {
            textButton(update.getCallbackQuery(), new Keyboard());
        }
    }

    private void textButton(CallbackQuery callbackQuery, Keyboard keyboard){
        Long idUser = callbackQuery.getMessage().getChatId();
        String chatId = String.valueOf(idUser);
        String domain = userTelegramService.getDomain(idUser);
        String telegramUser = userTelegramService.getUserById(idUser).getId().toString();
        int messageId = callbackQuery.getMessage().getMessageId();
        ButtonID pressedButton = ButtonID.valueOf(callbackQuery.getData());

        switch (pressedButton){
            case START_SCARPING:
                userTelegramService.changeStatus(Status.INPUT_DOMAIN, idUser);
                WorkWithFiles.deleteFolder(chatId,userTelegramService.getDomain(idUser));
                sendMessage("Введите адрес пользователя VK", chatId);
                break;

            case ABOUT:
                sendPhotoWithStream(keyboard.drawAbout(chatId, messageId));
                break;

            case FULL_DATA:
                sendPhotoWithStream(keyboard.drawFilesMenu(chatId, messageId));
                break;

            case GET_PHOTO:
                try {
                    SendDocument archive = API.getArchiveWithPhoto(telegramUser,userTelegramService.getDomain(idUser), chatId);
                    sendDocument(archive);
                }catch (NullPointerException ex){
                    sendMessage("У данного пользователя нет фотографий или они скрыты", chatId);
                }
                break;

            case GET_POST:
            {
                try{
                    SendDocument groupsInfo = API.analyzeUserGroups(telegramUser,userTelegramService.getDomain(idUser), chatId);
                    sendDocument(groupsInfo);
                }catch (ClientException | ApiException | NullPointerException exception){
                    sendMessage("Группы пользователя либо скрыты, либо он не подписан ни на одну группу", chatId);
                } catch (NotFoundException e) {
                    sendMessage(e.getMessage(), chatId);
                }
                break;
            }

            case BACK_SHORT:
                EditMessageMedia media = API.getFromFileUser(domain, chatId, messageId, telegramUser);
                sendEditPhotoMessage(keyboard.drawInfoUser(media));
                break;

            case BACK:
                sendPhotoWithStream(keyboard.drawMenu(chatId, messageId));
                break;
        }

    }

    private void processingSendChatText(Update update){
        Keyboard keyboard = new Keyboard();
        Long id = update.getMessage().getChatId();
        String chatId = String.valueOf(id);
        UserTelegram user = userTelegramService.getUserById(id);
        String message = update.getMessage().getText();

        switch (user.getStatus()){
            case INPUT_DOMAIN:
                try {
                    SendPhoto shortInfo = API.getShortInfoAboutUser(message, String.valueOf(id), String.valueOf(id));
                    if (shortInfo!=null){
                        sendPhotoMessage(keyboard.drawShortInfoUser(shortInfo));
                        userTelegramService.changeDomain(message, id);
                        userTelegramService.changeStatus(Status.SCARPING, id);
                    }
                    else sendMessage("Пользователь не найден!", chatId);
                }catch (ClientException | ApiException | NotFoundException ex){
                    sendMessage(ex.getMessage(), chatId);
                }
                break;
        }
    }

    private void textCommands(String commandUser, Update update){
        Long id = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(id));
        if ("/start".equals(commandUser)) {
            userTelegramService.registration(id);
            Keyboard keyboard = new Keyboard();
            PhotoThroughInputStream send = keyboard.drawMenu(String.valueOf(id));
            sendPhotoMessage(send.getSendPhoto());
            send.closeStream();

        }
    }

    private void sendPhotoWithStream(PhotoThroughInputStream photo){
        sendEditPhotoMessage(photo.getEditMessageMedia());
        photo.closeStream();
    }

    private void sendTextMessage(SendMessage message, String text){
        message.setText(text);
        try{
            execute(message);
        }catch (TelegramApiException exception){
            System.out.println(exception.getMessage());
        }
    }

    private void sendMessage(String message, String chatId){
        SendMessage sendMessage = new SendMessage(chatId, message);
        try{
            execute(sendMessage);
        }catch (TelegramApiException telegramApiException){
            System.out.println(telegramApiException.getMessage());
        }
    }

    private void sendPhotoMessage(SendPhoto photoWithText){
        try{
            execute(photoWithText);
        }catch (TelegramApiException telegramApiException){
            System.out.println(telegramApiException.getMessage());
        }
    }

    private void sendEditPhotoMessage(EditMessageMedia photo){
        try{
            execute(photo);
        }catch (TelegramApiException telegramApiException){
            System.out.println(telegramApiException.getMessage());
        }
    }

    private void sendDocument(SendDocument document){
        try{
            execute(document);
        }catch (TelegramApiException telegramApiException){
            System.out.println(telegramApiException.getMessage());
        }
    }
}
