package com.example.projectscarpingvk.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorkWithFiles {

    public static void writeToFile(File file, String text){
        try{
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }
    }

    public static String readFromFile(File file){
        StringBuilder text = new StringBuilder();
        try {
            FileReader reader = new FileReader(file);
            int c;
            while ((c=reader.read())!=-1){
                text.append((char) c);
            }

            reader.close();

        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }

        return text.toString();
    }

    public static void deleteFolder(String telegramUser,String domain){
        String path = "./files/"+telegramUser+"/"+domain;
        File folder = new File(path);

        if (folder.exists()) recursiveDeleteFiles(folder);
    }


    public static void createArchive(int countFiles, String name, String pathSave, String getPhoto){
        try{
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(pathSave + name + ".zip"));
            for (int i = 1; i <= countFiles; i++) {
                FileInputStream file = null;
                try{
                    file = new FileInputStream(getPhoto+"/Album_" + i + ".jpg");
                }catch (IOException ex){
                    continue;
                }
                ZipEntry entry = new ZipEntry("Album_" + i + ".jpg");
                zout.putNextEntry(entry);
                byte[] buffer = new byte[file.available()];
                file.read(buffer);
                zout.write(buffer);
                zout.closeEntry();
                file.close();
            }
            zout.close();
        }catch (IOException ioException){
            System.out.println(ioException);
        }

        for (File fileDelete : new File(getPhoto).listFiles())
            if (fileDelete.isFile()) fileDelete.delete();
    }

    private static void recursiveDeleteFiles(File folder){
        if (!folder.exists()) return;

        if (folder.isDirectory())
            for (File file : folder.listFiles())
                recursiveDeleteFiles(file);

        folder.delete();
    }

    public static void createFolder(String telegramUser){
        String path = "./files";
        File folder = new File(path);

        if (!folder.exists())
            folder.mkdir();

        File secondFolder = new File(path+"/"+telegramUser);
        if (!secondFolder.exists()){
            secondFolder.mkdir();
        }

    }

    public static File createFolder(String telegramUser, String name){
        File folder = new File("./files/"+telegramUser+"/"+name);
        folder.mkdir();

        return folder;
    }
}
