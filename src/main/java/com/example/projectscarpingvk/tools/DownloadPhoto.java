package com.example.projectscarpingvk.tools;

import com.example.projectscarpingvk.vk.VK;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DownloadPhoto {

    private static void downloadPhotoForArchive(String telegramUser,String urlStr, String domain, String name){
        String path = domain+"/photos";
        try {
            URL url = new URL(urlStr);
            BufferedImage image = ImageIO.read(url);
            File file = new File(WorkWithFiles.createFolder(telegramUser, path)+"/" + name + ".jpg");
            ImageIO.write(image, "jpg", file);

        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }

    public static InputFile downloadPhoto(String urlStr, String telegramUser, String name) throws IOException{
        BufferedImage image = ImageIO.read(new URL(urlStr));

        File photo = new File(WorkWithFiles.createFolder(telegramUser,name)+"/"+name+".jpg");
        ImageIO.write(image, "jpg", photo);

        return new InputFile(photo);
    }


    public static int downloadPhotoOffset(int userId, int countPhoto, VK vk, String telegramUser ,String domain) {
        int iteration = countPhoto / 20 + 1;
        int countPhotoAlbum = 0;
        for (int i = 0; i < iteration; i++) {
            JsonArray items = vk.takePhoto(userId, i);
            int size = items.size();
            for (int j = 0; j < size; j++) {
                countPhotoAlbum++;
                JsonArray sizePhoto1 = items.get(j).getAsJsonObject().getAsJsonArray("sizes");

                for (JsonElement element : sizePhoto1) {
                    if (element.getAsJsonObject().get("type").getAsString().equals("y")) {
                        downloadPhotoForArchive(telegramUser, element.getAsJsonObject().get("url").getAsString(), domain, "Album_" + countPhotoAlbum);
                    }
                }
            }
        }

        return countPhoto;
    }


}
