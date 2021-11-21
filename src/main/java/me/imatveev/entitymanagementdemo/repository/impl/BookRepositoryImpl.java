package me.imatveev.entitymanagementdemo.repository.impl;

import lombok.RequiredArgsConstructor;
import me.imatveev.entitymanagementdemo.entity.Book;
import me.imatveev.entitymanagementdemo.repository.BookRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(Long id) {
        entityManager.getTransaction().begin(); //create transaction
        Book book = entityManager.find(Book.class, id); //find entity by id by entityManager's method
        entityManager.getTransaction().commit(); //close(commit) transaction

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        entityManager.getTransaction().begin();

        List<Book> books = entityManager.createQuery("select book from Book book", Book.class)
                .getResultList();

        entityManager.getTransaction().commit();

        return books;
    }

    @Override
    public Book save(Book entity) {
        entityManager.getTransaction().begin();

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.getTransaction().commit();

        return entity;
    }
}
