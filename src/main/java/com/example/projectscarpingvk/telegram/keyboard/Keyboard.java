package com.example.projectscarpingvk.telegram.keyboard;

import com.example.projectscarpingvk.telegram.helper.PhotoThroughInputStream;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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


    public PhotoThroughInputStream drawFilesMenu(String chatId, int messageId){
        InputStream file = this.getClass().getResourceAsStream("/photo/files.jpg");
        InputMediaPhoto photo = new InputMediaPhoto();
        photo.setMedia(file, "files.jpg");
        photo.setCaption("Как только вы нажмете какую-либо из кнопок,\n Вам придется подождать, какое-то время,\n пока не завершится загрузка файлов");
        EditMessageMedia messageMedia = new EditMessageMedia();
        messageMedia.setChatId(chatId);
        messageMedia.setMessageId(messageId);
        messageMedia.setMedia(photo);

        messageMedia.setReplyMarkup(new InlineKeyboardMarkup(
                createRowsInLine(
                        createRowInLine(createButton("Архив с фото", ButtonID.GET_PHOTO.value())),
                        createRowInLine(createButton("Группы", ButtonID.GET_POST.value())),
                        createRowInLine(createButton("<< Назад", ButtonID.BACK_SHORT.value()))
                )
        ));

        return new PhotoThroughInputStream(messageMedia, file);
    }

    public EditMessageMedia drawInfoUser(EditMessageMedia editMessageMedia){
        editMessageMedia.setReplyMarkup(markupShortUserInfo());
        return editMessageMedia;
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
        return new InlineKeyboardMarkup(createRowsInLine(
                createRowInLine(createButton("Файлы", ButtonID.FULL_DATA.value())),

                createRowInLine(createButton("Поиск другого человека", ButtonID.START_SCARPING.value())),
                createRowInLine(createButton("В меню", ButtonID.BACK.value()))
                ));
    }
}
