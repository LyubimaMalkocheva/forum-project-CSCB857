package com.forumsystem.repositories;

import com.forumsystem.models.Tag;
import com.forumsystem.repositories.contracts.TagRepository;
import com.forumsystem.еxceptions.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Tag> getAll() {
        try(Session session = sessionFactory.openSession()){
            Query<Tag> query = session.createQuery("from Tag", Tag.class);
            List<Tag> result = query.list();
            return result;
        }
    }

    @Override
    public Tag getById(int tagId) {
        try(Session session = sessionFactory.openSession()){
            Tag tag = session.get(Tag.class, tagId);
            if (tag == null){
                throw new EntityNotFoundException("Tag", tagId);
            }
            return tag;
        }
    }

    @Override
    public Tag getByName(String tagName) {
        try(Session session = sessionFactory.openSession()){
            Query<Tag> query = session.createQuery("From Tag where name = :tagName", Tag.class);
            query.setParameter("tagName", tagName);
            List<Tag> result = query.list();
            if (result.isEmpty()){
                throw new EntityNotFoundException("Tag", "name", tagName);
            }
            return result.get(0);
        }
    }

    @Override
    public void create(Tag tag) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Tag tag) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            tag.setArchived(true);
            session.merge(tag);
            session.getTransaction().commit();
        }
    }
}
