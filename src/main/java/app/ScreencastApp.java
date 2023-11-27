package app;

import entities.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class ScreencastApp {
    public static void main(String[] args) {
        // Hide info messages about hibernate activities
        // Can also hide warnings by setting level to Level.SEVERE
        LogManager.getLogManager().getLogger("").setLevel(Level.SEVERE);

        // Get EntityManagerFactory - given to us by Persistence
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
        // Get EntityManager
        EntityManager entityManager = factory.createEntityManager();
        // Create entity instance (User)
        User normalUser = new User("grahamm", "password", "grahamm@dkit.ie", LocalDate.now());
        // Save entity to database
        // Get transaction
        EntityTransaction transaction = entityManager.getTransaction();
        // try the following:
        try {
            // Start transaction
            transaction.begin();
            // persist the object
            entityManager.persist(normalUser);
            // Commit the transaction
            transaction.commit();
        }catch(PersistenceException pe) {
            System.out.println("A persistence exception occurred while attempting to persist " + normalUser);
            System.out.println(pe.getMessage());
            // Rollback the transaction
            transaction.rollback();
        }
        // Change entity after saving
        normalUser.setPassword("sillybilly");

        // Select all users
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u", User.class);
        List<User> userList = query.getResultList();
        System.out.println("Select all search results: " + userList);

        // Select all users where email address contains dkit.ie
        query = entityManager.createQuery("SELECT u from User u where u.email LIKE ?1", User.class);
        query.setParameter(1, "%@dkit.ie%");
        userList = query.getResultList();
        System.out.println("Parameterized search results: " + userList);

        // Select by primary key
        User match = entityManager.find(User.class, 11);
        System.out.println("Entity matching primary key of 1: " + match);

        // Select where there is only a single result expected (and handle where no match is found)
        query = entityManager.createQuery("SELECT u from User u where u.email = ?1", User.class);
        query.setParameter(1, "graham@dkit.ie");
        try {
            User result = query.getSingleResult();
            System.out.println("Entity matching specified email: " + result);
        }catch(NoResultException nre){
            System.out.println("No result found for specified email");
            System.out.println(nre.getMessage());
        }


        // End program - close resources
        entityManager.close();
        factory.close();
    }
}
