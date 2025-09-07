package service.impl;

import dao.UserDao;
import exceptions.dao.UserNotFoundException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserDao userDao;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userDao = Mockito.mock(UserDao.class);
        userService = new UserServiceImpl(userDao);
    }

    @Test
    void addUser() {
        User user = createUser("Иван", "ivan@test.com", 25);
        userService.addUser(user);
        verify(userDao, times(1)).addUser(user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = createUser("Петр", "petr@test.com", 30);
        user.setId(1L);
        when(userDao.getUserById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);
        assertEquals("Петр", result.getName());
        assertEquals("petr@test.com", result.getEmail());
    }

    @Test
    void getUserById_shouldThrowIfNotFound() {
        when(userDao.getUserById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(99L));
    }

    @Test
    void getAllUsers() {
        List<User> users = List.of(createUser("Катя", "katya@test.com", 22));
        when(userDao.getAllUsers()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("Катя", result.get(0).getName());
    }

    @Test
    void updateUser() {
        User user = createUser("Иван", "ivan@test.com", 25);
        user.setId(1L);
        doNothing().when(userDao).updateUser(user);
        userService.updateUser(user);
        verify(userDao, times(1)).updateUser(user);
    }

    @Test
    void deleteUserById() {
        Long id = 1L;
        doNothing().when(userDao).deleteUserById(id);
        userService.deleteUserById(id);
        verify(userDao, times(1)).deleteUserById(id);
    }

    private User createUser(String name, String email, int age) {
        return new User(name, email, age);
    }
}
