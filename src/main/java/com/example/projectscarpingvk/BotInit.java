package com.example.projectscarpingvk;

import com.example.projectscarpingvk.service.telegram.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInit {

    private final TelegramBot bot;

    @Autowired
    public BotInit(TelegramBot bot){
        this.bot = bot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void start() throws TelegramApiException{
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try{
            telegramBotsApi.registerBot(bot);

        }catch (TelegramApiException ex){
            System.out.println(ex.getMessage());
        }
    }

}
