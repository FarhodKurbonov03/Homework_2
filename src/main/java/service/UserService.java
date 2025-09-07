package service;

import model.User;
import java.util.List;

public interface UserService {
    void addUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUserById(Long id);
}
