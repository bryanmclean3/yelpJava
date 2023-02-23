package Assignment1;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReadJson implements ActionListener {

    static SpringLayout layout = new SpringLayout();
    static JFrame frame = new JFrame();
    static JPanel panel = new JPanel();
    static JLabel label = new JLabel("Enter Restaurant Name: ");
    static JTextField text = new JTextField(50);
    static JButton button = new JButton("Enter");
    static String input;
    static JLabel rec1 = new JLabel("Recommendation 1: ");
    static JLabel rec2 = new JLabel("Recommendation 2: ");
    static JLabel label1 = new JLabel("");
    static JLabel label2 = new JLabel("");

        public static void main(String[] args) throws IOException, ParseException {

            //initialize JSONParser and create an array of the json objects

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            button.setSize(80, 25);
            frame.setSize(1000, 400);
            frame.add(panel);
            frame.setVisible(true);
            panel.setLayout(layout);
            panel.add(label);
            panel.add(text);
            panel.add(button);

            // Put constraint on components
            layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, panel);
            layout.putConstraint(SpringLayout.WEST, text, 5, SpringLayout.EAST, label);
            layout.putConstraint(SpringLayout.NORTH, text, 5, SpringLayout.NORTH, panel);
            layout.putConstraint(SpringLayout.WEST, button, 5, SpringLayout.EAST, text);
            layout.putConstraint(SpringLayout.NORTH, button, 5, SpringLayout.NORTH, panel);

            panel.add(rec1);
            panel.add(rec2);

            layout.putConstraint(SpringLayout.WEST, rec1, 5, SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, rec1, 50, SpringLayout.NORTH, label);
            layout.putConstraint(SpringLayout.WEST, rec2, 5, SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, rec2, 50, SpringLayout.NORTH, rec1);

            button.addActionListener(new ReadJson());

            panel.add(label1);
            panel.add(label2);

            layout.putConstraint(SpringLayout.WEST, label1, 120, SpringLayout.WEST, rec1);
            layout.putConstraint(SpringLayout.NORTH, label1, 50, SpringLayout.NORTH, label);
            layout.putConstraint(SpringLayout.WEST, label2, 120, SpringLayout.WEST, rec2);
            layout.putConstraint(SpringLayout.NORTH, label2, 50, SpringLayout.NORTH, label1);

        }

    @Override
    public void actionPerformed(ActionEvent e) {
        input = text.getText();

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(new FileReader("src\\Assignment1\\file1.json"));
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }


        Gson gson = new Gson();

        //initialize a hash table that stores the data that is to be parsed
        HT hashTableOne = new HT();        //hashtable that maps name to object
        HT hashTableTwo = new HT();      //hashtable that maps to tf-idf value

        //loop through the array of json objects
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            Business business = gson.fromJson(jsonObject.toJSONString(), Business.class);
            hashTableOne.add(business.name);
        }
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            Business business = gson.fromJson(jsonObject.toJSONString(), Business.class);
            hashTableOne.setValue(business.name, business.categories);
            hashTableTwo.add(business.categories);
        }

        String userInput = input;
        Object gsonObj = hashTableOne.getValue(userInput);
        hashTableOne.remove(input);
        hashTableTwo.remove(gsonObj);

        if (gsonObj == null) {
            label1.setText("Invalid Business!");
            label2.setText("Invalid Business!");

        }
        tfIdfCalculator calculator = new tfIdfCalculator();
        List<List<String>> documents = new ArrayList<>();
        List<List<String>> docs = new ArrayList<>();

        String[] queryString = gsonObj.toString().split(", ");
        List<String> inputList = Arrays.asList(queryString);  //categories of input business

        List<String> nextList = null;
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            Business business = gson.fromJson(jsonObject.toJSONString(), Business.class);
            nextList = Arrays.asList(business.categories.split(", "));
            documents.add(nextList); //add all business categories

        }

        for (int j = 0; j < documents.size(); j++) {
            double k = 0;
            docs.add(documents.get(j));   //add one businesses categories
            for (int i = 0; i < inputList.size(); i++) {
                double tfIdf = calculator.tfIdf(inputList, docs, inputList.get(i));
                k = k + tfIdf;
            }
            String doc = String.valueOf(documents.get(j));
            String category = doc.substring(1, doc.length() - 1);  //get rid of brackets
            hashTableTwo.setValue(category, k);
            docs.remove(documents.get(j));
        }
    }
}
