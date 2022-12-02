package com.example.projectscarpingvk.telegram.processngData;

import com.example.projectscarpingvk.telegram.object.Group;
import com.example.projectscarpingvk.tools.DownloadPhoto;
import com.example.projectscarpingvk.tools.WorkWithFiles;
import com.example.projectscarpingvk.vk.VK;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.projectscarpingvk.tools.WorkWithString.definedSex;
import static com.example.projectscarpingvk.tools.WorkWithString.emptyOrNull;

public class ProcessingData {
    private String telegramUser;
    public ProcessingData(String telegramUser){
        this.telegramUser = telegramUser;
        WorkWithFiles.createFolder(telegramUser);
    }

    public SendPhoto getShortUserInfo(GetResponse user, String domain){
        SendPhoto shortInfoAboutUser = new SendPhoto();
        InputFile photo = null;
        try{
            photo = DownloadPhoto.downloadPhoto(user.getPhoto400Orig().toString(), telegramUser ,domain);
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }
        shortInfoAboutUser.setPhoto(photo);
        String text = getInfo(user);
        shortInfoAboutUser.setCaption(text);
        WorkWithFiles.writeToFile(new File("./files/"+telegramUser+"/"+domain+"/"+domain+".txt"), text);

        return shortInfoAboutUser;
    }

    public EditMessageMedia getFromFileUser(String domain){
        String path = "./files/"+telegramUser+"/"+domain+"/";
        EditMessageMedia user = new EditMessageMedia();
        InputMediaPhoto file = new InputMediaPhoto();
        file.setMedia(new File(path+domain+".jpg"), domain+".jpg");
        file.setCaption(WorkWithFiles.readFromFile(new File(path+domain+".txt")));
        user.setMedia(file);
        return user;
    }

    public SendDocument processingGroup(List<Group> groups, String domain){
        String path = "./files/"+telegramUser+"/"+domain+"/";
        File txtFile = new File(path+"groups.txt");

        String[] dataAboutUserGroups = orderGroup(groups);
        WorkWithFiles.writeToFile(txtFile, dataAboutUserGroups[0]);

        SendDocument document = new SendDocument();
        InputFile inputFile = new InputFile(txtFile);

        document.setDocument(inputFile);
        document.setCaption(dataAboutUserGroups[1]);

        return document;
    }

    private String[] orderGroup(List<Group> groups){
        StringBuilder stringBuilder = new StringBuilder();
        int countMem = 0;
        int countArt = 0;
        int countSport = 0;
        int countProg = 0;

        for (Group item : groups) {
            stringBuilder.append("Группа: ").append(item.getTitleGroup()).append("\nДомен: ").append(item.getDomainGroup())
                    .append("\nКол-во: ")
                    .append(item.getCountMembers()).append("\n\n");
            String[] test = String.join(",", item.getTitleGroup().split(" ")).split(",");
            for (String verb : test) {
                if (getMemeList().contains(verb.toLowerCase())) {
                    countMem++;
                    break;
                }
                if (getArtList().contains(verb.toLowerCase())) {
                    countArt++;
                    break;
                }

                if (getSportList().contains(verb.toLowerCase())){
                    countSport++;
                    break;
                }

                if (getProgrammingList().contains(verb.toLowerCase())){
                    countProg++;
                    break;
                }
            }
        }


        String defineUser = determinationOfInterests(new int[]{countMem, countArt, countSport, countProg});
        StringBuilder handleGroup = new StringBuilder();
        handleGroup.append("Мемы: ").append(countMem).append("\nИскусство: ").append(countArt).append("\nСпорт: ")
                .append(countSport).append("\nПрога: ").append(countProg).append("\n");

        return new String[]{handleGroup.toString()+stringBuilder.toString(), defineUser};
    }

    private String determinationOfInterests(int[] categories){
        int max = categories[0];
        int indexCategory = 0;

        for (int i = 0; i<categories.length; i++)
            if (categories[i]>max)
                indexCategory = i;

        if (categories[indexCategory] == 0) indexCategory = -1;

        return defineCategory(indexCategory);
    }

    private String defineCategory(int indexCategory){
        switch (indexCategory){
            case 0:
                return "Человек часто залипает за мемами";
            case 1:
                return "Человек посвящает часто себя искусству";
            case 2:
                return "Человек следит за своим телом";
            case 3:
                return "Его стиль жизни - программист";
            default:
                return "Не могу определить его интересы";
        }
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

    private List<String> getMemeList(){
        return Stream.of("мемы", "мем", "мемасы", "мэмы", "memes", "meme", "mem",
                "pepe", "умора", "мемчики", "мемчиков", "мемная", "мемные", "мемами", "мемах", "мемес", "мемас", "мимас", "мимасики", "мемосы", "мемфоманка",
                "мемпритон", "меме", "мемафия", "мемов", "приколы", "прикольные", "прикольный", "следим", "следит", "знакомо?",
                "заслужили", "bullsht", "bullshit", "shit", "evryshit", "обман", "ftp", "орнул", "смехозависимость", "улыбнуло", "кринж.", "переговоры",
                "переговорный", "переговор", "переговоров", "damn", "энималс", "niggas", "несмешные", "юмор", "юмарески", "юморески", "рофл", "рофлы", "бред", "ирония",
                "постирония", "lmao", "OfficeGuyPublicMemeStash", "пикчи", "попуг", "абсурд", "videosos", "чудаки", "потрачено", "ладно", "бугурты", "бугуртач", "котизм").toList();
    }

    private List<String> getArtList(){
        return Stream.of("арт", "art", "фото", "фотографии", "фотографы", "фотографов", "фильмы", "фильма", "мода", "movie", "movies",
                "culture", "dream", "minimalism", "istomin", "музыка", "music", "NR.Music", "стиль", "style", "эстетика", "эстетично", "арт-хаус", "кино", "artonce",
                "primaere", "искусство", "catharsis", "esth?tique", "perfect", "epotage", "стихи", "lumi?re", "ретроскоп", "cinema", "cybermetabolic", "utopia", "photo", "cyber.", "атмосфера",
                "atmosphere", "любовью", "любовь", "love", "пиксельная", "song", "дизайн", "дизайнер", "design").toList();
    }

    private List<String> getSportList(){
        return Stream.of("спорт", "sport", "здоровье", "тело", "body", "гимнастика", "gymnastic", "футбор", "баскетбол", "плаванье", "качалка", "gym", "качаться", "качаемся", "fitness").toList();
    }

    private List<String> getProgrammingList(){
        return  Stream.of("программирование", "hash", "programming", "it", "айтишник", "айтишные", "python", "питон", "пайтон", "c++", "си", "c", "go", "java", "джава", "c#",
                "программистов", "программист", "прогаем", "программиста", "js", "javascript", "html", "css", "html/css", "веб", "машинное", "данные", "данных", "базы", "sql", "dart", "нейронные",
                "блокчейн", "blockchain", "php", "субд", "code", "код", "hi-tech", "linux", "линус").toList();
    }
}
