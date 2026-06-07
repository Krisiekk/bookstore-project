package pl.kpietrzak.bookstore.notification;

import org.junit.jupiter.api.Test;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;

class EmailNotificationStrategyTest {

    @Test
    void shouldSendEmailNotification() {
        EmailNotificationStrategy strategy = new EmailNotificationStrategy();

        User user = new User();
        user.setEmail("user1@test.pl");

        Book book = new Book();
        book.setTitle("Clean Code");

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);

        strategy.sendNotificationStrategy(reservation);
    }
}