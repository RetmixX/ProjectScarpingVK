package com.example.projectscarpingvk.telegram.processngData;

import com.example.projectscarpingvk.tools.WorkWithFiles;
import com.example.projectscarpingvk.vk.VK;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ProcessingData {

    public ProcessingData(){
        createFolder();
    }

    public SendPhoto getShortUserInfo(GetResponse user, String domain){
        SendPhoto shortInfoAboutUser = new SendPhoto();
        InputFile photo = null;
        try{
            photo = downloadPhoto(user.getPhoto400Orig().toString(), domain);
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }
        shortInfoAboutUser.setPhoto(photo);
        String text = getInfo(user);
        shortInfoAboutUser.setCaption(text);
        WorkWithFiles.writeToFile(new File("./files/"+domain+"/"+domain+".txt"), text);

        return shortInfoAboutUser;
    }

    public EditMessageMedia getFromFileUser(String domain){
        String path = "./files/"+domain+"/";
        EditMessageMedia user = new EditMessageMedia();
        InputMediaPhoto file = new InputMediaPhoto();
        file.setMedia(new File(path+domain+".jpg"), domain+".jpg");
        file.setCaption(WorkWithFiles.readFromFile(new File(path+domain+".txt")));
        user.setMedia(file);
        return user;
    }



    private String getInfo(GetResponse user){
        StringBuilder info = new StringBuilder();

        if (user==null) return "Not found";

        String name = "Имя: "+(!emptyOrNull(user.getFirstName()) ? user.getFirstName():"none");
        String lastname = "Фамилия: "+(!emptyOrNull(user.getLastName()) ? user.getLastName() : "none");
        String sex = "Пол: "+(!emptyOrNull(user.getSex()) ? definedSex(user.getSex().getValue()) : "none");
        String birthday = "Дата рождения: "+(!emptyOrNull(user.getBdate()) ? user.getBdate() : "none");
        String city ="Город: " + (!emptyOrNull(user.getCity()) ? user.getCity().getTitle(): "none");
        String status = "Статус: "+ (!emptyOrNull(user.getStatus()) ? user.getStatus() : "none");
        String site = "Сайт: " +(!emptyOrNull(user.getSite()) ? user.getSite() : "none");
        int countFriend = !emptyOrNull(user.getCounters().getFriends()) ? user.getCounters().getFriends() : 0;
        int countGroups = !emptyOrNull(user.getCounters().getPages())? user.getCounters().getPages(): 0;

        List<String> fields = Arrays.asList(name, lastname, sex, birthday, city, status, site);

        for (var item: fields)
            if (!item.endsWith("none"))
                info.append(item).append("\n");

        if (countFriend!=0) info.append("Количество друзей: ").append(countFriend).append("\n");

        if (countGroups!=0) info.append("Количество подписок: ").append(countGroups).append("\n");

        return info.toString();
    }

    private InputFile downloadPhoto(String urlStr, String name) throws IOException{
        BufferedImage image = ImageIO.read(new URL(urlStr));

        File photo = new File(createFolder(name)+"/"+name+".jpg");
        ImageIO.write(image, "jpg", photo);

        return new InputFile(photo);
    }

    private void downloadPhotoForArchive(String urlStr, String domain, String name){
        String path = domain+"/photos";
        try {
            URL url = new URL(urlStr);
            BufferedImage image = ImageIO.read(url);
            File file = new File(createFolder(path)+"/" + name + ".jpg");
            ImageIO.write(image, "jpg", file);
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
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

    public int downloadPhotoOffset(int userId, int countPhoto, VK vk, String domain) {
        int iteration = countPhoto / 20 + 1;
        int countPhotoAlbum = 0;
        for (int i = 0; i < iteration; i++) {
            JsonArray items = vk.takePhoto(userId, i);
            int size = items.size();
            for (int j = 0; j < size; j++) {
                countPhotoAlbum++;
                JsonArray sizePhoto1 = items.get(j).getAsJsonObject().getAsJsonArray("sizes");
                for (JsonElement element : sizePhoto1) {
                    if (element.getAsJsonObject().get("type").getAsString().equals("r")) {
                        downloadPhotoForArchive(element.getAsJsonObject().get("url").getAsString(), domain, "Album_" + countPhotoAlbum);
                    }
                }
            }
        }

        return countPhoto;
    }

}
