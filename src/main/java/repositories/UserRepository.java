package repositories;

import entities.User;
import jakarta.persistence.*;

import java.util.List;

public class UserRepository implements UserRepositoryInterface{
    private EntityManager entityManager;

    public UserRepository(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public boolean save(User u) {
        EntityTransaction transaction = entityManager.getTransaction();
        // try the following:
        try {
            // Start transaction
            transaction.begin();
            // persist the object
            entityManager.persist(u);
            // Commit the transaction
            transaction.commit();
            return true;
        }catch(PersistenceException pe) {
            System.out.println("A persistence exception occurred while attempting to persist " + u);
            System.out.println(pe.getMessage());
            // Rollback the transaction
            transaction.rollback();
            return false;
        }
    }

    @Override
    public boolean delete(User u) {
        User toBeDeleted = entityManager.find(User.class, u.getUserId());
        if(toBeDeleted != null){
            EntityTransaction transaction = entityManager.getTransaction();
            try{
                transaction.begin();
                entityManager.remove(toBeDeleted);
                transaction.commit();
                return true;
            }catch(PersistenceException pe){
                System.out.println("An PersistenceException occurred while attempting to remove " + toBeDeleted);
                System.out.println(pe.getMessage());
                transaction.rollback();
            }
        }
        return false;
    }

    @Override
    public User update(User u) {
        User updatedUser = null;
        // Get a transaction so we can change the database content
        EntityTransaction transaction = entityManager.getTransaction();
        try{
            // Create a save point that we can roll back to if the update fails
            transaction.begin();
            // Attempt to update the user's record with the changed information in this object
            updatedUser = entityManager.merge(u);
            // If we get here the update worked, so we can save the changes permanently
            transaction.commit();
        }catch(PersistenceException pe){
            System.out.println("A PersistenceException occurred while attempting to merge: " + u);
            System.out.println(pe.getMessage());
        }
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u", User.class);
        List<User> userList = query.getResultList();
        return userList;
    }

    @Override
    public List<User> getAllUsersContainingEmailDomain(String domain) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where u.email LIKE ?1", User.class);
        query.setParameter(1, "%"+domain+"%");
        List<User> userList = query.getResultList();
        return userList;
    }

    @Override
    public User getUserByUserId(int userId) {
        User match = entityManager.find(User.class, userId);
        return match;
    }

    @Override
    public User getUserByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where u.email = ?1", User.class);
        query.setParameter(1, email);
        try {
            User result = query.getSingleResult();
            return result;
        }catch(NoResultException nre){
            System.out.println("No result found for specified email");
            System.out.println(nre.getMessage());
            return null;
        }
    }


}
