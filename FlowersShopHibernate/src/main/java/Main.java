import com.github.javafaker.Faker;
import entity.Product;
import entity.daoImpl.ClientDAOImpl;
import entity.daoImpl.FlowerNameGenerator;
import entity.daoImpl.OrdersDAOImpl;
import entity.daoImpl.ProductDAOImpl;
import lombok.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import utils.ClientsOperations;
import utils.OrdersOperations;
import utils.ProductsOperations;

import java.util.*;

@Setter
@Getter
@Log
@Log4j2
@ToString
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        Faker faker = new Faker();
        ProductDAOImpl productDAO = new ProductDAOImpl();
        OrdersDAOImpl ordersDAO = new OrdersDAOImpl();
        ClientDAOImpl clientDAO = new ClientDAOImpl();

        ProductsOperations productsOperations = new ProductsOperations();
        OrdersOperations ordersOperations = new OrdersOperations(scanner, ordersDAO, clientDAO);
        ClientsOperations clientsOperations = new ClientsOperations(scanner, clientDAO);


        //----- creating a list of random products -----
        int randomNumberOfRandomProducts = faker.number().numberBetween(1, 30);
        for (int i = 0; i < randomNumberOfRandomProducts; i++) {
            Product product = new Product();
            product.setName(FlowerNameGenerator.generateRandomFlowerName());
            product.setPrice(random.nextFloat(10, 1000));
            productDAO.createProduct(product);
        }
        if (randomNumberOfRandomProducts == 1) {
            log.info("The product has been successfully created.");
        } else if (randomNumberOfRandomProducts > 1) {
            log.info("The products have been successfully created.");
        } else {
            log.warning("Number of products cannot be NEGATIVE. Please insert a number above 0 (zero)!");
        }
        System.out.println();

        //----- creating a list of random clients -----
        int numberOfRandomClients = faker.number().numberBetween(1, 10);
        for (int i = 0; i < numberOfRandomClients; i++) {
            clientDAO.createClient();
        }
        if (numberOfRandomClients == 1) {
            log.info("The client has been successfully created.");
        } else if (numberOfRandomClients > 1) {
            log.info("The clients have been successfully created.");
        } else {
            log.warning("Number of clients cannot be NEGATIVE. Please insert a number above 0 (zero)!");
        }
        System.out.println();

        //----- creating the main menu -----
        int mainMenuOption;
        boolean exitApplication = false;

        do {
            System.out.println("""
                Choose the desired option:
                1. Clients options
                2. Orders options
                3. Products options
                4. Exit the application!

                Type it here: """);

            if (scanner.hasNextInt()) {
                mainMenuOption = scanner.nextInt();

                switch (mainMenuOption) {
                    case 1:
                        clientsOperations.clientsMenu();
                        break;

                    case 2:
                        ordersOperations.ordersMenu();
                        break;

                    case 3:
                        productsOperations.productsMenu();
                        break;

                    case 4:
                        exitApplication = exitingApplication(scanner);
                        break;

                    default:
                        log.warning("Invalid OPTION on main menu. Choose an option between 1 to 4!");
                }
            } else {
                scanner.next();
                log.warning("Invalid CHARACTER on choosing main menu options. Type a NUMBER between 1 to 4!");
            }
        } while (!exitApplication);
        scanner.close();
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
