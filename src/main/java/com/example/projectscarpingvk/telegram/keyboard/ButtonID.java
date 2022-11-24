package com.example.projectscarpingvk.telegram.keyboard;

public enum ButtonID {

    YES("YES_BUTTON"),
    NO("NO_BUTTON"),
    NEXT("NEXT"),
    BACK("BACK"),
    START_SCARPING("START_SCARPING"),
    FULL_DATA("FULL_DATA"),
    CHARACTER_USER("CHARACTER_USER"),
    GET_PHOTO("GET_PHOTO"),
    GET_POSTS("GET_POST"),
    BACK_SHORT("BACK_SHORT"),
    ABOUT("ABOUT");


    private String idButton;
    ButtonID(String idButton){
        this.idButton = idButton;
    }

    public String value(){
        return this.idButton;
    }

}
