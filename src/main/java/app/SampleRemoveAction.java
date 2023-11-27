package app;

import entities.User;
import jakarta.persistence.*;

import java.util.logging.Level;
import java.util.logging.LogManager;

public class SampleRemoveAction {
    public static void main(String[] args) {
        // Hide info messages about hibernate activities
        // Can also hide warnings by setting level to Level.SEVERE
        LogManager.getLogManager().getLogger("").setLevel(Level.SEVERE);

        // Get EntityManagerFactory - given to us by Persistence
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
        // Get EntityManager
        EntityManager entityManager = factory.createEntityManager();

        // Select by primary key
        int userId = 102;
        User toBeDeleted = entityManager.find(User.class, userId);
        if(toBeDeleted != null){
            EntityTransaction transaction = entityManager.getTransaction();
            try{
                transaction.begin();
                entityManager.remove(toBeDeleted);
                transaction.commit();
            }catch(PersistenceException pe){
                System.out.println("An PersistenceException occurred while attempting to remove " + toBeDeleted);
                System.out.println(pe.getMessage());
                transaction.rollback();
            }
        }
        toBeDeleted = entityManager.find(User.class, userId);
        if (toBeDeleted != null){
            System.out.println("The user with userId = " + userId + " could not be deleted at this time");
        }else{
            System.out.println("The user with userId = " + userId + " was deleted successfully!");
        }

        entityManager.close();
        factory.close();
    }
}
