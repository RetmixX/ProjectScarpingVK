package com.example.projectscarpingvk.telegram.keyboard;

import com.example.projectscarpingvk.telegram.helper.PhotoThroughInputStream;
import com.example.projectscarpingvk.tools.WorkWithFiles;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Keyboard {

    public PhotoThroughInputStream drawMenu(String chatId){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InputStream file = this.getClass().getResourceAsStream("/photo/main.jpg");

        SendPhoto menu = new SendPhoto(chatId, new InputFile().setMedia(file, "main.jpg"));
        menu.setCaption("<===МЕНЮ===>");

        markup.setKeyboard(createRowsInLine(createRowInLine(
                createButton("Старт", ButtonID.START_SCARPING.value())
        ), createRowInLine(
                createButton("О боте", ButtonID.ABOUT.value()))));

        menu.setReplyMarkup(markup);
        return new PhotoThroughInputStream(menu, file);
    }

    public PhotoThroughInputStream drawMenu(String chatId, int messageId){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InputStream file = this.getClass().getResourceAsStream("/photo/main.jpg");
        InputMediaPhoto photo = new InputMediaPhoto();
        photo.setMedia(file, "main.jpg");
        photo.setCaption("<===МЕНЮ===>");

        EditMessageMedia menu = new EditMessageMedia();
        menu.setMedia(photo);
        menu.setChatId(chatId);
        menu.setMessageId(messageId);

        markup.setKeyboard(createRowsInLine(createRowInLine(
                createButton("Старт", ButtonID.START_SCARPING.value())
        ), createRowInLine(
                createButton("О боте", ButtonID.ABOUT.value()))));

        menu.setReplyMarkup(markup);

        return new PhotoThroughInputStream(menu, file);
    }

    public PhotoThroughInputStream drawAbout(String chatId, int messageId){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InputStream file = this.getClass().getResourceAsStream("/photo/about.jpg");
        InputMediaPhoto photo = new InputMediaPhoto();
        photo.setMedia(file, "about.jpg");
        photo.setCaption("Прототип бота, который парсит пользователя из VK");

        EditMessageMedia about = new EditMessageMedia();
        about.setMedia(photo);
        about.setChatId(chatId);
        about.setMessageId(messageId);

        markup.setKeyboard(createRowsInLine(createRowInLine(createButton("<< Назад", ButtonID.BACK.value()))));

        about.setReplyMarkup(markup);

        return new PhotoThroughInputStream(about, file);
    }

    public EditMessageMedia drawShortInfoUser(String chatId, String domain, int messageId){
        String pathToUserFolder = "./files/"+domain+"/";
        InputMediaPhoto photo = new InputMediaPhoto(pathToUserFolder+domain+".jpg");
        photo.setCaption(WorkWithFiles.readFromFile(new File(pathToUserFolder+domain+".short.text")));

        EditMessageMedia shortInfoUser = new EditMessageMedia();
        shortInfoUser.setMedia(photo);
        shortInfoUser.setMessageId(messageId);
        shortInfoUser.setChatId(chatId);

        shortInfoUser.setReplyMarkup(markupShortUserInfo());

        return shortInfoUser;
    }

    public SendPhoto drawShortInfoUser(SendPhoto sendPhoto){
        sendPhoto.setReplyMarkup(markupShortUserInfo());

        return sendPhoto;
    }

    private InlineKeyboardButton createButton(String text, String idButton){
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(idButton);
        return button;
    }

    private List<InlineKeyboardButton> createRowInLine(InlineKeyboardButton ...buttons){
        return new ArrayList<>(Arrays.asList(buttons));
    }

    private List<List<InlineKeyboardButton>> createRowsInLine(List<InlineKeyboardButton> ...rows){
        return new ArrayList<>(Arrays.asList(rows));
    }

    //Надо будет потом разделить создание клавы от фото, чтобы не было свалки
    private InlineKeyboardMarkup markupShortUserInfo(){
        return new InlineKeyboardMarkup(createRowsInLine(createRowInLine(
                        createButton("Данные", ButtonID.FULL_DATA.value()),
                        createButton("Характер", ButtonID.CHARACTER_USER.value())),

                createRowInLine(
                        createButton("Архив фото", ButtonID.GET_PHOTO.value()),
                        createButton("Посты(Не работает)", ButtonID.GET_POSTS.value())),
                createRowInLine(createButton("Поиск другого человека", ButtonID.START_SCARPING.value()))));
    }
}
