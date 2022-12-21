package com.example.projectscarpingvk.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkWithString {

    public static boolean emptyOrNull(Object object){
        return object==null || object=="" || object==" ";
    }

    public static String definedSex(String sex){
        if (sex.equals("1")) return "Женский";
        else if (sex.equals("2")) return "Мужской";
        else return "Не определен";
    }

    public static String definePlatform(int numberPlatform){
        switch (numberPlatform){
            case 1:
                return "Мобильная версия сайта";
            case 2:
                return "iPhone";
            case 3:
                return "iPad";
            case 4:
                return "Android";
            case 5:
                return "Windows Phone";
            case 6:
                return "Приложение для Windows";
            case 7:
                return "Сайт";
            default: return "Не определенная платформа";
        }
    }

    public static String convertUNIXToDateString(long unixTime) {

        SimpleDateFormat formatter = new SimpleDateFormat("H:mm || dd MMMM yyyy");
        Date date = new Date(unixTime * 1000L);
        return formatter.format(date);
    }
}
