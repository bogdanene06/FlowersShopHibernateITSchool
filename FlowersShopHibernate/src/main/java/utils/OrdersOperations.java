/*
Author: Ene Bogdan
Country: Romania
*/
package utils;

import entity.Client;
import entity.Orders;
import entity.Product;
import entity.daoImpl.ClientDAOImpl;
import entity.daoImpl.OrdersDAOImpl;
import lombok.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Log
@Log4j2
public class OrdersOperations {
    private Scanner scanner;
    private OrdersDAOImpl ordersDAO;
    private ClientDAOImpl clientDAO;

    public void ordersMenu(){
        int ordersOperations = 0;
        do {
            System.out.print("""
                                    Orders menu
                                    Choose the desired option:
                                    1. Create an order;
                                    2. View all the orders;
                                    3. View order details by ID;
                                    4. View all the orders of a specified client's ID;
                                    5. Update an order by ID;
                                    6. Delete an order by ID (without deleting the client);
                                    7. Go back to the main menu;
                                    8. Exit application.
                                    
                                    Type it here: 
                                    """);
            if (scanner.hasNextInt()) {
                ordersOperations = scanner.nextInt();
                switch (ordersOperations) {
                    case 1:
                        System.out.print("Choose the desired option:\n1. Create a new client\n2. Create an order (for already existing accounts)\n");
                        if (scanner.hasNextInt()) {
                            int option = scanner.nextInt();
                            if (option == 1) {
                                System.out.print("Type in the number of clients you want to insert in the database: ");
                                if (scanner.hasNextInt()) {
                                    int x = scanner.nextInt();
                                    for (int i = 0; i < x; i++) {
                                        clientDAO.createClient();
                                    }
                                    if (x == 1) {
                                        log.info("The client has been successfully created.");
                                    } else if (x > 1) {
                                        log.info("The clients have been successfully created.");
                                    } else {
                                        log.warning("Number of clients cannot be NEGATIVE. Please insert a number above 0 (zero)!");
                                    }
                                } else {
                                    log.warning("Invalid CHARACTER on \"Creating client\". You should type a NUMBER!");
                                }
                            } else if (option == 2) {
                                scanner.nextLine();
                                System.out.print("Insert the sender's name: ");
                                String senderName = scanner.nextLine();
                                Optional<Client> optionalClient = clientDAO.getClientByName(senderName);

                                if (optionalClient.isPresent()) {
                                    List<Product> productList = new ArrayList<>();
                                    ordersDAO.createOrders(productList, senderName);
                                } else {
                                    log.warning("Sender client does not exist. Please create it first.");
                                }
                            } else {
                                log.warning("Invalid option. Choose either 1 or 2.");
                            }
                        } else {
                            scanner.next();
                            log.warning("Invalid CHARACTER on choosing options. Type a NUMBER between 1 or 2.");
                        }
                        System.out.println();
                        break;

                    case 2:
                        List<Orders> allOrders = ordersDAO.findAllOrdersWithProducts();
                        for (Orders order : allOrders) {
                            System.out.println("Order ID: " + order.getId());
                            System.out.println("Sender: " + order.getSenderName());
                            System.out.println("Recipient: " + order.getRecipientName());
                            System.out.println("Products ordered:");

                            List<Product> products = order.getProducts();
                            if (!products.isEmpty()) {
                                for (Product product : products) {
                                    System.out.println("  - " + product.getName() + " - $" + product.getPrice());
                                }
                            } else {
                                System.out.println("No products in this order.");
                            }

                            System.out.println();
                        }
                        break;

                    case 3:
                        Optional<Orders> optionalOrder = ordersDAO.findOrdersById();
                        if (optionalOrder.isPresent()) {
                            Orders order = optionalOrder.get();
                            System.out.println("Order ID: " + order.getId());

                            Optional<Client> senderClient = clientDAO.getClientByName(order.getSenderName());
                            if (senderClient.isPresent()) {
                                System.out.println("Sender: " + senderClient.get().getName());
                            } else {
                                System.out.println("Sender client not found.");
                            }

                            System.out.println("Recipient: " + order.getRecipientName());

                            List<Product> orderProducts = order.getProducts();
                            if (!orderProducts.isEmpty()) {
                                System.out.println("Details in order no. " + order.getId());
                                for (Product product : orderProducts) {
                                    System.out.println("Product ID: " + product.getId() + " | Name: " + product.getName() + " | Price: $" + product.getPrice());
                                }
                            } else {
                                System.out.println("No products in this order.");
                            }
                        }
                        break;

                    case 4:
                        System.out.print("Insert the client's ID to view his/hers orders: ");
                        if (scanner.hasNextInt()) {
                            int clientId = scanner.nextInt();
                            List<Orders> clientOrders = ordersDAO.findAllOrdersByClientId(clientId);

                            if (clientOrders.isEmpty()) {
                                log.info("No orders found for the specified client ID.");
                            } else {
                                log.info("Orders for client with ID " + clientId + ":");
                                for (Orders order : clientOrders) {
                                    System.out.println("Order ID: " + order.getId() + " | " + "Sender: " + order.getSenderName() + " | " + "Recipient: " + order.getRecipientName() + " | " + order.getProducts() + ";");
                                }
                            }
                        } else {
                            log.warning("Invalid input for client ID. Please enter a valid number.");
                        }
                        break;

                    case 5:
                      ordersDAO.updateOrdersById();
                        break;

                    case 6:
                        ordersDAO.deleteOrdersById();
                        break;


                    case 7:
                        break;

                    case 8:
                        if (exitingApplication(scanner)) {
                            return;
                        }
                        break;

                    default:
                        log.warning("Invalid OPTION on orders menu. Choose an option between 1 to 8!");
                }

            } else {
                scanner.next();
                log.warning("Invalid CHARACTER on choosing orders menu options. Type a NUMBER between 1 to 8!");
            }


        } while (ordersOperations != 7);
    }

    private static boolean exitingApplication(Scanner scanner) {
        System.out.println("Are you sure you want to exit? (YES/NO)");
        String confirmation = scanner.next();
        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            System.out.println("Exiting the main application. Goodbye!");
            scanner.close();
            System.exit(0);
            return true;
        } else if (confirmation.equalsIgnoreCase("no") || confirmation.equalsIgnoreCase("n")) {
            System.out.println("Returning to application.");
            return false;
        } else {
            System.out.println("Invalid input. Please type either 'yes' or 'no' (insensitive case).");
            return exitingApplication(scanner);
        }
    }
}
