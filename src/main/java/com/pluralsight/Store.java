package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Store {

    public static void main(String[] args) {
        // Initialize variables
        ArrayList<Product> inventory = new ArrayList<Product>();
        ArrayList<Product> cart = new ArrayList<Product>();
        double totalAmount = 0.0;

        // Load inventory from CSV file
        loadInventory("products.csv", inventory);

        // Create scanner to read user input
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        // Display menu and get user choice until they choose to exit
        while (choice != 3) {
            System.out.println("-".repeat(60));
            System.out.println("Welcome to the Digital Emporium!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.println("-".repeat(60));

            choice = scanner.nextInt();
            scanner.nextLine();

            // Call the appropriate method based on user choice
            switch (choice) {
                case 1:
                    displayProducts(inventory, cart, scanner);
                    break;
                case 2:
                    displayCart(cart, scanner, totalAmount);
                    break;
                case 3:
                    System.out.println("Thank you for shopping with us!");
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
    }

    public static void loadInventory(String fileName, ArrayList<Product> inventory) {
        try {
            new File(fileName).createNewFile();
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("[|]");
                String id = tokens[0];
                String name = tokens[1];
                double price = Double.parseDouble(tokens[2]);
                inventory.add(new Product(id, name, price));
            }
            inventory.sort(Comparator.comparing(Product::getId).reversed());

            reader.close();
        } catch (IOException e) {
            System.err.println("File issue!");
        } catch (Exception e) {
            System.err.println("Something went wrong!\n" + e);
        }
    }

    public static void displayProducts(ArrayList<Product> inventory, ArrayList<Product> cart, Scanner scanner) {
        System.out.println();
        printColumn();
        for (Product i : inventory) {
            System.out.printf("|%s|\n+%s+\n", i, "-".repeat(i.toString().length()));
        }
        // Another menu for selecting an item to add to cart. -Prompts user to enter an ID
        String userChoice;
        boolean shopping = true;
        while (shopping) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("Please enter the ID of an item you would like to add to your cart.\nOr you can enter 'X' to go back.");
            System.out.println("-".repeat(60));
            userChoice = scanner.nextLine();
            if (userChoice.equalsIgnoreCase("X"))
                shopping = false;

            // Calls the search by ID method, validates user entry, user confirms choice, then places item in cart.
            try {
                Product selectedProduct = findProductById(userChoice, inventory);
                boolean confirming = true;
                while (confirming && !userChoice.equalsIgnoreCase("x")) {
                    System.out.println("-".repeat(60));
                    System.out.printf("You have selected %s, which costs $%.2f.", selectedProduct.getName(), selectedProduct.getPrice());
                    System.out.println("\nWould you like to add this item to your cart? (Y)es/(N)o\n" + "-".repeat(60));
                    userChoice = scanner.nextLine();
                    if (userChoice.equalsIgnoreCase("y")) {
                        inventory.remove(selectedProduct);
                        cart.add(selectedProduct);
                    }
                    confirming = false;
                }
                shopping = false;
            } catch (NullPointerException e) {
                System.out.println("Blank/Invalid ID! No selection.");
            } catch (Exception e) {
                System.out.println("Something went wrong!\n" + e);
            }
        }
    }

    public static void displayCart(ArrayList<Product> cart, Scanner scanner, double totalAmount) {
        System.out.println();

        // Displays objects in cart via table with a total amount below.
        printColumn();
        for (Product i : cart) {
            System.out.printf("|%s|\n+%s+\n", i, "-".repeat(i.toString().length()));
            totalAmount += i.getPrice();
        }
        System.out.printf("| Total: %-8.2f |\n+%s+\n", totalAmount, "-".repeat(17));

        // Menu similar to displayProducts - exit, enter a matching ID to remove item from cart, or check out.
        String userChoice;
        boolean shopping = true;
        while (shopping) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("Enter 'C' to check out \nEnter the ID of an item you would like to remove.\nEnter 'X' to go back.");
            System.out.println("-".repeat(60));
            userChoice = scanner.nextLine();
            if (userChoice.equalsIgnoreCase("X"))
                shopping = false;
            if (userChoice.equalsIgnoreCase("C"))
                checkOut(cart, totalAmount, scanner);
            else {
                try {
                    Product selectedProduct = findProductById(userChoice, cart);
                    boolean confirming = true;
                    while (confirming && !userChoice.equalsIgnoreCase("x")) {
                        System.out.println("\n" + "-".repeat(60));
                        System.out.printf("You have selected %s, which costs $%.2f.", selectedProduct.getName(), selectedProduct.getPrice());
                        System.out.println("\nWould you like to remove this item from your cart? (Y)es/(N)o\n" + "-".repeat(60));
                        userChoice = scanner.nextLine();
                        if (userChoice.equalsIgnoreCase("y")) {
                            cart.remove(selectedProduct);
                            cart.add(selectedProduct);
                        }
                        confirming = false;
                    }
                    shopping = false;
                } catch (NullPointerException e) {
                    System.out.println("Blank/Invalid ID! No selection.");
                } catch (Exception e) {
                    System.out.println("Something went wrong!\n" + e);
                }
            }
        }
    }
    public static void checkOut(ArrayList<Product> cart, double totalAmount, Scanner scanner) {
        // No items = no total = redundant screen - instantly returns with nothing in the cart.
        if (totalAmount == 0) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("Nothing to check out! Returning to cart.");
            return;
        }
        String border = "+" + "-".repeat(52) + "+";


        // Menu that loops with updating BalancePaid/Due, ends when user pays TotalAmount or over.
        double balancePaid = 0;
        boolean isCheckOut = true;
        while (isCheckOut) {
            double balanceDue = totalAmount - balancePaid;

            System.out.println(border);
            System.out.printf("| %-24s | Total Price: %-10.2f |\n", "Order Summary", totalAmount);
            System.out.println(border);
            System.out.printf("| Balance Paid: %-10.2f | Balance Due: %-10.2f |\n", balancePaid, balanceDue);
            System.out.println(border);
            if (balanceDue <= 0) {
                cart.clear();
                System.out.printf("Success! Thank you for your patronage!\nChange due: $%.2f\n", Math.abs(balanceDue));
                isCheckOut = false;
            }
            System.out.print("Please enter a balance to pay: ");
            try {
                balancePaid += Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid entry! Please enter a number to pay.");
            }
        }
    }
    public static Product findProductById(String id, ArrayList<Product> inventory) {

        // Iterate through an inventory, matching unique ID. Returns ID if found, nothing if not.
        for (Product i : inventory) {
            if (i.getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return null;

    }

    public static void printColumn() {

        // Repeatable pretty formatting!
        String header = String.format(" %-10s | %-40s | %-14s ", "ID", "Name", "Price");
        String border = "#" + "=".repeat(header.length()) + "#";
        System.out.printf("%s\n|%s|\n%s\n", border, header, border);
    }

}


