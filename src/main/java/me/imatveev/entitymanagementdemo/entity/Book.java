package me.imatveev.entitymanagementdemo.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String author;

    private BigDecimal price;

    private ZonedDateTime dateIns;

    //just for tests
    public boolean canEquals(Book book) {
        if (book == null) {
            return false;
        }
        if (this == book) {
            return true;
        }

        return Objects.equals(book.getTitle(), this.getTitle())
                && Objects.equals(book.getAuthor(), this.getAuthor())
                && Objects.equals(book.getPrice(), this.getPrice())
                && Objects.equals(book.getDateIns(), this.getDateIns());
    }
}
