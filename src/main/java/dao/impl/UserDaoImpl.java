package dao.impl;

import dao.UserDao;
import exceptions.dao.UserCreationException;
import exceptions.dao.UserDeletionException;
import exceptions.dao.UserReadException;
import exceptions.dao.UserUpdateException;
import model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addUser(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            logger.info("Пользователь добавлен: {}", user);
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка при добавлении пользователя", e);
            throw new UserCreationException("Не удалось создать пользователя", e);
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (HibernateException e) {
            logger.error("Ошибка при получении пользователя с id {}", id, e);
            throw new UserReadException("Не удалось прочитать пользователя", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (HibernateException e) {
            logger.error("Ошибка при получении всех пользователей", e);
            throw new UserReadException("Не удалось прочитать пользователей", e);
        }
    }

    @Override
    public void updateUser(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
            logger.info("Пользователь обновлён: {}", user);
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка при обновлении пользователя с id {}", user.getId(), e);
            throw new UserUpdateException("Не удалось обновить пользователя", e);
        }
    }

    @Override
    public void deleteUserById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("Пользователь удалён: {}", user);
            } else {
                logger.warn("Попытка удалить несуществующего пользователя с id {}", id);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка при удалении пользователя с id {}", id, e);
            throw new UserDeletionException("Не удалось удалить пользователя", e);
        }
    }
}
