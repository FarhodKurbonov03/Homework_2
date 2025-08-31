package dao;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void addUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUserById(Long id);
}
