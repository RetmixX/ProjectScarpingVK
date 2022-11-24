package com.example.projectscarpingvk.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Data
@Configuration
@PropertySource("application.properties")
@ComponentScan
public class AppConfig {

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.nameBot}")
    private String nameBot;

    @Value("${vk.token}")
    private String vkToken;

    @Value("${vk.app}")
    private String idApp;

}
