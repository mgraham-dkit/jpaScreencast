package app;

import entities.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class SampleUpdateAction {
    public static void main(String[] args) {
        // Hide info AND warning messages about hibernate activities
        LogManager.getLogManager().getLogger("").setLevel(Level.SEVERE);

        // Get EntityManagerFactory - given to us by Persistence
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
        // Get EntityManager
        EntityManager entityManager = factory.createEntityManager();
        // Create a User object with a primary key value (userId = 10) that isn't in the database
        // This will cause the merge action to insert it as a new entity to be stored
        User user = new User(10, "mgrahamm", "Twit twoo", "mgrahamm@dkit.ie", LocalDate.now());

        // Get a transaction so we can change the database content
        EntityTransaction transaction = entityManager.getTransaction();
        try{
            // Create a save point that we can roll back to if the update fails
            transaction.begin();
            // Attempt to update the user's record with the changed information in this object
            entityManager.merge(user);
            // If we get here the update worked, so we can save the changes permanently
            transaction.commit();
        }catch(PersistenceException pe){
            System.out.println("A PersistenceException occurred while attempting to merge: " + user);
            System.out.println(pe.getMessage());
        }

        // Create user to save changes from
        // In this one we don't have a primary key set - we need one to do the update
        // We can get around this by looking up the matching record based on the unique email info
        User u = new User("michelle", "beginning to look a lot like", "mgraham@dkit.ie", LocalDate.now());
        // Select where there is only a single result expected (and handle where no match is found)
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where u.email = ?1", User.class);
        query.setParameter(1, u.getEmail());
        User result = null;
        try {
            // If there is a match for that email address, store it in a User object so we can get its userId
            result = query.getSingleResult();
            System.out.println("Entity matching specified email: " + result);
        }catch(NoResultException nre){
            System.out.println("No result found for specified email");
            System.out.println(nre.getMessage());
        }

        // If we found a match for the user email
        if(result != null){
            // Use the record/result's id as the id information in the object being updated
            u.setUserId(result.getUserId());

            // Now we have primary key data stored in the object, we can do the update!
            // Same steps as before
            transaction = entityManager.getTransaction();
            try{
                transaction.begin();
                entityManager.merge(u);
                transaction.commit();
            }catch(PersistenceException pe){
                System.out.println("A PersistenceException occurred while attempting to merge: " + user);
                System.out.println(pe.getMessage());
            }
        }

        entityManager.close();
        factory.close();
    }
}
