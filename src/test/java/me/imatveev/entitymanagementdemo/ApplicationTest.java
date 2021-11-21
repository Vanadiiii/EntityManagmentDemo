package me.imatveev.entitymanagementdemo;

import me.imatveev.entitymanagementdemo.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationTest {
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void initEntityManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("me.imatveev.entitymanagementdemo");

        Book book = Book.builder()
                .author("Miran Lipovaca 2")
                .title("Learn Haskell for great good")
                .price(BigDecimal.valueOf(1500))
                .dateIns(
                        LocalDate.of(2021, 11, 15)
                                .atStartOfDay()
                                .atZone(ZoneId.systemDefault())
                )
                .build(); //entity is TRANSIENT now

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin(); //begin transaction

        entityManager.persist(book); //entity is MANAGED now
        entityManager.flush(); //all entities were saved to database from persistent context

        entityManager.remove(book); //entity is REMOVED now (not from DB but in persistent context)
        entityManager.persist(book); //entity is MANAGED now
        book.setAuthor("Miran Lipovaca"); //changing was saved into persistent context and will appear in DB

        entityManager.getTransaction().commit(); //finish transaction


        entityManager.close(); //now all entities are DETACHED (not linked with DB)


        //merge back :-)
        entityManager = entityManagerFactory.createEntityManager(); //create new entityManager, cause old was closed
        entityManager.getTransaction().begin(); //begin new transaction
        book = entityManager.merge(book); //entity is MANAGED again
        entityManager.refresh(book); //entity is still MANAGED and reloaded from DB (!!)
        entityManager.getTransaction().commit();
        entityManager.clear(); //all entities are DETACHED now (not linked with DB)
        entityManager.close(); //now all entities are DETACHED now (not linked with DB)
    }

    @Test
    void simpleTest() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Book book = entityManager.find(Book.class, 1L);
        assertEquals(1, book.getId());
        assertEquals("Miran Lipovaca", book.getAuthor());
        assertEquals(0, BigDecimal.valueOf(1500.0).compareTo(book.getPrice()));

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
