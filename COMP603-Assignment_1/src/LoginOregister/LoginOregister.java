/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LoginOregister;

import MyJDBC.MyJDBC;
import CalorieTracker.UserInteractions;
import java.util.Scanner;

/**
 *
 * @author chg
 */
public class LoginOregister {

    public static void logIn() {
        try ( Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.println("This is the User Log In");
                System.out.println("-----------------------------");
                System.out.println("Enter your UserName: ");

                String username = scan.nextLine();
                if (username.equalsIgnoreCase("ESC")) {
                    System.exit(0);
                }
                System.out.println("Enter your Password: ");

                String password = scan.nextLine();
                if (password.equalsIgnoreCase("ESC")) {
                    System.exit(0);
                }
                int userId = MyJDBC.getUserId(username, password);
                if (userId != -1) {
//                    System.out.println("Welcome " + username);
//                    System.out.println("login id: " + userId);
                    new UserInteractions(username, userId);
                    break;
                } else {
                    System.out.println("Username and Password don't match");
                    System.out.println("Do you want to Register? \"Y\" or \"any\" to try again (ESC to exit)");

                    String response = scan.nextLine();
                    if (response.equalsIgnoreCase("Y")) {
                        userRegister();
                        break;
                    } else if (response.equalsIgnoreCase("ESC")) {
                        System.exit(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public static void userRegister() {
        try ( Scanner scan = new Scanner(System.in)) {
            System.out.println("This is the User Registration");
            System.out.println("-----------------------------");
            System.out.println("Enter an UserName: ");
            String username = scan.nextLine();
            if (username.equalsIgnoreCase("ESC")) {
                System.exit(0);
            }
            System.out.println("Enter your Password min 6 character length: ");
            String password = scan.nextLine();
            if (password.equalsIgnoreCase("ESC")) {
                System.exit(0);
            }
            System.out.println("Re-enter your Password: ");
            String rePassword = scan.nextLine();
            if (rePassword.equalsIgnoreCase("ESC")) {
                System.exit(0);
            }
            System.out.println("Enter your Name: ");
            String name = scan.nextLine();
            if (name.equalsIgnoreCase("ESC")) {
                System.exit(0);
            }
            System.out.println("Enter your Last Name: ");
            String lastName = scan.nextLine();
            if (lastName.equalsIgnoreCase("ESC")) {
                System.exit(0);
            }
            System.out.println("Enter your email: ");
            String email = scan.nextLine();
            if (email.equalsIgnoreCase("ESC")) {
                System.exit(0);
            }
            if (validLoginInput(username, password, rePassword, name, lastName, email)) {
                if (MyJDBC.register(username, password, name, lastName, email)) {
                    System.out.println("registration successfully!");
                    System.out.println("==========================");
                    logIn();
                } else {
                    System.out.println("Username already taken try again!");
                    userRegister();
                }

            } else {
                userRegister();
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static boolean validLoginInput(String username, String password, String rePassword, String name, String lastname, String email) {
        if (username.length() == 0 || password.length() == 0 || name.length() == 0 || lastname.length() == 0 || email.length() == 0) {
            System.out.println("Please complete all Inputs");
            return false;
        }
        if (password.length() < 6) {
            System.out.println("Password have to be min 6 character length");
            return false;
        }
        if (!password.equals(rePassword)) {
            System.out.println("Password's don't match");
            return false;
        }
        return true;
    }
}
