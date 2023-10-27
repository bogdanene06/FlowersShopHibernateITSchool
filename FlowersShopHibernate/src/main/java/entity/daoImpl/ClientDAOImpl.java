/*
Author: Ene Bogdan
Country: Romania
*/
package entity.daoImpl;

import com.github.javafaker.Faker;
import entity.Client;
import entity.Orders;
import entity.dao.ClientDAO;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

@Log
@Log4j2
public class ClientDAOImpl implements ClientDAO {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private Session session;
    private Transaction transaction;
    Scanner scanner = new Scanner(System.in);
    Faker faker = new Faker();

    @Override
    public void createClient() {
        openSessionAndTransaction();
        Client client = new Client();
        client.setName(faker.funnyName().name());
        client.setEmailAddress(faker.internet().emailAddress());
        session.persist(client);
        commitTransactionAndCloseSession();
    }
    public Optional<Client> getClientByName(String name) {
        openSession();
        try {
            Query<Client> query = session.createQuery("FROM Client c WHERE c.name = :name", Client.class);
            query.setParameter("name", name);
            Client client = query.uniqueResult();
            return Optional.ofNullable(client);
        } finally {
            closeSession();
        }
    }

    public List<Client> getClientsByNamePartial(String name) {
        openSession();
        try {
            Query<Client> query = session.createQuery("FROM Client c WHERE c.name LIKE :name", Client.class);
            query.setParameter("name", "%" + name + "%");
            List<Client> clients = query.list();
            return clients;
        } finally {
            closeSession();
        }
    }

    @Override
    public List<Client> findAllClients() {
        openSession();
        List<Client> clients = session.createQuery("FROM Client", Client.class).list();
        closeSession();
        return clients;
    }

    @Override
    public Optional<Client> findClientById() {
        openSession();
        int id;
        try {
            System.out.print("Insert client's ID you want to search for: ");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                System.out.println();
                Client client = session.find(Client.class, id);
                if (client != null) {
                    return Optional.of(client);
                } else {
                    log.warning("The inserted ID does NOT exist.");
                    return Optional.empty();
                }
            } else {
                log.warning("The inserted ID does NOT exist.");
                return Optional.empty();
            }
        } finally {
            closeSession();
        }
    }

    @Override
    public void updateClientById(Client client) {
        openSessionAndTransaction();
        try {
            session.merge(client);
            commitTransactionAndCloseSession();
            log.info("The client with the inserted ID has been successfully modified.");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.warning("The provided ID does NOT exist in the database.");
        }
    }


    @Override
    public void deleteClientById() {
        openSessionAndTransaction();
        try {
            int id;
            System.out.print("Insert the client's ID you want to delete: ");
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
                System.out.println();
                Client client = session.get(Client.class, id);
                if (client != null) {
                    session.remove(client);
                    System.out.println("The client with the ID " + id + " has been successfully removed from database.");
                } else {
                    log.warning("The client with the ID " + id + " does NOT exist in the database.");
                }
            } else {
                log.warning("The client with the provided ID does NOT exist in the database.");
            }
        } catch (NoSuchElementException e) {
            log.warning("The client with the provided ID does NOT exist in the database.");
        } finally {
            commitTransactionAndCloseSession();
        }

    }

    public Optional<Client> findClientByIdToUpdate() {
        openSession();
        try {
            System.out.print("Insert the client's ID you want to modify: ");
            if (scanner.hasNextInt()) {
                int id = scanner.nextInt();
                System.out.println();
                Optional<Client> client = Optional.ofNullable(session.find(Client.class, id));
                return client;
            } else {
                log.warning("The client with the provided ID does NOT exist in the database.");
                return Optional.empty();
            }
        } finally {
            closeSession();
        }
    }

    public void deleteClientAndOrders(Client client) {
        openSessionAndTransaction();
        try {
            List<Orders> clientOrders = client.getOrders();
            for (Orders order : clientOrders) {
                session.remove(order);
            }
            session.remove(client);

            commitTransactionAndCloseSession();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.warning("The orders belonging to the client could not be deleted.");
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
