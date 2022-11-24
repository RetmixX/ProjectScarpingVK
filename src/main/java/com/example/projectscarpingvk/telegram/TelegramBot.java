package com.example.projectscarpingvk.telegram;

import com.example.projectscarpingvk.config.AppConfig;
import com.example.projectscarpingvk.exceptions.UserTelegramNotFound;
import com.example.projectscarpingvk.models.Status;
import com.example.projectscarpingvk.models.UserTelegram;
import com.example.projectscarpingvk.service.UserTelegramService;
import com.example.projectscarpingvk.telegram.dataVK.API;
import com.example.projectscarpingvk.telegram.helper.PhotoThroughInputStream;
import com.example.projectscarpingvk.telegram.keyboard.ButtonID;
import com.example.projectscarpingvk.telegram.keyboard.Keyboard;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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
    private final API API;

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
        int messageId = callbackQuery.getMessage().getMessageId();
        ButtonID pressedButton = ButtonID.valueOf(callbackQuery.getData());

        switch (pressedButton){
            case START_SCARPING:
                userTelegramService.changeStatus(Status.INPUT_DOMAIN, idUser);
                sendMessage("Введите адрес пользователя VK", chatId);
                break;

            case ABOUT:
                sendPhotoWithStream(keyboard.drawAbout(chatId, messageId));
                break;

            case BACK:
                sendPhotoWithStream(keyboard.drawMenu(chatId, messageId));
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
                SendPhoto shortInfo = API.getShortInfoAboutUser(message, String.valueOf(id));
                if (shortInfo!=null){
                    sendPhotoMessage(keyboard.drawShortInfoUser(shortInfo));
                    userTelegramService.changeDomain(message, id);
                    userTelegramService.changeStatus(Status.SCARPING, id);
                }
                else sendMessage("Пользователь не найден!", chatId);
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
}
