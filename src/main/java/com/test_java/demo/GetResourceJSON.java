package com.test_java.demo;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class GetResourceJSON {

    public static String mapResourceIndex(int i){
        switch (i){
            case 0:
                return "http://jsonvat.com/";
            default:
                return null;
        }

    }

    public static JsonNode getResourceJSON(int i){
        String url = mapResourceIndex(i);
        if(url==null) return null;

        try {
            HttpResponse<JsonNode> response = Unirest.get(url).asJson();
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
