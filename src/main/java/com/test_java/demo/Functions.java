package com.test_java.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.JsonNode;

import java.util.ArrayList;

public class Functions {
    public static JsonArray process(JsonNode jsonNode, String function, int parameter) {
        switch (function) {
            case "lv":
                return peakVatCountries(jsonNode, parameter, false);

            case "hv":
                return peakVatCountries(jsonNode, parameter, true);

            default:
                return null;
        }
    }

    //returns jsonarray of #entries country names from jsonNode
    // that have the highest or lowest VAT rates (depending on highToLow)
    private static JsonArray peakVatCountries(JsonNode jsonNode, int entries, boolean highToLow){

        ArrayList<String> countryList = JsonPath.read(jsonNode.toString(), "$.rates[*].name");

        //not optimized, use for small entries << number of countries or small number of countries
        //i guess the number of countries in the world isnt that much so it works either way
        //other options include sorting the list or using a heap
        ArrayList<String> peakNames = new ArrayList<>();
        ArrayList<Integer> peakValues = new ArrayList<>(); //could use new class for it

        for (String country:countryList) {
            //all periods for this country
            ArrayList<String> periodsList = JsonPath.read(jsonNode.toString(),
                    "$.rates[?(@.name == \""+country+"\")].periods[*].effective_from");

            //extract the latest period
            String maxPeriod = periodsList.get(0);
            for(int i = 1; i < periodsList.size(); ++i){
                if(maxPeriod.compareTo(periodsList.get(i))<0) maxPeriod = periodsList.get(i);
            }

            //get vat rate for that period
            ArrayList<Integer> countryVatRateList = JsonPath.read(jsonNode.toString(),
                    "$.rates[?(@.name == \""+country+"\")].periods[?(@.effective_from == \""
                            + maxPeriod + "\")].rates.standard");
            Integer countryVatRate = countryVatRateList.get(0);

            //keep only entries number of countries with the highest/lowest rates
            if(peakNames.size() < entries){
                peakNames.add(country);
                peakValues.add(countryVatRate);
            }
            else{
                int peakElement = 0;
                for(int i=1;i<peakNames.size();++i){
                    if(highToLow && peakValues.get(i) < peakValues.get(peakElement)){
                        peakElement = i;
                    }
                    else if(!highToLow && peakValues.get(i) > peakValues.get(peakElement)){
                        peakElement = i;
                    }
                }

                if(highToLow && peakValues.get(peakElement) < countryVatRate){
                    peakNames.set(peakElement, country);
                    peakValues.set(peakElement, countryVatRate);
                }
                else if(!highToLow && peakValues.get(peakElement) > countryVatRate){
                    peakNames.set(peakElement, country);
                    peakValues.set(peakElement, countryVatRate);
                }
            }

        }


        //make JSON string out of peak Pairs
        Gson gsonBuilder = new GsonBuilder().create();
        JsonParser jsonParser = new JsonParser();
        String jsonString = gsonBuilder.toJson(peakNames);
        return jsonParser.parse(jsonString).getAsJsonArray();
    }

}
