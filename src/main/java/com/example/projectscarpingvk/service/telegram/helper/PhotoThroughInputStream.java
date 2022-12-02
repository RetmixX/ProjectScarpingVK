package com.example.projectscarpingvk.service.telegram.helper;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;

import java.io.IOException;
import java.io.InputStream;

public class PhotoThroughInputStream {
    private SendPhoto sendPhoto;

    private EditMessageMedia editMessageMedia;
    private InputStream inputStream;

    public PhotoThroughInputStream(SendPhoto sendPhoto, InputStream inputStream) {
        this.sendPhoto = sendPhoto;
        this.inputStream = inputStream;
    }

    public PhotoThroughInputStream(EditMessageMedia editMessageMedia, InputStream inputStream){
        this.editMessageMedia = editMessageMedia;
        this.inputStream = inputStream;
    }

    public SendPhoto getSendPhoto(){
        return sendPhoto;
    }

    public EditMessageMedia getEditMessageMedia(){
        return this.editMessageMedia;
    }

    public void closeStream(){
        try{
            inputStream.close();
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }
    }
}
