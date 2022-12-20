package com.example.projectscarpingvk.vk;

import com.example.projectscarpingvk.config.AppConfig;
import com.example.projectscarpingvk.telegram.object.Group;
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
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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



    public GetResponse findUser(String domain) throws ApiException, ClientException, NotFoundException {
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

    public List<Group> getGroupsUser(int userId) throws ClientException, NullPointerException {
        List<Group> groupList = new ArrayList<>(50);
        JsonArray items = getGroupsItems(userId);
        for (int i = 0; i < items.size(); i++) {
            JsonObject temp = items.get(i).getAsJsonObject();
            groupList.add(getGroup(temp));
        }
        return groupList;
    }

    private JsonArray getGroupsItems(int userId) throws ClientException, NullPointerException {
        ClientResponse test = vk.groups().get(userActor).userId(userId).extended(true).fields(com.vk.api.sdk.objects.groups.Fields.MEMBERS_COUNT).executeAsRaw();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(test.getContent());
        if (jsonObject.getAsJsonObject("response") == null)
            throw new NullPointerException();

        return jsonObject.getAsJsonObject("response").getAsJsonArray("items");
    }

    private Group getGroup(JsonObject group){
        int id = group.get("id").getAsInt();
        String titleGroup = group.get("name").getAsString();
        String domainGroup = group.get("screen_name").getAsString();
        String countMembers = (group.get("members_count")!=null)? group.get("members_count").getAsString(): "неизвестно";

        return new Group(id, titleGroup, domainGroup, countMembers);
    }
    private List<Fields> fieldsUser(){
        return Stream.of(Fields.CITY, Fields.BDATE,Fields.ABOUT, Fields.BOOKS, Fields.CAREER,
                Fields.CONNECTIONS, Fields.CONTACTS, Fields.INTERESTS,Fields.MOVIES, Fields.MUSIC, Fields.OCCUPATION,
                Fields.PERSONAL, Fields.QUOTES, Fields.RELATIVES, Fields.RELATION, Fields.SEX, Fields.SITE, Fields.UNIVERSITIES, Fields.STATUS,
                Fields.VERIFIED, Fields.LAST_SEEN, Fields.PHOTO_400_ORIG, Fields.COUNTERS, Fields.ONLINE).toList();
    }

    private GetResponse userResponse(String domain) throws ApiException, ClientException, NotFoundException {
        List<GetResponse> responseUser = vk.users().get(userActor).userIds(domain)
                .fields(fieldsUser()).lang(Lang.RU).execute();

        if (responseUser.isEmpty()){
            throw new NotFoundException("Пользователь не найден");
        }

        return responseUser.get(0);
    }
}
