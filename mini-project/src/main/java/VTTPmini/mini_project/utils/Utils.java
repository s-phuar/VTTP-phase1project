package VTTPmini.mini_project.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Utils {
    
    //method to read jsonstr to jsonobject
    public static JsonObject toJson(String jsonStr) {
        JsonReader reader = Json.createReader(new StringReader(jsonStr));
        JsonObject jsonObj = reader.readObject();
        return jsonObj;   
    }
    
    //read price jsonobject and return double
    public static double jsonToDouble (JsonObject jsonObj){
        JsonObject resultsObj = jsonObj.getJsonArray("results").getJsonObject(0);
        return resultsObj.getInt("c");
    }

    //jsonstr can contain more than 1 htm url
    public static List<String> toList(String jsonStr) {
        JsonReader reader = Json.createReader(new StringReader(jsonStr));
        JsonObject jsonObj = reader.readObject();
        JsonArray jsonArray = jsonObj.getJsonArray("filings");

        List<String> quarterReports = new ArrayList<>();
        for(int i = 0; i< jsonArray.size(); i++){
            JsonObject temp = jsonArray.getJsonObject(i);
            if (temp.containsKey("linkToFilingDetails")) {
                String link = temp.getString("linkToFilingDetails");
                quarterReports.add(link);
            }
        }
        return quarterReports;   
    }

    public static JsonObject getCover(JsonObject reportJson){
        return reportJson.getJsonObject("CoverPage");
    }
    public static JsonObject getIncomeStatement(JsonObject reportJson){
        return reportJson.getJsonObject("StatementsOfIncome");
    }
    public static JsonObject getBalanceSheet(JsonObject reportJson){
        return reportJson.getJsonObject("BalanceSheets");
    }
    public static JsonObject getStatementSHEquity(JsonObject reportJson){
        return reportJson.getJsonObject("StatementsOfShareholdersEquity");
    }
    public static JsonObject getNetIncomeDetails(JsonObject reportJson){
        return reportJson.getJsonObject("NetIncomePerShareDetails");
    }
    //returns 0 if the key does not exist
    public static long getValue(JsonObject statement, String lineItem, int index){
        try{
            return Long.parseLong(statement.getJsonArray(lineItem).getJsonObject(index).getString("value"));
        }catch(NullPointerException e){
            return 0;
        }
    }

    public static long getTotalValue(JsonObject statement, String lineItem){
        try{
        JsonArray jsonArray = statement.getJsonArray(lineItem);
        for(int i = 0; i < jsonArray.size(); i++){
            JsonObject jsonObj = jsonArray.getJsonObject(i);

            if(!jsonObj.containsKey("segment")){
                return Long.parseLong(statement.getJsonArray(lineItem).getJsonObject(i).getString("value"));
            }
        }
        }catch(NullPointerException e){
            return 0;
        }
        return 0;
    }


    public static double getDecimalValue(JsonObject statement, String lineItem, int index){
        try{
        return Double.parseDouble(statement.getJsonArray(lineItem).getJsonObject(index).getString("value"));
        }catch(NullPointerException e){
            return 0;
        }
    }

    public static String dateToEpochStr(Date date) {
        if(date != null){
        long epochMilli = date.getTime();
        String epochMilliStr = String.valueOf(epochMilli);
        return epochMilliStr;
        }
        return "";
    }



}
