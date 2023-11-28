package repositories;

import entities.User;

import java.util.List;

public interface UserRepositoryInterface {
    boolean save(User u);
    boolean delete(User u);
    User update(User u);
    List<User> getAllUsers();
    List<User> getAllUsersContainingEmailDomain(String domain);
    User getUserByUserId(int userId);
    User getUserByEmail(String email);
}
