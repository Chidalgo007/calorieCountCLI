/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MyJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 *
 * @author chg
 */
public class MyJDBC {

    private static final String DB_URL = "jdbc:mysql://awscaloriecount.cnmqgie6ahhg.us-east-1.rds.amazonaws.com:3306/AWSCalorieCount";
    private static final String DB_USERNAME = "PDC_Admin";
    private static final String DB_PASSWORD = "PDC_Admin";

    // FreeSQLdatabase.com
//    private static final String DB_URL = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6697968";
//    private static final String DB_USERNAME = "sql6697968";
//    private static final String DB_PASSWORD = "8hxEUIgqMC";
    // local DataBase
//    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/calorietracker";
//    private static final String DB_USERNAME = "root";
//    private static final String DB_PASSWORD = "@.MySQL";
    private static final String DB_USER_TABLE_NAME = "user";
    private static final String DB_ITEMS_CALORIES = "items_";
        
    // register new users
    public static boolean register(String username, String password, String name, String lastname, String email) {
        try {
            // check if user exist in the DB
            if (!checkUser(username, email)) {
                try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                          PreparedStatement insertUser = connection.prepareStatement(
                                "INSERT INTO " + DB_USER_TABLE_NAME + "(USERNAME, PASSWORD, NAME, LASTNAME, EMAIL)" + " VALUES(?,?,?,?,?)"
                        )) {
                    insertUser.setString(1, username);
                    insertUser.setString(2, password);
                    insertUser.setString(3, name);
                    insertUser.setString(4, lastname);
                    insertUser.setString(5, email);
                    // update DB
                    insertUser.executeUpdate();
                }
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    // check if the username or email exist in the user table before creating a new user
    private static boolean checkUser(String username, String email) {
        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                  PreparedStatement checkUserExist = connection.prepareStatement( //NOSONAR
                        " SELECT * FROM " + DB_USER_TABLE_NAME + " WHERE USERNAME = ? OR EMAIL = ?")) {

            checkUserExist.setString(1, username);
            checkUserExist.setString(2, email);

            try ( ResultSet resultSet = checkUserExist.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    return false;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    // get the user id
    public static int getUserId(String username, String password) {
        int userId = -1; // default to indicate user not found
        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                  PreparedStatement checkUserExist = connection.prepareStatement(
                        " SELECT * FROM " + DB_USER_TABLE_NAME + " WHERE BINARY USERNAME = ? AND BINARY PASSWORD = ?")) {

            checkUserExist.setString(1, username);
            checkUserExist.setString(2, password);

            ResultSet resultSet = checkUserExist.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("userID");
            }

        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userId;
    }

    // check for valid user
    public static boolean validLogin(String username, String password) {
        int userId = getUserId(username, password);
        return userId != -1;
    }

    // ------------  calories interaction -----------------------------------------------------
    // Enter calories for each item in their general items table
    public static void enterCalories(String tableName, int userID, Date date, String items, int calorie) {

        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                  PreparedStatement insertCalories = connection.prepareStatement(
                        "INSERT INTO " + DB_ITEMS_CALORIES + tableName + "(FK_USERID, DATE, ITEMS, CALORIES)" + " VALUES(?,?,?,?)"
                )) {

            //  Date sqlDate = Date.valueOf(date);
            insertCalories.setInt(1, userID);
            insertCalories.setDate(2, date);
            insertCalories.setString(3, items);
            insertCalories.setInt(4, calorie);

            insertCalories.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // call all the total calories update tables
    public static void sumDailyCalories() {
        String[] ALL_MEAL_TABLE = new String[]{"breakfast", "lunch", "dinner", "snack"};
        for (String s : ALL_MEAL_TABLE) {
            sumCaloriesByDateAndUser(s, s);
        }
        totalSumPerDay(ALL_MEAL_TABLE, "totals");
    }

    // sum all the calories by day and user and add them into the indivudual meal table
    private static void sumCaloriesByDateAndUser(String ITEM_TABLE, String MEAL_TABLE) {

        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            if (isTableEmpty(connection, ITEM_TABLE)) {
                return;
            }

            PreparedStatement sumIntoTotalPerMeal = connection.prepareStatement(
                    "INSERT INTO " + MEAL_TABLE + "(FK_USERID, DATE, TOTAL) "
                    + "SELECT FK_USERID, DATE, SUM(CALORIES) AS TOTAL "
                    + "FROM " + DB_ITEMS_CALORIES + ITEM_TABLE + " GROUP BY FK_USERID, DATE "
                    + "ON DUPLICATE KEY UPDATE TOTAL = VALUES(TOTAL)"
            );

            sumIntoTotalPerMeal.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void totalSumPerDay(String[] ALL_MEAL_TABLE, String total) {
        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            // check if all table are empty
            boolean allTableEmpty = true;
            for (String table : ALL_MEAL_TABLE) {
                if (!isTableEmpty(connection, table)) {
                    allTableEmpty = false;
                    break;
                }
            }
            if (allTableEmpty) {
                return;
            }
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("INSERT INTO ").append(total)
                    .append("(FK_USERID, DATE, TOTAL) ")
                    .append("SELECT FK_USERID, DATE, SUM(TOTAL) AS TOTAL FROM (");

            // apprend individual table queries
            for (int i = 0; i < ALL_MEAL_TABLE.length; i++) {
                queryBuilder.append("SELECT FK_USERID, DATE, TOTAL FROM ")
                        .append(ALL_MEAL_TABLE[i]);
                if (i < ALL_MEAL_TABLE.length - 1) {
                    queryBuilder.append(" UNION ALL ");
                }
            }
            queryBuilder.append(") AS combined GROUP BY FK_USERID, DATE ")
                    .append("ON DUPLICATE KEY UPDATE TOTAL = VALUES(TOTAL)");

            PreparedStatement totalCaloriesPerDay = connection.prepareStatement(queryBuilder.toString());
            totalCaloriesPerDay.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // check if the table is empty before sum the calories by day and user and add tem into the total meal
    private static boolean isTableEmpty(Connection connection, String ITEM_TABLE) {
        try {
            PreparedStatement checkIfEmpty = connection.prepareStatement("SELECT COUNT(*) FROM " + ITEM_TABLE);
            ResultSet result = checkIfEmpty.executeQuery();
            if (!result.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Retrive the information of the tables items_
    public static Map<String, Integer> retriveItems_details(String table, Date date, int userID) {
        Map<String, Integer> tableDetails = new LinkedHashMap<>();

        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                  PreparedStatement checkUserExist = connection.prepareStatement(
                        " SELECT * FROM " + DB_ITEMS_CALORIES + table + " WHERE FK_USERID = ? AND DATE = ?")) {

            checkUserExist.setInt(1, userID);
            checkUserExist.setDate(2, date);

            try ( ResultSet resultSet = checkUserExist.executeQuery()) {
                if (!resultSet.next()) {
                    tableDetails.put("No information", 0);
                } else {
                    do {
                        String item = resultSet.getNString("items") + " " + resultSet.getInt("calories") + "Kcal";
                        int id = resultSet.getInt("items_ID");
                        tableDetails.put(item, id);
                    } while (resultSet.next());
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tableDetails;
    }

    // Retrive the information of the tables mealType (breakfast, lunch, dinner, snack)
    public static Map<String, Integer> retrieveTotalKcal(String table, Date date, int userID) {
        Map<String, Integer> totalKcal = new LinkedHashMap<>();
        LocalDate localDate = date.toLocalDate();
        LocalDate localDateWeekAgo = localDate.minusDays(7);
        Date dateOneWeekAgo = Date.valueOf(localDateWeekAgo);

        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                //                PreparedStatement checkUserExist = connection.prepareStatement(
                //                "SELECT * FROM " + table + " WHERE FK_USERID = ? AND DATE = ?");  
                  PreparedStatement checkUserExistWithoutDate = connection.prepareStatement(
                        "SELECT * FROM " + table + " WHERE FK_USERID = ?")) {

            // Check if there are entries for dates one week ago
//            checkUserExist.setInt(1, userID);
//            checkUserExist.setDate(2, dateOneWeekAgo);
//            try ( ResultSet resultSet = checkUserExist.executeQuery()) {
//                System.out.println("before while userExist");
//                while (resultSet.next()) {
//                System.out.println("inside while userExist");
//                    Date mealDate = resultSet.getDate("date");
//                    int id = resultSet.getInt("total");
//                    totalKcal.put(mealDate.toString(), id);
//                System.out.println("userExist date: "+mealDate+" id: "+id);
//                }
//            }
//             If there are no entries, retrieve all entries for the specified user without filtering by date
            if (totalKcal.isEmpty()) {
                checkUserExistWithoutDate.setInt(1, userID);
                try ( ResultSet resultSetWithoutDate = checkUserExistWithoutDate.executeQuery()) {
                    if (resultSetWithoutDate.isBeforeFirst()) {
                        while (resultSetWithoutDate.next()) {
                            Date mealDate = resultSetWithoutDate.getDate("date");
                            int total = resultSetWithoutDate.getInt("total");
                            totalKcal.put(mealDate.toString(), total);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

        return totalKcal;
    }

    public static void removeLineForEditedInformation(String table, int userID, int itemsID) {

        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                  PreparedStatement deleteRow = connection.prepareStatement("DELETE FROM " + DB_ITEMS_CALORIES + table
                        + " WHERE ITEMS_ID = ? AND FK_USERID = ?")) {

            deleteRow.setInt(1, itemsID);
            deleteRow.setInt(2, userID);

            deleteRow.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ------------------------------ FILE I/O from DB----------------------------------
    public static JsonObject writeJsonItems(String table, int userID) {

        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                  PreparedStatement statement = connection.prepareStatement( //NOSONAR
                        "SELECT * FROM " + DB_ITEMS_CALORIES + table + " WHERE FK_USERID = ? ORDER BY DATE")) {
            statement.setInt(1, userID);
            try ( ResultSet resultSet = statement.executeQuery()) {

                Map<String, JsonArray> groupItems = new LinkedHashMap<>();

                while (resultSet.next()) {

                    String date = resultSet.getString("date");

                    JsonObject items = new JsonObject();
                    items.addProperty("items_ID", resultSet.getString("items_ID"));
                    items.addProperty("FK_userID", resultSet.getString("FK_userID"));
                    items.addProperty("date", date);
                    items.addProperty("items", resultSet.getString("items"));
                    items.addProperty("calories", resultSet.getString("calories"));

                    JsonArray dateArray = groupItems.computeIfAbsent(date, k -> new JsonArray());
                    dateArray.add(items);
                }
                // convert to aajson structure
                JsonObject jsonResult = new JsonObject();
                JsonObject jsonTable = new JsonObject();
                for (Map.Entry<String, JsonArray> entry : groupItems.entrySet()) {
                    jsonTable.add(entry.getKey(), entry.getValue());
                }
                jsonResult.add(table, jsonTable);
                // Write JSON data to a file
                return jsonResult;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static JsonObject writeJsonMain(String table, int userID) {

        try ( Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //NOSONAR
                  PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM " + table + " WHERE FK_USERID = ? ORDER BY DATE")) {
            statement.setInt(1, userID);

            try ( ResultSet resultSet = statement.executeQuery()) {
                // to hold table, date, items ...
                Map<String, JsonArray> groupTotal = new LinkedHashMap<>();

                while (resultSet.next()) {
                    String date = resultSet.getString("date");

                    JsonObject items = new JsonObject();
                    items.addProperty(table + "ID", resultSet.getString(table + "ID"));
                    items.addProperty("userID", resultSet.getString("FK_userID"));
                    items.addProperty("date", date);
                    items.addProperty("calories", resultSet.getString("total"));

                    JsonArray dateMap = groupTotal.computeIfAbsent(date, k -> new JsonArray());
                    dateMap.add(items);
                }
                // convert to json structure
                JsonObject jsonResults = new JsonObject();
                JsonObject tableObj = new JsonObject();
                for (Map.Entry<String, JsonArray> tableEntry : groupTotal.entrySet()) {
                    tableObj.add(tableEntry.getKey(), tableEntry.getValue());
                }
                jsonResults.add(table, tableObj);
                // Write JSON data to a file
                return jsonResults;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
