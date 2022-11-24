package com.example.projectscarpingvk.telegram.processngData;

import com.example.projectscarpingvk.tools.WorkWithFiles;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class ProcessingData {

    public ProcessingData(){
        createFolder();
    }

    public SendPhoto getShortUserInfo(GetResponse user, String domain){
        SendPhoto shortInfoAboutUser = new SendPhoto();
        InputFile photo = null;
        try{
            photo = downloadPhoto(user.getPhoto200Orig().toString(), domain);
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }
        shortInfoAboutUser.setPhoto(photo);
        String text = getShortInfo(user);
        shortInfoAboutUser.setCaption(text);
        WorkWithFiles.writeToFile(new File("./files/"+domain+"/"+domain+".short.txt"), text);

        return shortInfoAboutUser;
    }


    private String getShortInfo(GetResponse user){
        StringBuilder info = new StringBuilder();

        if (user==null) return "Not found";
        String name = !emptyOrNull(user.getFirstName()) ? user.getFirstName():"Не указано";
        String lastname = !emptyOrNull(user.getLastName()) ? user.getLastName() : "Не указано";
        String sex = !emptyOrNull(user.getSex()) ? definedSex(user.getSex().getValue()) : "Не указано";
        String birthday = !emptyOrNull(user.getBdate()) ? user.getBdate() : "Не указано";
        int countFriend = user.getCounters().getFriends();
        int countGroups = user.getCounters().getPages();

        StringBuilder sI = new StringBuilder("Имя и фамилия: ");
        sI.append(name).append(" ").append(lastname).append("\nПол: ").append(sex).append("\nДата рождения: ")
                .append(birthday).append("\nКоличество друзей: ").append(countFriend).append("\nКоличество групп: ").append(countGroups);

        return sI.toString();
    }

    private InputFile downloadPhoto(String urlStr, String name) throws IOException{
        BufferedImage image = ImageIO.read(new URL(urlStr));

        File photo = new File(createFolder(name)+"/"+name+".jpg");
        ImageIO.write(image, "jpg", photo);

        return new InputFile(photo);
    }

    private void createFolder(){
        File folder = new File("./files");

        if (!folder.exists()) folder.mkdir();
    }

    private File createFolder(String name){
        File folder = new File("./files/"+name);
        if (!folder.exists()) folder.mkdir();

        return folder;
    }

    private boolean emptyOrNull(Object object){
        return object==null || object=="" || object==" ";
    }

    private String isEmpty(String str){
        if (emptyOrNull(str)) return "";
        else return str;
    }

    private String definedSex(String sex){
        if (sex.equals("1")) return "Женский";
        else if (sex.equals("2")) return "Мужской";
        else return "Не определен";
    }

}
