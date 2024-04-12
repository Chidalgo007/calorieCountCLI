/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileIO;

import MyJDBC.MyJDBC;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chg
 */
public class File_IO {

    private final static String[] meal = new String[]{"breakfast", "lunch", "dinner", "snack", "totals"};
    private static final Map<String, String> caloriesBurned = new LinkedHashMap<>();
    private static String user;
    private static int userID;
    private static String pathCalories;

    public File_IO() {
        // when created by ViewBurnedCalories class
    }

    public File_IO(String userName, int userID) {
        // when created by User class
        File_IO.user = userName;
        File_IO.userID = userID;
        pathCalories = "./resources/" + userName + "_calories.txt";
        readUserCalories();
    }

    // daily calories file read
    public static void readUserCalories() {
        caloriesBurned.clear();
        // if file exist read it and get information
        File file = new File(pathCalories);
        if (!file.exists()) {
            return;
        }
            try ( BufferedReader br = new BufferedReader(new FileReader(pathCalories))) {
                String line;
                while ((line = br.readLine()) != null) {
                    //split the line into key and value
                    String[] parts = line.split(" ");
                    // ensure there are two parts(key and value)
                    if (parts.length == 2) {
                        String key = parts[0];
                        String value = parts[1];
                        caloriesBurned.put(key, value);
                    } else {
                        System.out.println("INVALID LINE: " + line);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(File_IO.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    // daily calories file write
    public static void userCalories(String date, String calorie, String user) {
        readUserCalories();
        try ( FileWriter fw = new FileWriter(pathCalories)) {
            if (!caloriesBurned.isEmpty()) {
                // if file exist after load the information in the map, check for repeat info
                caloriesBurned.put(date, calorie);
                for (Map.Entry<String, String> entry : getCaloriesBurned().entrySet()) {
                    fw.write(entry.getKey() + " " + entry.getValue() + "\n");
                }
                // if file doens't exist, add info and create it
            } else {
                fw.write(date + " " + calorie + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(File_IO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // write user registration
    public static void usersLoggins(Map<String, String> users) {

        String path = "./resources/" + users.get("username") + "_Registration.txt";
        try ( FileWriter fw = new FileWriter(path)) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                // write the user name information in the file
                fw.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(File_IO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ------------------------writing from DB----------------------------------------
    public static void writeFromDBtoFile() {
        ArrayList combinedJsonTables = new ArrayList();
        for (String st : meal) {
            if (!st.equalsIgnoreCase("TOTALS")) {
                combinedJsonTables.add(MyJDBC.writeJsonItems(st, getUserID()));
            } else {
                combinedJsonTables.add(MyJDBC.writeJsonMain(st, getUserID()));
            }
        }
        writeFile(combinedJsonTables);
    }

    public static void writeFile(ArrayList combinedJsonTables) {
        String path = "./resources/" + getUser() + ".json";
        try ( FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(combinedJsonTables.toString());
        } catch (IOException e) {
            System.out.println("Data NO exported to json file successfully!");
        }
    }
// ---------------------------getter and setters ---------------------------------------

    /**
     * @return the caloriesBurned
     */
    public static Map<String, String> getCaloriesBurned() {
        return caloriesBurned;
    }

    /**
     * @return the userID
     */
    public static int getUserID() {
        return userID;
    }

    /**
     * @param aUserID the userID to set
     */
    public static void setUserID(int aUserID) {
        userID = aUserID;
    }

    /**
     * @return the user
     */
    public static String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        File_IO.user = user;
    }

}
