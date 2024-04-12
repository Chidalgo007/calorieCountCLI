/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CalorieTracker;

import MyJDBC.MyJDBC;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 *
 * @author chg
 */
public class RetriveInformation {

    private int weeklyCal, dayTotalMeal;
    private String str1, str2;

    private Map<String, Integer> weekCalories = caloriesDetails = new LinkedHashMap<>();
    private Map<String, Integer> caloriesDetails = caloriesDetails = new LinkedHashMap<>();
    private final String[] meal = new String[]{"breakfast", "lunch", "dinner", "snack"};
    private StringBuilder weekly;
    private final UserInteractions userInteractions;

    public RetriveInformation(UserInteractions userInteraction) {
        this.userInteractions = userInteraction;
    }

    public void callCalorieRetrive(Date date, int userID) {
        weekly = new StringBuilder("Weekly Kcal Information:\n");
        for (String s : meal) {
            retrieveTotalKcal(s, date, userID);
            retriveItems_details(s, date, userID);
        }
        retrieveTotalKcal("totals", date, userID);
        System.out.println("-----------------------------------------------"
                + "-----------------------------------------------");
        System.out.println(weekly);
        System.out.println("===============================\n");
        calories();
        System.out.println("-----------------------------------------------"
                + "-----------------------------------------------");
        userInteractions.WantToDoMore();
    }

    private void calories() {
        ViewBurnedCalories.getWeekCalories();
        int calorieBurned = ViewBurnedCalories.getSumWeeklyCal();
        System.out.println("Your Calories Burned this week " + calorieBurned);
        System.out.println("===============================");
        int diff = calorieBurned - weeklyCal;
        if (diff > 0) {
            float weight = diff / 7000.0f;
            System.out.printf("Your have a calorie deficit of %d calories this week.%n", diff);
            System.out.printf("at this rhythm you should lose %.2f kg of weight this week%n", weight);
            } else if(diff < 0) {
                float weight = -diff / 7000.0f;
            System.out.printf("Your have a calorie surplus of %d calories this week.%n", diff);
            System.out.printf("at this rhythm you should gain %.2f kg of weight this week%n", weight);           
            } 
    }

    private void retriveItems_details(String table, Date date, int userID) {
        getCaloriesDetails().clear();
        caloriesDetails = MyJDBC.retriveItems_details(table, date, userID);
        printCalories(getCaloriesDetails());
    }

    private void retrieveTotalKcal(String table, Date date, int userID) {
        weekCalories.clear();
        weekCalories = MyJDBC.retrieveTotalKcal(table, date, userID);
        getWeeklyCalValue(date, weekCalories, table);
        if (table.equalsIgnoreCase("totals")) {
            System.out.println("\n----------------------------------------------\n"
                    + "Total for today " + dayTotalMeal + " Kcal");
            str1 = "";
        } else {
            System.out.println("\nTotal " + table + " : " + dayTotalMeal + " Kcal\n"
                    + "-----------------------------------------");
            str1 = "   " + table + " ";
        }
        appendWeeklyCal();
    }

    private void appendWeeklyCal() {
        weekly.append(str1);
        weekly.append(str2);
    }

    private void printCalories(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String item = entry.getKey();
            System.out.println("\t" + item);
        }
    }

    // get total of the week from the closed Monday to one day before today
    private void getWeeklyCalValue(Date date, Map<String, Integer> map, String table) {
        weeklyCal = 0;
        dayTotalMeal = 0;
        String firstDate = "";
        String endDate="";
        LocalDate currentDate = date.toLocalDate();
        int daysMinus = currentDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (daysMinus == 0) {
            daysMinus = 7;
        }
        LocalDate oneWeekAgo = currentDate.minusDays(daysMinus);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            LocalDate itemDate = LocalDate.parse(entry.getKey());
            int calTotal = entry.getValue();

            if (daysMinus == 7) {
                if (!itemDate.isBefore(oneWeekAgo) && itemDate.isBefore(currentDate)) {
                    weeklyCal += calTotal;
                    if (firstDate.isEmpty()) {
                        firstDate = itemDate.toString();
                    }
                }
                endDate = currentDate.minusDays(1).toString();
            } else {
                if (!itemDate.isBefore(oneWeekAgo)) {
                    weeklyCal += calTotal;
                    if (firstDate.isEmpty()) {
                        firstDate = itemDate.toString();
                    }
                }
                endDate = currentDate.toString();
            }
            if (itemDate.isEqual(currentDate)) {
                dayTotalMeal = calTotal;
            }
        }
        /*
        this store the weekly information from the monday proximo at the date requested
        if the date requested is monday, it should shown monday to sunday of the previous week
         */
        

        if (table.equalsIgnoreCase("totals")) {
            str2 = "===============================\n\t\tTotal for this week " + weeklyCal + " Kcal";
        } else {
            str2 = "\tfrom " + firstDate + " to " + endDate + " : " + weeklyCal + " Kcal\n";
        }

    }

    // retreive the information for the user to select and delete
    public void editCalories(Date date, int userID) {
        getCaloriesDetails().clear();
        for (String s : meal) {
            caloriesDetails = MyJDBC.retriveItems_details(s, date, userID);
            System.out.printf("\n%s Calories\n----------------------\n", s);
            for (Map.Entry<String, Integer> entry : getCaloriesDetails().entrySet()) {
                System.out.println(entry.getValue() + ")  " + entry.getKey());
            }
        }
        userInteractions.selectItemsToEdit();
    }

    /**
     * @return the caloriesDetails
     */
    public Map<String, Integer> getCaloriesDetails() {
        return caloriesDetails;
    }

}
