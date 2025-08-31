package app;

import model.User;
import dao.UserDao;
import dao.impl.UserDaoImpl;
import exceptions.app.InvalidInputException;
import exceptions.dao.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDaoImpl();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            try {
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1 -> addUser();
                    case 2 -> getUserById();
                    case 3 -> getAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> running = false;
                    default -> throw new InvalidInputException("Выбрана несуществующая опция");
                }
            } catch (InvalidInputException e) {
                System.out.println("Ошибка ввода: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Введите только числа для выбора опции");
            }
        }

        System.out.println("Программа завершена");
    }

    private static void printMenu() {
        System.out.println("\nПользовательский интерфейс");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите опцию: ");
    }

    private static void addUser() {
        try {
            System.out.print("Введите имя: ");
            String name = scanner.nextLine();
            System.out.print("Введите email: ");
            String email = scanner.nextLine();
            System.out.print("Введите возраст: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = new User(name, email, age);
            userDao.addUser(user);
            System.out.println("Пользователь успешно добавлен");
        } catch (NumberFormatException e) {
            System.out.println("Возраст должен быть числом");
        } catch (UserCreationException e) {
            System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
            logger.error("Ошибка при добавлении пользователя", e);
        }
    }

    private static void getUserById() {
        try {
            System.out.print("Введите ID пользователя: ");
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> user = userDao.getUserById(id);
            user.ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("Пользователь с таким ID не найден")
            );
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть числом");
        } catch (UserReadException e) {
            System.out.println("Ошибка при чтении пользователя: " + e.getMessage());
            logger.error("Ошибка при чтении пользователя", e);
        }
    }

    private static void getAllUsers() {
        try {
            List<User> users = userDao.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Нет пользователей в базе");
            } else {
                users.forEach(System.out::println);
            }
        } catch (UserReadException e) {
            System.out.println("Ошибка при чтении пользователей: " + e.getMessage());
            logger.error("Ошибка при чтении пользователей", e);
        }
    }

    private static void updateUser() {
        try {
            System.out.print("Введите ID пользователя для обновления: ");
            Long id = Long.parseLong(scanner.nextLine());
            Optional<User> optionalUser = userDao.getUserById(id);
            if (optionalUser.isEmpty()) {
                System.out.println("Пользователь с таким ID не найден");
                return;
            }

            User user = optionalUser.get();

            System.out.print("Новое имя (" + user.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.isBlank()) user.setName(name);

            System.out.print("Новый email (" + user.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.isBlank()) user.setEmail(email);

            System.out.print("Новый возраст (" + user.getAge() + "): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.isBlank()) user.setAge(Integer.parseInt(ageInput));

            userDao.updateUser(user);
            System.out.println("Пользователь успешно обновлён");
        } catch (NumberFormatException e) {
            System.out.println("Возраст должен быть числом");
        } catch (UserUpdateException | UserReadException e) {
            System.out.println("Ошибка при обновлении пользователя: " + e.getMessage());
            logger.error("Ошибка при обновлении пользователя", e);
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("Введите ID пользователя для удаления: ");
            Long id = Long.parseLong(scanner.nextLine());
            userDao.deleteUserById(id);
            System.out.println("Пользователь успешно удалён");
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть числом");
        } catch (UserDeletionException e) {
            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
            logger.error("Ошибка при удалении пользователя", e);
        }
    }
}
