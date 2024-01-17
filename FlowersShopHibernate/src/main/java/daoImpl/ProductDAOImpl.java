/*
Author: Ene Bogdan
Country: Romania
*/
package entity.daoImpl;

import com.github.javafaker.Faker;
import entity.Orders;
import entity.Product;
import entity.dao.ProductDAO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.*;

@Setter
@Getter
@Log
@Log4j2
public class ProductDAOImpl implements ProductDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private Session session;
    private Transaction transaction;
    Scanner scanner = new Scanner(System.in);
    Random random = new Random();
    Faker faker = new Faker();


    @Override
    public void createProduct(Product product) {
        openSessionAndTransaction();

        if (product != null) {
            session.persist(product);
            commitTransactionAndCloseSession();
            log.info("The product has been successfully created.");
        } else {
            log.warning("Invalid product input.");
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            commitTransactionAndCloseSession();
        }
    }

    @Override
    public void createProduct(List<Product> productList) {
        openSessionAndTransaction();

        int numberOfProducts = faker.random().nextInt(1, 10);

        for (int i = 0; i < numberOfProducts; i++) {
            Product product = new Product();
            product.setName(FlowerNameGenerator.generateRandomFlowerName());
            product.setPrice(random.nextFloat(10F, 1000F));

            productList.add(product);
            session.persist(product);
        }
        commitTransactionAndCloseSession();
    }

    @Override
    public List<Product> findAllProducts() {
        openSession();
        List<Product> products = session.createQuery("FROM Product", Product.class).list();
        closeSession();
        return products;
    }

    @Override
    public Product getProductById(int productId) {
        openSession();
        try {
            return session.get(Product.class, productId);
        } finally {
            closeSession();
        }
    }

    @Override
    public Optional<Product> findProductById() {
        return Optional.empty();
    }


    public Optional<Product> findProductById(int productId) {
        openSession();
        try {
            return Optional.ofNullable(session.get(Product.class, productId));
        } finally {
            closeSession();
        }
    }

    @Override
    public void updateProductById(Product updatedProduct) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Optional<Product> existingProduct = findProductByIdToUpdate(session);
                if (existingProduct.isPresent()) {
                    Product oldProduct = existingProduct.get();

                    log.info("Old Product details: " + oldProduct);
                    log.info("New Product details: " + updatedProduct);

                    oldProduct.setName(updatedProduct.getName());
                    oldProduct.setPrice(updatedProduct.getPrice());

                    session.merge(oldProduct);
                    transaction.commit();
                    log.info("The product with the ID " + oldProduct.getId() + " has been successfully updated.");
                } else {
                    log.warning("Product with the provided ID not found.");
                }
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                log.warning("Failed to update the product. Please check the provided information. Error: " + e.getMessage());
            }
        }
    }

    public List<Product> getProductsInOrder(int orderId) {
        openSession();
        try {
            Orders order = session.get(Orders.class, orderId);
            if (order != null) {
                return order.getProducts();
            }
            return Collections.emptyList();
        } finally {
            closeSession();
        }
    }

    @Override
    public void deleteProductById() {
        openSessionAndTransaction();
        try {
            int id;
            System.out.print("Insert the product's ID you want to delete: ");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                System.out.println();
                Product product = session.get(Product.class, id);
                if (product != null) {
                    session.remove(product);
                    System.out.println("The product with the ID " + id + " has been successfully removed from database.");
                } else {
                    log.warning("The product with the ID " + id + " does NOT exist in the database.");
                }
            } else {
                log.warning("The product with the provided ID does NOT exist in the database.");
            }
        } catch (NoSuchElementException e) {
            log.warning("The product with the provided ID does NOT exist in the database.");
        } finally {
            scanner.nextLine();
            commitTransactionAndCloseSession();
        }
    }

    public Optional<Product> findProductByIdToUpdate() {
        try (Session session = sessionFactory.openSession()) {
            openSession();
            return findProductByIdToUpdate(session);
        }
    }

    public Optional<Product> findProductByIdToUpdate(Session session) {
        try {
            if (session == null || !session.isOpen()) {
                log.warning("Session is not open or is null.");
                return Optional.empty();
            }

            System.out.print("Insert the product's ID you want to modify: ");
            if (scanner.hasNextInt()) {
                int id = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                Optional<Product> product = Optional.ofNullable(session.find(Product.class, id));
                return product;
            } else {
                log.warning("Invalid input. Please enter a valid number for the product ID.");
                return Optional.empty();
            }
        }
        finally {

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
