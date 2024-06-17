package com.forumsystem.repositories;

import com.forumsystem.modelhelpers.PostModelFilterOptions;
import com.forumsystem.models.Post;
import com.forumsystem.models.User;
import com.forumsystem.models.UserLikes;
import com.forumsystem.models.UserLikesId;
import com.forumsystem.repositories.contracts.PostRepository;
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
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;
    private final UserRepository userRepository;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory, UserRepository userRepository) {
        this.sessionFactory = sessionFactory;
        this.userRepository = userRepository;
    }

    @Override
    public List<Post> getAll(PostModelFilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();
            StringBuilder queryString = new StringBuilder("from Post p left outer join p.postTags as pt");

            populateFiltersAndParams(filters, params, filterOptions, queryString);

            if (!filters.isEmpty()) {

                filters.add("p.isArchived = false");

                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            } else {
                queryString.append(" where p.isArchived = false");
            }

            queryString.append(generateOrderBy(filterOptions));

            if (!filterOptions.getSortOrder().isPresent()) {
                queryString.append(" order by p.id desc");
            }

            Query<Post> query = session.createQuery(queryString.toString(), Post.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public List<Post> getAllForAdmin(PostModelFilterOptions filterOptions) {
        try (Session session = sessionFactory.openSession()) {

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();
            StringBuilder queryString = new StringBuilder("from Post p left outer join p.postTags as pt");

            populateFiltersAndParams(filters, params, filterOptions, queryString);

            if (!filters.isEmpty()) {

                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryString.append(generateOrderBy(filterOptions));

            Query<Post> query = session.createQuery(queryString.toString(), Post.class);
            query.setProperties(params);
            return query.list();

        }
    }

    @Override
    public List<Post> getTopTenCommentedPosts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post p where p.isArchived = false " +
                    "order by p.comments desc limit 10", Post.class);
            List<Post> result = query.list();
            return result;
        }
    }

    @Override
    public List<Post> getTenNewestPosts() {

        try (Session session = sessionFactory.openSession()){

            Query<Post> query = session.createQuery("From Post p where p.isArchived = false" +
                    " order by p.id desc limit 10", Post.class);
            List <Post> result = query.list();
            return result;
        }
    }

    @Override
    public Long getPostCount() {

        try(Session session = sessionFactory.openSession()){
            Query<Long> query = session.createQuery("select count(p.postId) From Post p" +
                    " where p.isArchived = false", Long.class);
            Long postCount = query.getSingleResultOrNull();
            if (postCount == null){
                postCount = 0L;
            }
            return postCount;
        }
    }

    @Override
    public Post getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        }
    }

    @Override
    public void create(User user, Post post) {
        post.setCreatedBy(user);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();

        }
    }

    @Override
    public Post update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();

        }
        return post;
    }

    @Override
    public void delete(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void likePost(int postId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            UserLikesId userLikesId = new UserLikesId(userId, postId);
            UserLikes userLikes = session.get(UserLikes.class, userLikesId);

            if (userLikes == null) {
                userLikes = new UserLikes(userRepository.get(userId), getById(postId), true, false);
                session.persist(userLikes);
            } else {
                userLikes.setLiked(true);
                userLikes.setDisliked(false);
                session.merge(userLikes);
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void dislikePost(int postId, int userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            UserLikesId userLikesId = new UserLikesId(userId, postId);
            UserLikes userLikes = session.get(UserLikes.class, userLikesId);

            if (userLikes == null) {
                userLikes = new UserLikes(userRepository.get(userId), getById(postId), false, true);
                session.persist(userLikes);
            } else {
                userLikes.setLiked(false);
                userLikes.setDisliked(true);
                session.merge(userLikes);
            }
            session.getTransaction().commit();
        }
    }

    private void populateFiltersAndParams(List<String> filters,
                                          Map<String, Object> params,
                                          PostModelFilterOptions filterOptions,
                                          StringBuilder queryString) {
        filterOptions.getTitle().ifPresent(value -> {
            if (!value.isBlank()) {
                filters.add("title like :title");
                params.put("title", String.format("%%%s%%", value));
            }
        });

        filterOptions.getLikes().ifPresent(value -> {
            filters.add("likes >= :likes");
            params.put("likes", value);
        });

        filterOptions.getDislikes().ifPresent(value -> {
            filters.add("dislikes >= :dislikes");
            params.put("dislikes", value);
        });

        filterOptions.getTagName().ifPresent(value -> {
            if (!value.isBlank()) {
                filters.add("name like :tagName");
                params.put("tagName",  String.format("%%%s%%", value));
            }
        });
    }

    private String generateOrderBy(PostModelFilterOptions filterOptions) {

        if (filterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (filterOptions.getSortBy().get()) {
            case "title":
                orderBy = "title";
                break;
            case "likes":
                orderBy = "likes";
                break;
            case "dislikes":
                orderBy = "dislikes";
                break;
            default:
                orderBy = "p.id";
        }
        orderBy = String.format(" order by %s", orderBy);

        if (filterOptions.getSortOrder().isPresent() &&
                filterOptions.getSortOrder().get().equalsIgnoreCase("asc")) {
            orderBy = String.format("%s asc", orderBy);
        }

        if (filterOptions.getSortOrder().isPresent() &&
                filterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }
        return orderBy;
    }
}
