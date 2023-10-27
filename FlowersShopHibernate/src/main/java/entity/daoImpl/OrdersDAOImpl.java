/*
Author: Ene Bogdan
Country: Romania
*/
package entity.daoImpl;

import com.github.javafaker.Faker;
import entity.Client;
import entity.Orders;
import entity.Product;
import entity.dao.ClientDAO;
import entity.dao.OrdersDAO;
import entity.dao.ProductDAO;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.util.*;

@Log
@Log4j2
public class OrdersDAOImpl implements OrdersDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private Session session;
    private Transaction transaction;
    Scanner scanner = new Scanner(System.in);
    Random random = new Random();
    Faker faker = new Faker();
    ProductDAO productDAO = new ProductDAOImpl();
    ClientDAO clientDAO = new ClientDAOImpl();


    @Override
    public void createOrders(List<Product> productList, String clientName) {
        openSessionAndTransaction();
        Orders orders = new Orders();
        orders.setSenderName(clientName);
        System.out.println("Sender's name is: " + orders.getSenderName());
        orders.setRecipientName(faker.funnyName().name());
        System.out.println("Recipient's name is: " + orders.getRecipientName());

        List<Product> availableProducts = productDAO.findAllProducts();

        if (availableProducts.isEmpty()) {
            log.warning("No products available.");
        } else {
            System.out.println("Available products:");
            for (int i = 0; i < availableProducts.size(); i++) {
                Product product = availableProducts.get(i);
                System.out.println((i + 1) + ". " + product.getName() + " - " + product.getPrice() + "$");
            }

            System.out.print("Insert the product IDs you want to send, separated by \",\": ");
            String input = scanner.nextLine();
            input = input.replaceAll("\\s", "");
            String[] productIndexes = input.split(",");

            List<Product> selectedProducts = new ArrayList<>();

            for (String productIndex : productIndexes) {
                try {
                    int productId = Integer.parseInt(productIndex);
                    Product product = productDAO.getProductById(productId);

                    if (product == null) {
                        log.warning("Product with ID " + productId + " does not exist. Please enter a valid product ID.");
                    } else {
                        selectedProducts.add(product);
                    }
                } catch (NumberFormatException e) {
                    log.warning("Invalid product ID: " + productIndex + ". Please enter a valid product ID.");
                }
            }

            Optional<Client> optionalClient = clientDAO.getClientByName(clientName);
            if (optionalClient.isPresent()) {
                Client client = optionalClient.get();
                orders.setClient(client);
                orders.setProducts(selectedProducts);
                orders.calculateOrderValue();
                System.out.println("Order contents:");
                for (Product product : selectedProducts) {
                    System.out.println("  - " + product.getName() + " - $" + product.getPrice());
                }
                session.persist(orders);
            } else {
                log.warning("Client with the name " + clientName + " was not found.");
            }
        }
        commitTransactionAndCloseSession();
    }


    @Override
    public Optional<Orders> findOrdersById() {
        openSession();
        try {
            System.out.print("Insert the order ID you want to search for: ");
            if (scanner.hasNextInt()) {
                int orderId = scanner.nextInt();
                System.out.println();
                Query<Orders> query = session.createQuery(
                        "SELECT DISTINCT o FROM Orders o " +
                                "LEFT JOIN FETCH o.products " +
                                "WHERE o.id = :orderId", Orders.class
                );
                query.setParameter("orderId", orderId);
                Orders order = query.uniqueResult();

                if (order != null) {
                    return Optional.of(order);
                } else {
                    log.warning("The inserted order ID does NOT exist.");
                    return Optional.empty();
                }
            } else {
                log.warning("Invalid input for order ID. Please provide a valid order ID.");
                return Optional.empty();
            }
        } finally {
            closeSession();
        }
    }

    public List<Product> getProductsInOrder(int orderId) {
        openSession();
        try {
            Query<Orders> query = session.createQuery(
                    "SELECT DISTINCT o FROM Orders o " +
                            "LEFT JOIN FETCH o.products " +
                            "WHERE o.id = :orderId", Orders.class
            );
            query.setParameter("orderId", orderId);
            Orders order = query.uniqueResult();

            if (order != null) {
                return order.getProducts();
            } else {
                log.warning("The inserted order ID does NOT exist.");
                return Collections.emptyList();
            }
        } finally {
            closeSession();
        }
    }

    public List<Orders> findAllOrdersWithProducts() {
        openSession();
        try {
            Query<Orders> query = session.createQuery(
                    "SELECT DISTINCT o FROM Orders o " +
                            "LEFT JOIN FETCH o.products", Orders.class
            );
            return query.list();
        } finally {
            closeSession();
        }
    }

    @Override
    public List<Orders> findAllOrdersByClientId(int clientId) {
        openSession();
        try {
            Query<Orders> query = session.createQuery("FROM Orders o WHERE o.client.id = :clientId", Orders.class);
            query.setParameter("clientId", clientId);
            List<Orders> orders = query.list();
            return orders;
        } finally {
            closeSession();
        }
    }

    @Override
    public void updateOrdersById() {
        openSessionAndTransaction();
        System.out.println("Insert the ID of the order you want to update: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        try {
            Orders orders = session.find(Orders.class, orderId);

            if (orders != null) {
                System.out.print("Insert new sender's name: ");
                String newSenderName = scanner.nextLine();
                orders.setSenderName(newSenderName);

                List<Product> newProducts = generateRandomProducts(faker.number().numberBetween(1, 10));
                orders.setProducts(newProducts);

                session.merge(orders);
                commitTransactionAndCloseSession();
                log.info("Order with ID " + orderId + " has been successfully updated.");
            } else {
                log.warning("The specified orders ID does not exist.");
            }
        } catch (Exception e) {
            log.warning("Failed to update the order.");
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public List<Product> generateRandomProducts(int numberOfProducts) {
        List<Product> productList = new ArrayList<>();
        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < numberOfProducts; i++) {
            Product product = new Product();
            product.setName(faker.commerce().productName());
            product.setPrice((float) (10 + random.nextInt(991)));
            productList.add(product);
        }

        return productList;
    }

    @Override
    public void deleteOrdersById() {
        System.out.println("Insert the order ID you want to delete: ");
        int orderId = scanner.nextInt();
        openSessionAndTransaction();
        try {
            Orders order = session.get(Orders.class, orderId);
            if (order != null) {
                session.remove(order);
                log.info("The order with ID " + orderId + " has been successfully removed.");
            } else {
                log.warning("The order with ID " + orderId + " does NOT exist in the database.");
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.warning("The order with the provided ID does NOT exist in the database.");
        } finally {
            commitTransactionAndCloseSession();
        }
    }

    public void openSession() {
        session = sessionFactory.openSession();
    }

    public void openSessionAndTransaction() {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    public void commitTransactionAndCloseSession() {
        transaction.commit();
        session.close();
    }

    public void closeSession() {
        session.close();
    }
}
