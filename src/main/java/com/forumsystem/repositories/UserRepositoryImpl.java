package com.forumsystem.repositories;

import com.forumsystem.modelhelpers.UserModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.repositories.contracts.UserRepository;
import com.forumsystem.еxceptions.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAll(UserModelFilterOptions userFilter) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            userFilter.getUsername().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("username like :username");
                    params.put("username", String.format("%%%s%%", value));
                }
            });

            userFilter.getEmail().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("email like :email");
                    params.put("email", String.format("%%%s%%", value));
                }
            });

            userFilter.getFirstName().ifPresent(value -> {
                if (!value.isBlank()) {
                    filters.add("firstName like :firstName");
                    params.put("firstName", String.format("%%%s%%", value));
                }
            });

            StringBuilder queryString = new StringBuilder("from User");

            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(userFilter));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }

    }

    @Override
    public User get(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null || user.isArchived()) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username= :username", User.class);
            query.setParameter("username", username);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }

            return result.get(0);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email= :email", User.class);
            query.setParameter("email", email);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("User", "email", email);
            }

            return result.get(0);
        }
    }

    @Override
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public User update(User userToUpdate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(userToUpdate);
            session.getTransaction().commit();
            return userToUpdate;
        }
    }

    @Override
    public void delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User user = get(id);
            user.setArchived(true);
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Post> getUserPosts(String username) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select p from Post p join p.createdBy u where u.username = :username and p.isArchived = false ";
            Query<Post> query = session.createQuery(hql, Post.class);
            query.setParameter("username", username);
            List<Post> userPosts = query.list();
            if (userPosts.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username, "posts");
            }
            return userPosts;
        }
    }

    @Override
    public void blockUser(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "update User set isBlocked = true where username = :username";
            Query query = session.createQuery(hql);
            query.setParameter("username", username);
            query.executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Override
    public void unblockUser(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "update User set isBlocked = false where username = :username";
            Query query = session.createQuery(hql);
            query.setParameter("username", username);
            query.executeUpdate();

            session.getTransaction().commit();
        }
    }


    @Override
    public boolean checkIfAdmin(int userId) {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT COUNT(*) FROM admins WHERE user_id = :userId";
            Number result = (Number) session.createNativeQuery(sql)
                    .setParameter("userId", userId)
                    .uniqueResult();

            return result != null && result.longValue() > 0;
        }
    }

    @Override
    public String getAdminPhoneNumber(int userId){
        try (Session session = sessionFactory.openSession()){
            String sql = "Select phone_number from admins where user_id = :userId";
            String result = (String) session.createNativeQuery(sql)
                    .setParameter("userId", userId)
                    .uniqueResult();

            if (result == null){
                throw new EntityNotFoundException("Admin", "id", String.valueOf(userId));
            }
            return result;
        }
    }

    @Override
    public long getCountUsers() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select count(u) from User u";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult();
        }
    }

    @Override
    public boolean isEmailExists(User userEmail) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "select count(u) FROM User u WHERE u.email = :email and u.id != :userId";
            Query query = session.createQuery(hql);
            query.setParameter("email", userEmail.getEmail());
            query.setParameter("userId", userEmail.getUserId());
            Long results = (Long) query.uniqueResult();

            return results > 0;
        }
    }

    @Override
    public void giveUserAdminRights(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sql = "INSERT INTO admins (user_id, phone_number) VALUES (:userId, :phoneNumber)";
            int result = session.createNativeQuery(sql)
                    .setParameter("userId", user.getUserId())
                    .setParameter("phoneNumber", 0)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Override
    public void updatePhoneNumber(String phoneNumber, int userId) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sql = "update admins set phone_number = :phoneNumber where user_id = :userid";
            int result = session.createNativeQuery(sql)
                    .setParameter("userid", userId)
                    .setParameter("phoneNumber", phoneNumber)
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(UserModelFilterOptions userFilter) {

        if (userFilter.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (userFilter.getSortBy().get()) {
            case "username":
                orderBy = "username";
                break;
            case "email":
                orderBy = "email";
                break;
            case "firstName":
                orderBy = "firstName";
                break;
            default:
                orderBy = "userId";
        }
        orderBy = String.format(" order by %s", orderBy);

        if (userFilter.getSortOrder().isPresent() &&
                userFilter.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }

}