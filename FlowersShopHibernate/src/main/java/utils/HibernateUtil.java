package utils;

import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

@Log
@Log4j2
public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static SessionFactory buildSessionFactory() {
        try {
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();

            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            return metadata.getSessionFactoryBuilder().build();
        } catch (Exception ex) {

            log.severe(String.format("%sInitial SessionFactory creation failed.", ex));
            throw new ExceptionInInitializerError(ex);
        }
    }

}
