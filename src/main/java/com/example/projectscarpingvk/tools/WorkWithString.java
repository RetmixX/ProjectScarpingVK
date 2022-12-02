package com.example.projectscarpingvk.tools;

public class WorkWithString {

    public static boolean emptyOrNull(Object object){
        return object==null || object=="" || object==" ";
    }

    public static String isEmpty(String str){
        if (emptyOrNull(str)) return "";
        else return str;
    }

    public static String definedSex(String sex){
        if (sex.equals("1")) return "Женский";
        else if (sex.equals("2")) return "Мужской";
        else return "Не определен";
    }
}
