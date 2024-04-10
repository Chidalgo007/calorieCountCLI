/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package API;

import Main.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author chg
 */
public class fetchAPI {

//----------------------------------------------------------------------------------------
    // fetch a single ingredient for calories information
    public static int fetchAPISingleCalories(String ing) {
        try {
            // Set the API endpoint URL
            String apiUrl = "https://api.edamam.com/api/nutrition-data";
            String appId = "9de20323";
            String appKey = "d6641e573606aa0604bef1b575fe0c12";
            String ingr = ing;//"500gr%20fried%20chicken"; spaces with %20
            String wrongIng = ing.replaceAll("%20", " ");

            // Construct the request URL with query parameters
            String requestUrl = apiUrl + "?app_id=" + appId + "&app_key=" + appKey + "&ingr=" + ingr;

            //        System.out.println(requestUrl);
            // Send the POST request
            URL url = new URL(requestUrl);

            // Open connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the reuest method to GET
            connection.setRequestMethod("GET");

            // Set the accepted header
            connection.setRequestProperty("accept", "application/json");

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Check if the response code is HTTP OK (200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response;
                try ( // read the response from the connection input stream
                        BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    response = new StringBuilder();
                    while ((inputLine = read.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                //-------------------------------------------
                // Parse the JSON response
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj;
                try {
                    resultJsonObj = (JSONObject) parser.parse(response.toString());

                    // Get the total nutrients object
                    JSONObject totalNutrients = (JSONObject) resultJsonObj.get("totalNutrients");
                    if (totalNutrients != null) {

                        // Get the calories object
                        JSONObject caloriesObj = (JSONObject) totalNutrients.get("ENERC_KCAL");

                        if (caloriesObj != null) {

                            // Extract the calories value
                            Object calObj = caloriesObj.get("quantity");
                            if (calObj != null) {
                                //                   System.out.println("Calories: " + calories);
                                int calories = ((Number) calObj).intValue();
                                return calories;
                            } else {
                                System.out.println("Sorry I can find those calories of " + wrongIng + ", please refrase");
                                return -1;
                            }
                        } else {
                            System.out.println("Sorry I can compute " + wrongIng + " :(   try with another one");
                            return -1;
                        }
                    } else {
                        System.out.println("Sorry " + wrongIng + " doesn't show in my records, plese refrase");
                        return -1;
                    }
                    //-------------------------------------------
                } catch (ParseException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // if response code is not HTTP OK, print an error message
                System.out.println("Error; " + responseCode);
            }
            // close the connection
            connection.disconnect();

        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
        return -1;
    }
}
