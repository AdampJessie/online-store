package com.pluralsight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Store {

    private static final ArrayList<Product> cart = new ArrayList<Product>();


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
            System.out.println("Welcome to the Online Store!");
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
        printColumn();
        for (Product i : inventory) {
                System.out.printf("|%s|\n+%s+\n", i, "-".repeat(i.toString().length()));
        }

        String userChoice;
        boolean shopping = true;
        while (shopping) {
            System.out.println("-".repeat(60));
            System.out.println("Please enter the ID of an item you would like to purchase.\nOr you can enter 'X' to go back.");
            System.out.println("-".repeat(60));
            userChoice = scanner.nextLine();
            if (userChoice.equalsIgnoreCase("X"))
                shopping = false;

            try {
                Product selectedProduct = findProductById(userChoice, inventory);
                boolean confirming = true;
                while (confirming && !userChoice.equalsIgnoreCase("x")){
                    System.out.println("-".repeat(60));
                    System.out.printf("You have selected %s, which costs $%.2f.", selectedProduct.getName(), selectedProduct.getPrice());
                    System.out.println("\nWould you like to add this item to your cart? (Y)es/(N)o\n"+"-".repeat(60));
                    userChoice = scanner.nextLine();
                    if (userChoice.equalsIgnoreCase("y")){
                        inventory.remove(selectedProduct);
                        cart.add(selectedProduct);
                    }
                    confirming = false;
                }
                shopping = false;


            }catch (NullPointerException e){
                System.out.println("Blank/Invalid ID! No selection.");
            }catch (Exception e){
                System.out.println("Something went wrong!\n"+e);
            }
        }

    }

    public static void displayCart(ArrayList<Product> cart, Scanner scanner, double totalAmount) {
        // This method should display the items in the cart ArrayList, along
        // with the total cost of all items in the cart. The method should
        // prompt the user to remove items from their cart by entering the ID
        // of the product they want to remove. The method should update the cart ArrayList and totalAmount
        // variable accordingly.
    }

    public static void checkOut(ArrayList<Product> cart, double totalAmount) {
        // This method should calculate the total cost of all items in the cart,
        // and display a summary of the purchase to the user. The method should
        // prompt the user to confirm the purchase, and calculate change and clear the cart
        // if they confirm.
    }

    public static Product findProductById(String id, ArrayList<Product> inventory) {
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
