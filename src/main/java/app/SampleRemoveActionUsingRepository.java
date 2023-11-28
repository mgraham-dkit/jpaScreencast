package app;

import entities.User;
import jakarta.persistence.*;
import repositories.UserRepository;
import repositories.UserRepositoryInterface;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class SampleRemoveActionUsingRepository {
    public static void main(String[] args) {
        // Hide info messages about hibernate activities
        // Can also hide warnings by setting level to Level.SEVERE
        LogManager.getLogManager().getLogger("").setLevel(Level.SEVERE);

        // Get EntityManagerFactory - given to us by Persistence
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
        // Get EntityManager
        EntityManager entityManager = factory.createEntityManager();

        UserRepositoryInterface userRepo = new UserRepository(entityManager);
        User toBeDeleted = new User(1, "michelle", "", "mgraham@dkit.ie", LocalDate.now());
        boolean isDeleted = userRepo.delete(toBeDeleted);
        if (!isDeleted){
            System.out.println("The user with userId = " + toBeDeleted.getUserId() + " could not be deleted at this time");
        }else{
            System.out.println("The user with userId = " + toBeDeleted.getUserId() + " was deleted successfully!");
        }

        entityManager.close();
        factory.close();
    }
}
