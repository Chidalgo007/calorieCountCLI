/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CalorieTracker;

import FileIO.File_IO;
import MyJDBC.MyJDBC;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chg
 *this class manage the insertion of information into the DB
 */
public class EnterCalories {

    private final LinkedHashMap<String, Integer> items;
    private final int userID;

    public EnterCalories(int userId) {
        items = new LinkedHashMap<>();
        userID = userId;
    }

    /**
     * @param items the items to set
     */
    public void setItems(String items, int calories) {
        this.items.put(items, calories);
    }

    // print the items added for insertion into the DB
    public void printItems() {
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String item = entry.getKey();
            int calories = entry.getValue();
            if (calories > 0) {
                System.out.printf("%s: %d kcal%n", item, calories);
            }
        }
    }

    // This method insert each food witht the calories into 
    // the correspondient table for the selected user
    public void insertCaloriesIntoDB(String table, Date date) {

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String item = entry.getKey();
            int calories = entry.getValue();
            if (calories > 0) {
                MyJDBC.enterCalories(table, userID, date, item, calories);
                //  System.out.printf("%s: %d kcal%n", item, calories);
            }
        }
        items.clear();
        System.out.println("Sorry for the delay, I am writing into a file...");
        File_IO.writeFromDBtoFile();
    }

    public void removeLineForEditedInformation(String table,int itemsID ){
        MyJDBC.removeLineForEditedInformation(table, userID, itemsID);
    }
  
}
