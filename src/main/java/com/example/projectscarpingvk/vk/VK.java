package com.example.projectscarpingvk.vk;

import com.example.projectscarpingvk.config.AppConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class VK {

    private AppConfig appConfig;

    private UserActor userActor;
    private VkApiClient vk;

    @Autowired
    public VK(AppConfig appConfig){
        this.appConfig = appConfig;
        TransportClient tc = new HttpTransportClient();
        vk = new VkApiClient(tc);
        userActor = new UserActor(Integer.getInteger(appConfig.getIdApp()), appConfig.getVkToken());
    }



    public GetResponse findUser(String domain)throws ApiException, ClientException{
        return userResponse(domain);
    }

    public JsonArray takePhoto(int userId, int i){
        JsonArray items = null;
        try {
            ClientResponse photoList1 = vk.photos().getAll(userActor).ownerId(userId).photoSizes(true).offset(i * 20).executeAsRaw();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(photoList1.getContent());
            items = jsonObject.getAsJsonObject("response").getAsJsonArray("items");
        } catch (ClientException clientException) {
            System.out.println(clientException.getMessage());
        }

        return items;
    }

    private List<Fields> fieldsUser(){
        return Stream.of(Fields.CITY, Fields.BDATE, Fields.SCHOOLS, Fields.ABOUT, Fields.BOOKS, Fields.CAREER,
                Fields.CONNECTIONS, Fields.CONTACTS, Fields.INTERESTS, Fields.MILITARY, Fields.MOVIES, Fields.MUSIC, Fields.OCCUPATION,
                Fields.PERSONAL, Fields.QUOTES, Fields.RELATIVES, Fields.RELATION, Fields.SEX, Fields.SITE, Fields.UNIVERSITIES, Fields.STATUS,
                Fields.VERIFIED, Fields.LAST_SEEN, Fields.PHOTO_400_ORIG, Fields.COUNTERS, Fields.ONLINE).toList();
    }

    private GetResponse userResponse(String domain) throws ApiException, ClientException {
        List<GetResponse> responseUser = vk.users().get(userActor).userIds(domain)
                .fields(fieldsUser()).lang(Lang.RU).execute();

        return responseUser.get(0);
    }
}
