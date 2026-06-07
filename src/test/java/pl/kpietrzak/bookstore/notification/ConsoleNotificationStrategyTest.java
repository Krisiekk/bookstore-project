package pl.kpietrzak.bookstore.notification;

import org.junit.jupiter.api.Test;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.ReservationStatus;

class ConsoleNotificationStrategyTest {

    @Test
    void shouldSendConsoleNotification() {
        ConsoleNotificationStrategy strategy = new ConsoleNotificationStrategy();

        User user = new User();
        user.setUsername("user1");

        Book book = new Book();
        book.setTitle("Clean Code");

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);

        strategy.sendNotificationStrategy(reservation);
    }
}