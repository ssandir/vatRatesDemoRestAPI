package com.test_java.demo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.JsonNode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RestController
public class Control {


    @RequestMapping(value = "/query", produces = "application/json")
    public String serveVat(@RequestParam(name = "s", required = false, defaultValue = "0") String source,
                           @RequestParam("f") String function,
                           @RequestParam(name = "p", required = false, defaultValue = "3") String parameter,
                           HttpServletResponse response){

        response.setContentType("application/json");

        JsonObject error = new JsonObject();
        JsonObject success = new JsonObject();
        error.addProperty("status","error");
        success.addProperty("status","success");

        //get resource, currently by http request, can be change to host the JSON on server
        //best option make JSON database and query it
        // (note - some error/type checking missing)
        JsonNode jsonNode = GetResourceJSON.getResourceJSON(Integer.parseInt(source));
        if(jsonNode==null) return error.toString();

        //use the requested function on the jsonObject
        JsonArray result = Functions.process(jsonNode,function,Integer.parseInt(parameter));
        if(result==null) return error.toString();

        success.add("result", result);
        return success.toString();
    }
}
