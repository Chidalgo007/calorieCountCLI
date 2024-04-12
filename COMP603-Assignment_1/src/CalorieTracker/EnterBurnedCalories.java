/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CalorieTracker;

import FileIO.File_IO;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author chg
 */
public class EnterBurnedCalories {

    private final UserInteractions userInteraction;

    public EnterBurnedCalories(UserInteractions userInteractions) {
        this.userInteraction = userInteractions;
    }

    public void enterBurnedCalories(Scanner sc) {
        Date date = userInteraction.getDate();
        System.out.println("Enter burned calories for " + date);
        while (true) {
            String cal = sc.nextLine();
            if (cal.equalsIgnoreCase("ESC")) {
                userInteraction.systemClose();
            } else if (cal.matches("\\d+")) {
                if (cal.length() < 5) {
                    File_IO.userCalories(date.toString(), cal, userInteraction.getUser().getUserName());
                    break;
                } else {
                    System.out.println("Your calories seems to high, please enter again.");
                }
            } else {
                System.out.println("Please only enter numbers.");
            }
        }
        System.out.println("Whan to enter more burned calories? \"Y\" or any letter to continue");
        String moreCal = sc.nextLine();
        if (moreCal.equalsIgnoreCase("ESC")) {
            userInteraction.systemClose();
        } else if (moreCal.equalsIgnoreCase("Y")) {
            System.out.println("Please select the date to enter the calories \n1) Today\n2) Other date");
            userInteraction.selectDate();
            enterBurnedCalories(sc);
        }
        userInteraction.WantToDoMore();
    }

}
