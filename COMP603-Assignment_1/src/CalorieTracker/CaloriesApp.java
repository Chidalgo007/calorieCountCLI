/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CalorieTracker;
import LoginOregister.LoginOregister;
import java.util.Scanner;

/**
 *
 * @author chg
 */
public class CaloriesApp {

    public CaloriesApp() {
       start();
    }
    private void start(){
         try(Scanner scan = new Scanner(System.in)){
        System.out.println("Do you want to Start? \"Y\"");
        if (scan.nextLine().equalsIgnoreCase("Y")) {
            firstLoopOptions();
        } else {
            System.exit(0);
        }
        }catch(Exception e){
            System.err.println("An error occured: "+e.getMessage());
        }
    }

    private void firstLoopOptions() {
        try(Scanner scan = new Scanner(System.in)){
        System.out.println("Do you want to:\n 1) Log In\n2) Regiter\n3) Just Try\n4) Exit.");
        String num = scan.nextLine();

            if (num.equalsIgnoreCase("ESC")) {
                System.exit(0);
            }
            if (num.equals("1")||num.equals("2")||num.equals("3")||num.equals("4")){
                selectOptionStart(num);
            }else{
                firstLoopOptions();
            }
        }catch(Exception e){
            System.err.println("An error occurred: "+e.getMessage());
        }
    }

    private void selectOptionStart(String num) {
        switch (num) {
            case "1":
                System.out.println("You selected \"log in\"");
                LoginOregister.logIn(); // call log in 
                break;
            case "2":
                System.out.println("You selected \"Register\"");
                LoginOregister.userRegister(); // call register
                break;
            case "3":
                System.out.println("You selected \"Just Try\"");
                UserInteractions tryOut = new UserInteractions("Try Out",-1); // go straight to calorieCount
                tryOut.startLoopOptions();
                break;
            case "4":
                System.exit(0);
                break;
            default:
                System.out.println("Sorry we don't have that option yet...try again");
                firstLoopOptions();
        }
    }
}
