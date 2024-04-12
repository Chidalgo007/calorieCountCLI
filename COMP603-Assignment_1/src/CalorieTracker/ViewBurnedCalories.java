/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CalorieTracker;

import FileIO.File_IO;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author chg
 */
public class ViewBurnedCalories extends File_IO {

    private static int sumWeeklyCal = 0;
    private static final Map<String, String> weeklyCalorie = new LinkedHashMap<>();

    public static void getWeekCalories() {
        sumWeeklyCal = 0;
        weeklyCalorie.clear();
        LocalDate currentDate = LocalDate.now();
        int daysMinus = currentDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (daysMinus == 0) {
            daysMinus = 7;
        }
        LocalDate oneWeekAgo = currentDate.minusDays(daysMinus);

        for (Map.Entry<String, String> entry : File_IO.getCaloriesBurned().entrySet()) {
            LocalDate itemDate = LocalDate.parse(entry.getKey());
            int cal = Integer.parseInt(entry.getValue());
            if (daysMinus == 7) {

                if (!itemDate.isBefore(oneWeekAgo) && itemDate.isBefore(currentDate)) {
                    sumWeeklyCal += cal;
                    weeklyCalorie.put(entry.getKey(), entry.getValue());
                }
            } else {
                if (!itemDate.isBefore(oneWeekAgo)) {
                    sumWeeklyCal += cal;
                    weeklyCalorie.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public static void printWeeklyCalories() {
        File_IO.readUserCalories();
        getWeekCalories();
        System.out.println("\nWeekly Calories\n-----------------------\n");
        if (!weeklyCalorie.isEmpty()) {
            for (Map.Entry<String, String> entry : weeklyCalorie.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        } else {
            System.out.println("Sorry you haven't entered any burned calories this week");
        }
        System.out.println("Total Calories burned this week " + getSumWeeklyCal() + "\n"
                + "-----------------------------------------------------------\n");
    }

    /**
     * @return the sumWeeklyCal
     */
    public static int getSumWeeklyCal() {
        return sumWeeklyCal;
    }
}
