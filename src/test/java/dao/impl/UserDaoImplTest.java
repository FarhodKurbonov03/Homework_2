package dao.impl;

import dao.UserDao;
import model.User;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoImplTest {

    private static PostgreSQLContainer<?> postgreSQLContainer;
    private SessionFactory testSessionFactory;
    private static UserDao userDao;

    @BeforeAll
    void setUpContainer() {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("postgres")
                .withPassword("postgres");
        postgreSQLContainer.start();

        testSessionFactory = util.HibernateUtil.initForTest(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        );
        userDao = new UserDaoImpl(testSessionFactory);
    }

    @AfterAll
    void tearDown() {
        if (testSessionFactory != null) {
            testSessionFactory.close();
        }
        if (postgreSQLContainer != null) {
            postgreSQLContainer.stop();
        }
    }

    @BeforeEach
    void cleanDb() {
        try (var session = testSessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @Order(1)
    void addUser() {
        User user = createUser("Иван", "ivan@test.com", 25);
        userDao.addUser(user);
        List<User> users = userDao.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Иван", users.get(0).getName());
    }

    @Test
    @Order(2)
    void getUserById() {
        User user = createUser("Катя", "katya@test.com", 22);
        userDao.addUser(user);
        Optional<User> found = userDao.getUserById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("Катя", found.get().getName());
    }

    @Test
    @Order(3)
    void getAllUsers() {
        User user1 = createUser("Аня", "anya@test.com", 20);
        User user2 = createUser("Сергей", "sergey@test.com", 30);
        userDao.addUser(user1);
        userDao.addUser(user2);
        List<User> users = userDao.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    @Order(4)
    void updateUser() {
        User user = createUser("Иван", "ivan@test.com", 25);
        userDao.addUser(user);
        user.setName("Иван Петров");
        userDao.updateUser(user);
        Optional<User> updated = userDao.getUserById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("Иван Петров", updated.get().getName());
    }

    @Test
    @Order(5)
    void deleteUserById() {
        User user = createUser("Алексей", "aleksey@test.com", 28);
        userDao.addUser(user);
        Optional<User> beforeDelete = userDao.getUserById(user.getId());
        assertTrue(beforeDelete.isPresent());
        userDao.deleteUserById(user.getId());
        Optional<User> afterDelete = userDao.getUserById(user.getId());
        assertFalse(afterDelete.isPresent());
    }

    @AfterAll
    void teardown() {
        testSessionFactory.close();
        postgreSQLContainer.stop();
    }

    private User createUser(String name, String email, int age) {
        return new User(name, email, age);
    }
}
