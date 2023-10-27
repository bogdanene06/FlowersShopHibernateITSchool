/*
Author: Ene Bogdan
Country: Romania
*/
package utils;

import entity.Client;
import entity.daoImpl.ClientDAOImpl;
import lombok.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

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

public class ClientsOperations {

    private Scanner scanner = new Scanner(System.in);
    private ClientDAOImpl clientDAO;

    public void clientsMenu() {
        int clientOperations = 0;
        do {
            System.out.print("""
                    Clients menu
                    Choose the desired option:
                    1. Create one or more clients
                    2. View all the clients
                    3. Find client by name
                    4. View a client details account by ID
                    5. Update a client by ID
                    6. Delete a client account by ID
                    7. Go back to the main menu
                    8. Exit application!
                    
                    Type it here: 
                    """);
            if (scanner.hasNextInt()) {
                clientOperations = scanner.nextInt();
                switch (clientOperations) {
                    case 1:
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
                        System.out.println();
                        break;

                    case 2:
                        System.out.println("All clients are:");
                        clientDAO.findAllClients().forEach(System.out::println);
                        break;

                    case 3:
                        System.out.print("Enter the exact name of the client to be found: ");
                        scanner.nextLine();
                        String clientToBeFound = scanner.nextLine();
                        List<Client> foundClients = clientDAO.getClientsByNamePartial(clientToBeFound);
                        if (foundClients.isEmpty()) {
                            log.warning("No client found with the provided name.");
                        } else {
                            log.info("Found clients:");
                            for (Client foundClient : foundClients) {
                                log.info("Client ID: " + foundClient.getId() + ", Name: " + foundClient.getName());
                            }
                        }
                        if (!foundClients.isEmpty()) {
                            System.out.println("The client named " + clientToBeFound + " already exists in the database.");
                        } else {
                            log.info("The name " + clientToBeFound + " does NOT exists in the database. You can create it.");
                        }
                        break;

                    case 4:
                        System.out.println(clientDAO.findClientById());
                        break;

                    case 5:
                        Optional<Client> optionalClient = clientDAO.findClientByIdToUpdate();
                        if (optionalClient.isPresent()) {
                            Client clientToUpdate = optionalClient.get();
                            clientToUpdate.setName("Nume client modificat");
                            clientToUpdate.setEmailAddress("Adresa e-mail modificata");
                            clientDAO.updateClientById(clientToUpdate);
                        } else {
                            log.warning("The provided ID does NOT exist in the database.");
                            System.out.println();
                        }
                        break;

                    case 6:
                        clientDAO.deleteClientById();
                        break;

                    case 7:
                        break;

                    case 8:
                        if (exitingApplication(scanner)) {
                            return;
                        }
                        break;
                    default:
                        log.warning("Invalid OPTION on main menu. Choose an option between 1 to 8!");
                }

            } else {
                scanner.next();
                log.warning("Invalid CHARACTER on choosing main menu options. Type a NUMBER between 1 to 8!");

            }
        }
        while (clientOperations != 7);
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
