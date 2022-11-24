package com.example.projectscarpingvk.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WorkWithFiles {

    public static void writeToFile(File file, String text){
        try{
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
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

        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }

        return text.toString();
    }

}
