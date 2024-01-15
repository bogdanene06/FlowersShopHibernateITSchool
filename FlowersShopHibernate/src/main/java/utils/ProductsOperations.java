/*
Author: Ene Bogdan
Country: Romania
*/
package utils;

import entity.Product;
import entity.daoImpl.FlowerNameGenerator;
import entity.daoImpl.ProductDAOImpl;
import lombok.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Log
@Log4j2
public class ProductsOperations {
    private Scanner scanner = new Scanner(System.in);
    private ProductDAOImpl productDAO = new ProductDAOImpl();
    private Random random = new Random();

    public void productsMenu() {
        int productOperations = 0;
        do {
            System.out.print("""
                    Products menu
                    Choose the desired option:
                    1. Create one or more products;
                    2. View all the products;
                    3. View product details by ID;
                    4. Update a product by ID (without updating the order);
                    5. Delete a product by ID (without deleting the order);
                    6. Go back to the main menu;
                    7. Exit application.
                    
                    Type it here: 
                    """);
            if (scanner.hasNextInt()) {
                productOperations = scanner.nextInt();
                switch (productOperations) {
                    case 1:
                        System.out.print("Type in the number of products you want to insert in the database: ");
                        if (scanner.hasNextInt()) {
                            int x = scanner.nextInt();
                            scanner.nextLine();

                            for (int i = 0; i < x; i++) {
                                Product product = new Product();
                                product.setName(FlowerNameGenerator.generateRandomFlowerName());
                                product.setPrice(random.nextFloat(10, 1000));
                                productDAO.createProduct(product);
                            }
                            if (x == 1) {
                                log.info("The product has been successfully created.");
                            } else if (x > 1) {
                                log.info("The products have been successfully created.");
                            } else {
                                log.warning("Invalid CHARACTER on \"Creating product\". You should type a NUMBER!");
                                scanner.nextLine();
                            }
                        } else {
                            log.warning("Invalid CHARACTER on \"Creating product\". You should type a NUMBER!");
                        }
                        System.out.println();
                        break;
                    case 2:
                        System.out.println("All the products are:");
                        productDAO.findAllProducts().forEach(System.out::println);
                        break;

                    case 3:
                        System.out.print("Enter the product ID: ");
                        if (scanner.hasNextInt()) {
                            int productId = scanner.nextInt();
                            Optional<Product> product = productDAO.findProductById(productId);
                            if (product.isPresent()) {
                                System.out.println("Product details: " + product.get());
                            } else {
                                log.warning("Product with ID " + productId + " not found.");
                            }
                        } else {
                            log.warning("Invalid CHARACTER on \"View product details by ID\". You should type a NUMBER!");
                            scanner.nextLine(); // Consuming the invalid input
                        }
                        break;

                    case 4:
                        Optional<Product> optionalProduct = productDAO.findProductByIdToUpdate();
                        if (optionalProduct.isPresent()) {
                            Product productToUpdate = optionalProduct.get();
                            productToUpdate.setName("Product name modified.");
                            productToUpdate.setPrice(999F);
                            productDAO.updateProductById(productToUpdate);
                        } else {
                            log.warning("The provided ID does NOT exist in the database.");
                            System.out.println();
                        }
                        break;

                    case 5:
                        productDAO.deleteProductById();
                        break;

                    case 6:
                        break;

                    case 7:
                        if (exitingApplication(scanner)) {
                            return;
                        }
                        break;
                    default:
                        log.warning("Invalid OPTION on product menu. Choose an option between 1 to 7!");
                }
            } else {
                scanner.next();
                log.warning("Invalid CHARACTER on choosing product menu options. Type a NUMBER between 1 to 7!");
            }
        } while (productOperations != 6);
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
