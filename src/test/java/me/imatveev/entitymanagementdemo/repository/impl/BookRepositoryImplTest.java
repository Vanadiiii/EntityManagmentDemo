package me.imatveev.entitymanagementdemo.repository.impl;

import me.imatveev.entitymanagementdemo.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryImplTest {
    private EntityManagerFactory entityManagerFactory;
    private final Book book;
    private final Book book2;

    {
        book = Book.builder()
                .author("Miran Lipovaca")
                .title("Learn Haskell for great good")
                .price(BigDecimal.valueOf(1500))
                .dateIns(
                        LocalDate.of(2021, 11, 15)
                                .atStartOfDay()
                                .atZone(ZoneId.systemDefault())
                )
                .build();

        book2 = Book.builder()
                .author("Brian Goetz")
                .title("Concurrency in practice")
                .price(BigDecimal.valueOf(1300))
                .dateIns(
                        LocalDate.of(2021, 11, 15)
                                .atStartOfDay()
                                .atZone(ZoneId.systemDefault())
                )
                .build();
    }

    @BeforeEach
    void initEntityManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("me.imatveev.entitymanagementdemo");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin(); //begin transaction

        entityManager.persist(book); //entity is MANAGED now
        entityManager.persist(book2); //entity is MANAGED now
        entityManager.flush(); //all entities were saved to database from persistent context

        entityManager.getTransaction().commit(); //finish transaction

        entityManager.close(); //now all entities are DETACHED (not linked with DB)
    }

    @Test
    void findById() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BookRepositoryImpl bookRepository = new BookRepositoryImpl(entityManager);

        Book actualBook = bookRepository.findById(1L)
                .orElse(null);

        assertNotNull(actualBook);
        assertEquals(1L, actualBook.getId());
        assertEquals(book.getAuthor(), actualBook.getAuthor());
        assertEquals(0, book.getPrice().compareTo(actualBook.getPrice()));
        assertEquals(book.getTitle(), actualBook.getTitle());
        assertEquals(book.getDateIns(), actualBook.getDateIns());
    }

    @Test
    void findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BookRepositoryImpl bookRepository = new BookRepositoryImpl(entityManager);

        List<Book> books = bookRepository.findAll();

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(2, books.size());
    }

    @Test
    void save() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BookRepositoryImpl bookRepository = new BookRepositoryImpl(entityManager);

        Book savedBook = Book.builder()
                .author("Robert Lafore")
                .title("Algorithms in Java")
                .price(BigDecimal.valueOf(2000))
                .dateIns(ZonedDateTime.now())
                .build();

        bookRepository.save(savedBook);

        Book result = entityManager.createQuery("select book from Book book where book.author = 'Robert Lafore'", Book.class)
                .getSingleResult();

        assertTrue(savedBook.canEquals(result));
    }
}