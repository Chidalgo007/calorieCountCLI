/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CalorieTracker;

import API.fetchAPI;
import LoginOregister.LoginOregister;
import MyJDBC.MyJDBC;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author chg
 */
public class UserInteractions {

    private String mealType;
    private final Users user;
    private Date date;
    private final Scanner scan = new Scanner(System.in);
    private RetriveInformation getInfo;
    private EnterCalories enterInfo;

    public UserInteractions() { // constructor for try out
        user = new Users("try out", -1);
        startLoopOptions();

    }

    public UserInteractions(String userName, int userID) { // constructor for log in
        user = new Users(userName, userID);
        getInfo = new RetriveInformation(this);
        enterInfo = new EnterCalories(user.getUserId());
        whatDoYouWantToDo();
    }

    private void startLoopOptions() {
        System.out.println("Select where to enter food calories :\n 1) Breakfast\n2) Lunch\n3) Dinner\n4) Snack");

        String input = scan.nextLine().trim();

        if (input.equalsIgnoreCase("ESC")) {
            systemClose();
        }

        selectOptionCaloriesCount(input);
    }

    private void selectOptionCaloriesCount(String num) {
        switch (num) {
            case "1":
                mealType = "breakfast";
                break;
            case "2":
                mealType = "lunch";
                break;
            case "3":
                mealType = "dinner";
                break;
            case "4":
                mealType = "snack";
                break;
            case "5":
                systemClose();
                break;
            default:
                System.out.println("Sorry we don't have that option yet...try again");
                startLoopOptions();
                break;
        }
        enterCalories();
    }

    /*
     * this method receive the items list from the user either as list separated by , or 
     * as list line per line and fix them to by call them in the API.
     * the List receive items until the user either type "done" or "esc"
     * if type ESC the program finish.
     * if type DONE the program print a list of items found and added to the data base of the user
     */
    private void enterCalories() {
        LinkedList<String> foodList = new LinkedList<>();
        System.out.println("Enter items (e.g 500g fried Chicken), and press ENTER (type \"DONE\" to finish or ESC to exit): ");
        while (scan.hasNext()) {
            String items = scan.nextLine();
            if (items.equalsIgnoreCase("done")) {
                break;
            } else if (items.equalsIgnoreCase("ESC")) {
                systemClose();
            }
            if (items.contains(",")) {
                String[] array = items.split(",");
                for (String s : array) {
                    if (s.trim().length() > 0) { // make sure after split only add real items no empty strings
                        foodList.add(s.trim());
                    }
                }
            } else {
                foodList.add(items.trim());
            }
        }
        for (String i : foodList) {
            System.out.println(i);
        }
        System.out.println("Please double check if this items correct? \"Y\\N\"");
        String checkInput = scan.nextLine();
        if (checkInput.equalsIgnoreCase("esc")) {
            systemClose();
        } else if (checkInput.equalsIgnoreCase("Y")) {
            enterCaloriesToMyJDBC(foodList);
        } else if (checkInput.equalsIgnoreCase("N")) {
            enterCalories();
        }
    }

    private void enterCaloriesToMyJDBC(LinkedList<String> foodList) {
        // retrive the items calories from the API and add them to the Map for each user
        for (String i : foodList) {
            String ing = i.replace(" ", "%20");
            int calories = fetchAPI.fetchAPISingleCalories(ing);
            if (user.getUserId() != -1) {
                enterInfo.setItems(i, calories);
            } else {
                System.out.println(i + " : " + calories);
            }
        }
        // print a list of the items added
        String input;
        if (user.getUserId() != -1) {
            enterInfo.insertCaloriesIntoDB(mealType, date);
            enterInfo.printItems();
            System.out.println("Do you want to add more items? \"Y\" or any to continue");
            input = scan.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                startLoopOptions();
            } else if (input.equalsIgnoreCase("ESC")) {
                systemClose();
            }
            WantToDoMore();
        } else {
            System.out.println("Do you want to Register? \"Y\" or any to Exit");
            input = scan.nextLine();
            if (input.equalsIgnoreCase("Y")) {
                LoginOregister.userRegister(); // call register
            } else if (input.equalsIgnoreCase("ESC")) {
                systemClose();
            } else {
                systemClose();
            }
        }
    }

    // ------------------- user interactions ----------------------------------
    public void WantToDoMore() {
        String input;
        System.out.println("Do you want to do something else? \"Y\" or any to Exit");
        input = scan.nextLine();
        if (input.equalsIgnoreCase("Y")) {
            whatDoYouWantToDo(); // restart options
        } else if (input.equalsIgnoreCase("ESC")) {
            systemClose();
        } else {
            systemClose();
        }
    }

    private void whatDoYouWantToDo() {
        System.out.println("What do you want to do today \n"
                + "---------------------------------------");
        System.out.println("1) Enter Food Calories \n2) View your current Calories"
                + "\n3) Edit current calories \n4) Exit.");

        String input = scan.nextLine();

        if (input.equalsIgnoreCase("ESC")) {
            systemClose();
        }
        selectOption(input);

    }

    private void selectOption(String n) {
        switch (n) {
            case "1":
                System.out.println("You selected \"Enter Food Calories\"");
                System.out.println("Please select date to entrer food calories : \n1) Today\n2) Other date");

                selectDate(); // select day to enter information
                startLoopOptions();
                break;
            case "2":
                System.out.println("You selected \"View your Calories\"");
                System.out.println("Please select which date you want to view your calories : \n1) Today\n2) Other date");

                selectDate(); // select day to enter information
                System.out.printf("\n==================\n"
                        + "This are the calories for %s \n", date.toString());
                //   MyJDBC.sumDailyCalories();
                MyJDBC.sumDailyCalories();
                getInfo.callCalorieRetrive(date, user.getUserId());

                break;
            case "3":
                System.out.println("You selected \"Edit calorie Entered\"");
                System.out.println("Please select which date you want to edit your calories : \n1) Today\n2) Other date");

                selectDate(); // select day to enter information
                getInfo.editCalories(date, user.getUserId());

                break;
//            case "4":
//                System.out.println("You selected \"Enter calorie Burned\"");
//                System.out.println("Please select which date you want to enter calories burned : \n1) Today\n2) Other date");
//
//                selectDate(); // select day to enter information
//                //enter calories burned
//                break;
            case "4":
                System.exit(0);
                break;
            default:
                System.out.println("Sorry we don't have that option yet...try again");
                whatDoYouWantToDo();
        }
    }

    private void selectDate() {

        while (true) {
            String input = scan.nextLine().trim().toLowerCase();

            if (input.equalsIgnoreCase("ESC")) {
                systemClose();
            } else if (input.equals("1") || input.equals("today")) {
                LocalDate today = LocalDate.now(); // set today day
                date = Date.valueOf(today);
                break;
            } else if (input.equals("2") || input.equals("other date")) {
                String dateRegex = "\\d{4}-\\d{2}-\\d{2}";
                while (true) {
                    System.out.println("Please enter date yyyy-mm-dd");
                    String inputDate = scan.nextLine().trim();

                    if (inputDate.equalsIgnoreCase("ESC")) {
                        systemClose();
                    }

                    if (!inputDate.trim().matches(dateRegex)) {
                        System.out.println("Invalid date format dateRegex");
                        continue;
                    }

                    try {
                        date = Date.valueOf(inputDate);

                        if (date.toLocalDate().isBefore(LocalDate.now())) {
                            // date have right format and is before today
                            break;
                        } else {
                            System.out.println("Date cannot be in the futureb");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid date value. Please enter a valid date.");
                    }
                }
                break;
            } else {
                System.out.println("Invalid input. Please enter 1 or 2");
            }
        }
        System.out.println("Selected date: " + date);
    }

    private void systemClose() {
        scan.close();
        System.exit(0);
    }

    /// calories entered
    void selectItemsToEdit() {
        while (true) {
            System.out.println("Select option to edit (e.g. breakfast - 12)");
            String input = scan.nextLine();
            String[] inputSplit = input.split("-");
            String table = inputSplit[0].trim();
            int itemID = Integer.parseInt(inputSplit[1].trim());

            if (input.equalsIgnoreCase("ESC")) {
                systemClose();
                break;
            } else if (table.equalsIgnoreCase("BREAKFAST")
                    || table.equalsIgnoreCase("LUNCH")
                    || table.equalsIgnoreCase("DINNER")
                    || table.equalsIgnoreCase("SNACK")
                    && getInfo.getCaloriesDetails().containsValue(itemID)) {
                enterInfo.removeLineForEditedInformation(table, itemID);
                mealType = table;
                enterCalories();
                break;
            }
        }
    }
}
