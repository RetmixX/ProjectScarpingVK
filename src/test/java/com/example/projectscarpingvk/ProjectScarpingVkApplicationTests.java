package com.example.projectscarpingvk;

import com.example.projectscarpingvk.tools.WorkWithFiles;
import com.example.projectscarpingvk.tools.WorkWithString;
import com.example.projectscarpingvk.vk.VK;
import javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class ProjectScarpingVkApplicationTests {
    @Autowired
    private BotInit botInit;

    @Autowired
    private VK vk;


    @Test
    public void testWorkRegisterBot() {
        Assertions.assertDoesNotThrow(() -> {
            botInit.start();
        });
    }

    @Test
    public void testWorkVKAPI() {
        Assertions.assertDoesNotThrow(() -> {
            vk.findUser("durov");
        });
    }

    @Test
    public void testFindUser() {
        String validUser = "id329203536";
        String notValidUser = "aksuatf8g312";
        Assertions.assertDoesNotThrow(() -> {
            vk.findUser(validUser);
        });

        Assertions.assertThrows(NotFoundException.class, () -> {
            vk.findUser(notValidUser);
        });
    }

    @Test
    public void testGetGroup(){
        int idOpenGroup = 598222987;
        int idClosedGroup = 7846830;

        Assertions.assertDoesNotThrow(()->vk.getGroupsUser(idOpenGroup));
        Assertions.assertThrows(Exception.class, ()->vk.getGroupsUser(idClosedGroup));
    }

    @Test
    public void testCreateFolder() {
        Assertions.assertInstanceOf(File.class, WorkWithFiles.createFolder("test", "test_name"));
    }

    @Test
    public void testDefinePlatform() {
        int iPad = 2;
        int android = 4;
        int noPlatform = 90;

        Assertions.assertEquals("iPhone", WorkWithString.definePlatform(iPad));
        Assertions.assertEquals("Android", WorkWithString.definePlatform(android));
        Assertions.assertEquals("Не определенная платформа", WorkWithString.definePlatform(noPlatform));
    }

    @Test
    public void testDefineSex(){
        String male = "2";
        String female = "1";

        Assertions.assertEquals("Женский", WorkWithString.definedSex(female));
        Assertions.assertEquals("Мужской", WorkWithString.definedSex(male));
    }

    @Test
    public void testConvertToUnixTime(){
        long unix = 1671631410;
        String validDate = "21:03 || 21 декабря 2022";
        Assertions.assertEquals(validDate, WorkWithString.convertUNIXToDateString(unix));
    }



}
