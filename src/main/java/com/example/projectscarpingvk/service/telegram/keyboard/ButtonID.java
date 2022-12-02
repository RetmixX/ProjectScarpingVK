package com.example.projectscarpingvk.service.telegram.keyboard;

public enum ButtonID {
    BACK("BACK"),
    START_SCARPING("START_SCARPING"),
    FULL_DATA("FULL_DATA"),
    CHARACTER_USER("CHARACTER_USER"),
    GET_PHOTO("GET_PHOTO"),
    GET_POST("GET_POST"),
    BACK_SHORT("BACK_SHORT"),
    BACK_HEAD("BACK_HEAD"),
    ABOUT("ABOUT");


    private String idButton;
    ButtonID(String idButton){
        this.idButton = idButton;
    }

    public String value(){
        return this.idButton;
    }

}
