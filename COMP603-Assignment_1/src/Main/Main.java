/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main;

import CalorieTracker.CaloriesApp;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;

/**
 *
 * @author chg
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String message = "<html><body style='width:500px'>"
                    + "This is a Calories Tracking Program, you can add individual items or "
                    + "whole recipes and this program will tell you how many calories those"
                    + " items have. <br>"
                    + "<br>"
                    + "You can agroup them by \"Breakfas, Lunch, Diner or snacks\".<br>"
                    + "<br>"
                    + "The program will provided the daily calories consumtion as well the weekly "
                    + "total calories consumed.<br>"
                    + "<br>"
                    + "If you add the total calories burned each day, the program will "
                    + "tell you if you are in deficit (losing or gaining weight this week).<br>"
                    + "<br><hr>"
                    + "Please use the CUI (Command-line User Interface) below to interact with the program. "
                    + "to exit at any point just type \"ESC\" and press ENTER.<br>"
                    + "Thank you !!!";
            JOptionPane.showMessageDialog(null, message, "Calorie Tracking Program", JOptionPane.PLAIN_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "An unexpected error occured" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("An error occured: "+e.getMessage());
        }
        new CaloriesApp();
        JOptionPane.getRootFrame().dispose();
    }
}
