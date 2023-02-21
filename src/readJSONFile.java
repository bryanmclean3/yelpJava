import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class readJSONFile {

    public static void main(String[] args) throws IOException, ParseException {
        //initialize JSONParser and create an array of the json objects
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("file1.json"));

        //initialize gson
        Gson gson = new Gson();

        //initialize a hash table that stores the data that is to be parsed
        HT hashTableOne = new HT();        //hashtable that maps name to object
        HT hashTableTwo = new HT();      //hashtable that maps GSON to tf-idf value

        //loop through the array of json objects
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            Business business = gson.fromJson(jsonObject.toJSONString(), Business.class);

            hashTableOne.add(business.name);
            hashTableOne.setValue("St Honore Pastries", business.categories);
        }

        //user inputs for example

        hashTableOne.printAll();

    }

}
